package dev.xkmc.modulargolems.compat.materials.cataclysm.armor;

import dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers.EnderGuardianVoidRuneAttackGoal;
import dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers.EnderGuardianTeleportSlamModifier;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * 末影守卫护腿的强化场：在技能召唤出的虚空涡流周围再叠一层更大、更强、带伤害的拉扯场。
 * <p>
 * 本体的 {@code Void_Vortex_Entity} 把拉扯力度（0.075）、范围（±3，高 15）和爆炸威力（2.0）
 * 全部写死在实体内部，没法从外部调参，所以在 MG 侧另加一层：技能召唤涡流时登记一个位置，
 * 之后每 tick 由 {@link dev.xkmc.modulargolems.compat.materials.cataclysm.CataEventHandler}
 * 驱动，把这层强化场结算掉。
 * <p>
 * 只有穿戴末影守卫护腿时才会登记；位置用持久化数据以字符串存，最多同时保留
 * {@link #MAX_FIELDS} 个，避免长时间战斗时无限增长。
 */
public class VortexField {

	private static final String KEY = "GuardianVortexField";
	private static final int MAX_FIELDS = 4;

	/** 涡流存活时长（tick），与本体 spawnVortex 传给实体的 150 一致。 */
	public static final int LIFETIME = 150;
	/** 强化场的吸力和范围相对本体虚空涡流放大的比例（0.6 = +60%）。tooltip 直接显示这个值。 */
	public static final double BONUS = 0.6;
	/** 强化场半径（格）：本体拉扯范围是 ±3。 */
	public static final double RADIUS = 3.0 * (1 + BONUS);
	/** 强化场高度（格）：本体是 15。 */
	public static final double HEIGHT = 15.0;
	/** 每 tick 额外向内的拉扯力度：本体是 0.075。 */
	public static final double PULL = 0.075 * (1 + BONUS);
	/** 额外向下的拉扯力度：本体是 -2，这里只补一点，主要靠本体的主力。 */
	public static final double PULL_DOWN = -0.6;
	/** 伤害间隔（tick）。 */
	public static final int DAMAGE_INTERVAL = 20;

	/** 由召唤涡流的技能调用：登记一个强化场。 */
	public static void add(AbstractGolemEntity<?, ?> golem, Vec3 pos) {
		if (golem.level().isClientSide()) return;
		if (!CataArmorHelper.guardianLegs(golem)) return;
		var data = golem.getPersistentData();
		List<String> list = read(data.getString(KEY));
		long end = golem.level().getGameTime() + LIFETIME;
		list.add(pos.x + "," + pos.y + "," + pos.z + "," + end);
		while (list.size() > MAX_FIELDS) list.remove(0);
		data.putString(KEY, String.join(";", list));
	}

	/** 每 tick 结算所有强化场，顺便清掉过期的。 */
	public static void tick(AbstractGolemEntity<?, ?> golem) {
		if (golem.level().isClientSide()) return;
		var data = golem.getPersistentData();
		String raw = data.getString(KEY);
		if (raw.isEmpty()) return;
		long now = golem.level().getGameTime();
		List<String> keep = new ArrayList<>();
		for (String s : raw.split(";")) {
			double[] p = parse(s);
			if (p == null) continue;
			if (now >= (long) p[3]) continue;
			pull(golem, p, now);
			keep.add(s);
		}
		if (keep.isEmpty()) data.remove(KEY);
		else data.putString(KEY, String.join(";", keep));
	}

	private static void pull(AbstractGolemEntity<?, ?> golem, double[] p, long now) {
		Vec3 center = new Vec3(p[0], p[1], p[2]);
		AABB box = new AABB(center.x - RADIUS, center.y, center.z - RADIUS,
				center.x + RADIUS, center.y + HEIGHT, center.z + RADIUS);
		boolean hurt = now % DAMAGE_INTERVAL == 0;
		float dmg = 0;
		if (hurt) {
			// 每秒伤害 = 攻击力 × 护腿加成 × 0.5，让 tooltip 里的百分比有意义
			double atk = golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
			dmg = (float) (atk * MGConfig.COMMON.gravityChargeArmorBonus.get() * 0.5);
		}
		for (LivingEntity e : golem.level().getEntitiesOfClass(LivingEntity.class, box)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			Vec3 diff = e.position().subtract(center);
			double len = diff.length();
			if (len < 1.0E-4) {
				e.push(0, PULL_DOWN, 0);
			} else {
				Vec3 dir = diff.normalize().scale(PULL);
				e.push(-dir.x, PULL_DOWN, -dir.z);
			}
			if (hurt && dmg > 0) e.hurt(golem.damageSources().mobAttack(golem), dmg);
		}
	}

	private static List<String> read(String raw) {
		List<String> list = new ArrayList<>();
		if (raw.isEmpty()) return list;
		for (String s : raw.split(";")) {
			if (parse(s) != null) list.add(s);
		}
		return list;
	}

	private static double[] parse(String s) {
		String[] parts = s.split(",");
		if (parts.length != 4) return null;
		try {
			return new double[]{
					Double.parseDouble(parts[0]),
					Double.parseDouble(parts[1]),
					Double.parseDouble(parts[2]),
					Long.parseLong(parts[3])};
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private VortexField() {
	}
}
