package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.targeting.TargetManager;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTypeTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 沙暴群组的持有者。沙暴要在攻击冷却之外一直跟着傀儡，所以状态存在傀儡的瞬态字段里而不是绑在
 * Goal 上：没有目标时 Goal 会 stop，沙暴必须留在场上。
 * <p>
 * 沙暴停在傀儡身边时排成三层同心环（半径 4/8/12，每环三个），和原本近战模式一致。下标越靠后
 * 半径越大，所以派出去追人的永远是最外圈的那几个，留下来守着的自然占住内圈。
 */
public class AncientRemnantSandstormGroup {

	private static final String KEY = "cataclysm_sandstorm_group";

	private static final int LIFESPAN = 300;
	private static final float BASE_RADIUS = 6.0F;
	private static final float SPEED = 0.04F;

	/** 远古金属头盔把沙暴移速放大到 {@link #FAST_MULT} 倍，这里返回百分比（50 表示 +50%）。 */
	public static int helmetSpeedPercent() {
		return Math.round((FAST_MULT - 1) * 100);
	}
	/**
	 * 远古金属头盔：沙暴移速倍率。
	 */
	private static final float FAST_MULT = 1.5F;
	private static final int SEARCH_RANGE = 35;

	private final List<Entity> storms = new ArrayList<>();
	/**
	 * 与 storms 同下标的当前目标，null 表示这个沙暴留在傀儡身边环绕。刚召唤出来的那一帧是 null，
	 * 让它们先散成环再派出去。
	 */
	private final List<LivingEntity> assigned = new ArrayList<>();

	public static AncientRemnantSandstormGroup of(AbstractGolemEntity<?, ?> golem) {
		AncientRemnantSandstormGroup group = golem.getTransientState(KEY);
		if (group == null) {
			group = new AncientRemnantSandstormGroup();
			golem.setTransientState(KEY, group);
		}
		return group;
	}

	public static void clear(AbstractGolemEntity<?, ?> golem) {
		golem.setTransientState(KEY, null);
	}

	/**
	 * 按等级维持沙暴数量，lv1/2/3 对应 3/6/9 个。句柄失效（沙暴自然消亡或区块重载）后重新接管，
	 * 仍然不足才补召。远古金属头盔让沙暴移动更快。
	 */
	public void maintain(AbstractGolemEntity<?, ?> golem, int lv) {
		int want = lv * 3;
		dropDead();
		if (storms.isEmpty() && !assigned.isEmpty()) reacquire(golem);
		if (storms.size() >= want) return;
		int missing = want - storms.size();
		float speed = CataArmorHelper.ancientHelmet(golem) ? SPEED * FAST_MULT : SPEED;
		var added = GolemCataProxy.spawnSandstormGroup(golem, missing, LIFESPAN, BASE_RADIUS, speed);
		storms.addAll(added);
		for (int i = 0; i < added.size(); i++) assigned.add(null);
	}

	private void dropDead() {
		for (int i = storms.size() - 1; i >= 0; i--) {
			var e = storms.get(i);
			if (e == null || e.isRemoved() || !e.isAlive()) {
				storms.remove(i);
				assigned.remove(i);
			}
		}
	}

	/**
	 * 重新接管群组，用于句柄在区块卸载后失效的情况。
	 */
	public void reacquire(AbstractGolemEntity<?, ?> golem) {
		List<Entity> found = GolemCataProxy.getSandstorms(golem);
		if (found.isEmpty()) return;
		storms.clear();
		assigned.clear();
		storms.addAll(found);
		for (int i = 0; i < found.size(); i++) assigned.add(null);
	}

	/**
	 * 每帧的便宜校验：只检查已在追踪的目标是否还有效，不扫全场。目标失效时立刻让沙暴脱离
	 * 并归位，不用等下一次全量重扫。
	 */
	public void track(AbstractGolemEntity<?, ?> golem) {
		for (int i = 0; i < assigned.size(); i++) {
			var t = assigned.get(i);
			if (t == null) continue;
			if (GolemTargetUtil.canTarget(golem, t)) continue;
			assigned.set(i, null);
			if (i < storms.size()) GolemCataProxy.setSandstormOrder(storms.get(i), i, null);
		}
	}

	/**
	 * 分配目标。从最外圈开始派，内圈的留守。
	 */
	public void assign(AbstractGolemEntity<?, ?> golem) {
		if (storms.isEmpty()) return;
		for (int i = 0; i < assigned.size(); i++) {
			var t = assigned.get(i);
			if (t != null && !GolemTargetUtil.canTarget(golem, t)) {
				assigned.set(i, null);
			}
		}
		Set<LivingEntity> taken = new LinkedHashSet<>();
		for (var t : assigned) {
			if (t != null) taken.add(t);
		}
		List<LivingEntity> free = new ArrayList<>();
		for (var t : collectTargets(golem)) {
			if (!taken.contains(t)) free.add(t);
		}
		// 外圈优先出击：从后往前填
		int next = 0;
		for (int i = storms.size() - 1; i >= 0 && next < free.size(); i--) {
			if (assigned.get(i) == null) assigned.set(i, free.get(next++));
		}
		for (int i = 0; i < storms.size(); i++) {
			GolemCataProxy.setSandstormOrder(storms.get(i), i, assigned.get(i));
		}
	}

	private List<LivingEntity> collectTargets(AbstractGolemEntity<?, ?> golem) {
		var list = golem.level().getEntities(
				EntityTypeTest.forClass(LivingEntity.class),
				golem.getBoundingBox().inflate(SEARCH_RANGE),
				e -> GolemTargetUtil.canTarget(golem, e));
		// 主目标永远排最前，其余按傀儡自己的目标优先级排
		var main = golem.getTarget();
		list.sort(Comparator.comparing((LivingEntity e) -> e == main ? 0 : 1)
				.thenComparing(e -> Optional.ofNullable(
						TargetManager.predicateTarget(golem, e)
				).map(Enum::ordinal).orElse(100)));
		List<LivingEntity> ans = new ArrayList<>();
		for (var e : list) {
			if (ans.size() >= storms.size()) break;
			ans.add(e);
		}
		return ans;
	}

}
