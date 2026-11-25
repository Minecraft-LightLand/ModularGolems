package dev.xkmc.modulargolems.content.modifier.special;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.targeting.TargetManager;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 连续发射一串弹幕的远程技能。弹幕会分摊到附近的目标上；
 * 范围内只有一个目标时，每一发都打向它。
 * <p>
 * 子类只需提供 {@link #fire}，齐射数量不同时再覆写 {@link #getBurstCount}；
 * 发射数量、间隔以及每一发的锁定目标都由这里统一处理。
 * <p>
 * 需要"有几个目标打几发"（原 {@code MultiTargetRangedGoal} 的行为）时，在构造器里调一次
 * {@link #useMultiTargetSemantics()} 即可。
 */
public abstract class BurstRangedGoal extends BaseRangedAttackGoal {

	private static final int BURST_DELAY = 5;
	private static final float[] LATERALS = {0.0F, -0.5F, 0.5F};

	private final int farRange;
	private int delay = -1;
	private int shot;
	private boolean multiTarget;
	private final List<Pair<LivingEntity, Float>> volley = new ArrayList<>();
	private final List<LivingEntity> targets = new ArrayList<>();

	protected BurstRangedGoal(int near, int far, AbstractGolemEntity<?, ?> golem, int lv) {
		this(200 >> (lv - 1), near, far, golem, lv);
	}

	protected BurstRangedGoal(int cooldown, int near, int far, AbstractGolemEntity<?, ?> golem, int lv) {
		super(cooldown, near, far, golem, lv);
		this.farRange = far;
	}

	/**
	 * 切到原 {@code MultiTargetRangedGoal} 的语义：有几个目标打几发、按优先级索敌、要求视线、
	 * 每 tick 一发（{@code getBurstDelay() == 0} 时不是一次性打完）。构造器里调一次即可。
	 */
	protected void useMultiTargetSemantics() {
		this.multiTarget = true;
	}

	/** 一轮齐射总共打几发。 */
	protected int getBurstCount() {
		return 3;
	}

	/**
	 * 齐射中两发之间的间隔（tick）。第一发总是立刻射出。
	 * <p>
	 * {@code 1} 表示第一发之后其余的同时打出（鸣雷槊/沧波棱刺的"首发领射"）；
	 * 更大的值按该间隔一发一发射出；{@code 0} 的含义见 {@link #fireAllAtOnce()}。
	 */
	protected int getBurstDelay() {
		return BURST_DELAY;
	}

	/**
	 * {@code getBurstDelay() <= 0} 时是否把整轮齐射一次性打完。
	 * <p>
	 * 默认 {@code true}（湮灭箭那种"一轮瞬间倾泻"）；{@code false} 表示每 tick 一发，
	 * 对应原 {@code MultiTargetRangedGoal} 里 {@code cd() == 0} 的逐 tick 连发语义。
	 */
	protected boolean fireAllAtOnce() {
		return !multiTarget && getBurstDelay() <= 0;
	}

	/**
	 * 目标不足 {@link #getBurstCount()} 个时是否重复打同一个目标。
	 * <p>
	 * 默认 {@code true}：齐射数量不变，目标轮换使用（分摊弹幕的常用行为）。
	 * {@code false}：有几个目标就打几发——即原 {@code MultiTargetRangedGoal} 的语义。
	 */
	protected boolean repeatTargets() {
		return !multiTarget;
	}

	/** 索敌半径（格）。默认沿用构造时给的 {@code far}。 */
	protected double searchRange() {
		return farRange;
	}

	/** 索敌是否要求视线（{@code hasLineOfSight}）。 */
	protected boolean requireLineOfSight() {
		return multiTarget;
	}

	/**
	 * 索敌排序方式。{@code false} 按距离由近到远（默认）；{@code true} 按
	 * {@link TargetManager#predicateTarget} 的优先级（{@code FORCED} 等优先）。
	 */
	protected boolean sortByPriority() {
		return multiTarget;
	}

	/**
	 * 是否给多发弹幕加横向偏移（{@link #LATERALS}），避免连续几发在目标处重叠。
	 * {@code false} 时 {@code fire} 收到的 {@code lateral} 恒为 0。
	 */
	protected boolean useLateral() {
		return !multiTarget;
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (delay < 0) {
			volley.clear();
			buildVolley(target);
			return fireBurst();
		}
		delay--;
		if (delay > 0) {
			return false;
		}
		// 攻击前的延迟已经走完，从这里开始由齐射本身的节奏驱动
		return continueBurst();
	}

	private boolean fireBurst() {
		if (volley.isEmpty()) {
			finishVolley();
			return true;
		}
		int gap = getBurstDelay();
		if (gap <= 0 && fireAllAtOnce()) {
			for (var p : volley) {
				fire(GolemTargetUtil.resolve(golem, p.getFirst()), p.getSecond());
			}
			finishVolley();
			return true;
		}
		shot = 1;
		fire(GolemTargetUtil.resolve(golem, volley.get(0).getFirst()), volley.get(0).getSecond());
		// 齐射只有一发：本轮到此为止。少了这一步，continueBurst 会按 {@code shot % volley.size()}
		// 再把同一个目标补打一次，表现为"一次施放连打两轮"。
		if (shot >= volley.size()) {
			finishVolley();
			return true;
		}
		if (gap == 1 && !multiTarget) {
			// 间隔只有 1 tick 时剩下的弹幕本来也会同时打出，仍走错峰路径
			delay = BURST_DELAY;
			return false;
		}
		// 多目标语义下原 cd() 是"倒数到 0 之后的下一个 tick 才开火"，实际间隔为 cd()+1
		delay = Math.max(0, gap) + (multiTarget ? 1 : 0);
		return false;
	}

	private boolean continueBurst() {
		if (volley.isEmpty()) {
			finishVolley();
			return true;
		}
		if (getBurstDelay() == 1 && !multiTarget) {
			// 只有第一发被压后，其余的同时跟上
			for (int i = shot; i < volley.size(); i++) {
				var p = volley.get(i);
				fire(GolemTargetUtil.resolve(golem, p.getFirst()), p.getSecond());
			}
			finishVolley();
			return true;
		}
		// 每个间隔放出一发，目标列表循环使用，只有一个目标时也能持续命中
		var p = volley.get(shot % volley.size());
		fire(GolemTargetUtil.resolve(golem, p.getFirst()), p.getSecond());
		shot++;
		if (shot >= volley.size()) {
			finishVolley();
			return true;
		}
		delay = Math.max(0, getBurstDelay()) + (multiTarget ? 1 : 0);
		return false;
	}

	/**
	 * 收集本轮齐射要打的目标。默认：主目标 + 按距离由近到远补足；
	 * 需要优先级/视线过滤时覆写 {@link #sortByPriority()} / {@link #requireLineOfSight()}。
	 */
	protected void collectTargets(LivingEntity main, int count, List<LivingEntity> out) {
		out.clear();
		double range = searchRange();
		if (!sortByPriority() && !requireLineOfSight()) {
			GolemTargetUtil.collect(golem, main, range, count, out);
			return;
		}
		List<LivingEntity> list = new ArrayList<>();
		AABB aabb = golem.getBoundingBox().inflate(range);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			if (requireLineOfSight() && !golem.hasLineOfSight(e)) continue;
			list.add(e);
		}
		if (sortByPriority()) {
			list.sort(Comparator.comparing(ke -> Optional.ofNullable(
					TargetManager.predicateTarget(golem, ke)
			).map(Enum::ordinal).orElse(100)));
		} else {
			list.sort(Comparator.comparingDouble(golem::distanceToSqr));
		}
		for (int i = 0; i < Math.min(count, list.size()); i++) {
			out.add(list.get(i));
		}
	}

	private void buildVolley(LivingEntity target) {
		int count = getBurstCount();
		collectTargets(target, count, targets);
		if (targets.isEmpty()) return;
		int n = targets.size();
		int shots = repeatTargets() ? count : Math.min(count, n);
		for (int i = 0; i < shots; i++) {
			LivingEntity t = targets.get(i % n);
			int seen = 0;
			for (int j = 0; j < i; j++) {
				if (volley.get(j).getFirst() == t) seen++;
			}
			volley.add(Pair.of(t, useLateral() ? LATERALS[seen % LATERALS.length] : 0.0F));
		}
	}

	/**
	 * 一轮齐射正常打完：把状态清回初始值。
	 * <p>
	 * 关键是把 {@code delay} 归位到 -1 —— {@link #performAttack} 只有看到 {@code delay < 0} 才会
	 * 重建齐射。原先打完时 {@code delay} 停在 0，于是下一轮走的是「继续齐射」分支、只补射一发，
	 * 表现为 3 发 / 1 发 / 3 发 交替。
	 */
	private void finishVolley() {
		delay = -1;
		shot = 0;
		volley.clear();
	}

	protected abstract void fire(LivingEntity target, float lateral);

	@Override
	public void stop() {
		delay = -1;
		shot = 0;
		volley.clear();
		super.stop();
	}

}
