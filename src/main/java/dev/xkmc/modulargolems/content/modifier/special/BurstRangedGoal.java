package dev.xkmc.modulargolems.content.modifier.special;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 连续发射一串弹幕的远程技能。弹幕会分摊到附近的目标上；
 * 范围内只有一个目标时，每一发都打向它。
 *
 * 子类只需提供 {@link #fire}，齐射数量不同时再覆写 {@link #getBurstCount}；
 * 发射数量、间隔以及每一发的锁定目标都由这里统一处理。
 */
public abstract class BurstRangedGoal extends BaseRangedAttackGoal {

	private static final int BURST_DELAY = 5;
	private static final float[] LATERALS = {0.0F, -0.5F, 0.5F};

	private final int farRange;
	private int delay = -1;
	private int shot;
	private final List<Pair<LivingEntity, Float>> volley = new ArrayList<>();
	private final List<LivingEntity> targets = new ArrayList<>();

	protected BurstRangedGoal(int near, int far, AbstractGolemEntity<?, ?> golem, int lv) {
		this(200 >> (lv - 1), near, far, golem, lv);
	}

	protected BurstRangedGoal(int cooldown, int near, int far, AbstractGolemEntity<?, ?> golem, int lv) {
		super(cooldown, near, far, golem, lv);
		this.farRange = far;
	}

	/** 一次齐射包含多少发 */
	protected int getBurstCount() {
		return 3;
	}

	/**
	 * 齐射中两发之间的间隔（tick）。第一发总是立刻射出。
	 * 0 表示整轮齐射一次性打完；1 表示第一发之后剩下的同时打出；
	 * 更大的值则按该间隔一发一发射出。
	 */
	protected int getBurstDelay() {
		return BURST_DELAY;
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
		int gap = getBurstDelay();
		if (gap <= 0) {
			for (var p : volley) {
				fire(GolemTargetUtil.resolve(golem, p.getFirst()), p.getSecond());
			}
			return true;
		}
		shot = 1;
		fire(volley.get(0).getFirst(), volley.get(0).getSecond());
		if (gap == 1) {
			// 间隔只有 1 tick 时剩下的弹幕本来也会同时打出，仍走错峰路径
			delay = BURST_DELAY;
			return false;
		}
		delay = gap;
		return false;
	}

	private boolean continueBurst() {
		if (getBurstDelay() <= 1) {
			// 只有第一发被压后，其余的同时跟上
			for (int i = shot; i < volley.size(); i++) {
				var p = volley.get(i);
				fire(GolemTargetUtil.resolve(golem, p.getFirst()), p.getSecond());
			}
			return true;
		}
		// 每个间隔放出一发，目标列表循环使用，只有一个目标时也能持续命中
		var p = volley.get(shot % volley.size());
		fire(GolemTargetUtil.resolve(golem, p.getFirst()), p.getSecond());
		shot++;
		if (shot >= volley.size()) {
			return true;
		}
		delay = getBurstDelay();
		return false;
	}

	private void buildVolley(LivingEntity target) {
		int count = getBurstCount();
		GolemTargetUtil.collect(golem, target, farRange, count, targets);
		for (int i = 0; i < count; i++) {
			int seen = 0;
			for (int j = 0; j < i; j++) {
				if (volley.get(j).getFirst() == targets.get(i % targets.size())) seen++;
			}
			volley.add(Pair.of(targets.get(i % targets.size()), LATERALS[seen % LATERALS.length]));
		}
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
