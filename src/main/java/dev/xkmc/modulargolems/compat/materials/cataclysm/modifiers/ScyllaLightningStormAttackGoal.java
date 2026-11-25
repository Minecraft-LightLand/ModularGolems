package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class ScyllaLightningStormAttackGoal extends BaseRangedAttackGoal {


	private LivingEntity castTarget;
	private int castTicks;
	private boolean casting;

	public ScyllaLightningStormAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(ScyllaLightningStormModifier.COOLDOWN, ScyllaLightningStormModifier.MIN_RANGE, ScyllaLightningStormModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public boolean canUse() {
		// 风暴之怒只在完整形态（半血以下，或被风暴胸甲钉住）可用
		return CataArmorHelper.stormFinalForm(golem) && super.canUse();
	}

	@Override
	public void start() {
		super.start();
		casting = false;
		castTarget = null;
		castTicks = 0;
	}

	@Override
	public void stop() {
		super.stop();
		casting = false;
		castTarget = null;
	}

	@Override
	public boolean canContinueToUse() {
		return casting || super.canContinueToUse();
	}
	@Override
	public void tick() {
		if (casting) {
			if (golem.isRemoved()) {
				casting = false;
				castTarget = null;
			} else {
				castTicks++;
				if (castTicks % 3 == 0) GolemCataProxy.scyllaStormParticles(golem);
				// 幻影水蛇属于风暴之怒本体，起手特效与召唤始终按时序推进；
				// 召唤数量只看生命值（2/3 以下一对、1/3 以下再来一对）。
				if (castTicks <= ScyllaLightningStormModifier.CLOUD_TICKS) {
					serpentPhase();
				}
				double x = golem.getX();
				double y = golem.getY() + golem.getBbHeight() * 2.8;
				double z = golem.getZ();
				if (castTicks <= ScyllaLightningStormModifier.CLOUD_TICKS) {
					// 本体 attackState 9 在 tick 56~114 每 tick 推开 5.5 格内的目标，防止贴脸
					stormKnockback();
					GolemCataProxy.scyllaThunderCloudStorm(golem, x, y, z);
					// 雷云位置的圈状闪电粒子（本体 CircleLighning，EndHeight=9）
					GolemCataProxy.scyllaThunderCloudLightning(golem, x, y, z);
				} else if (castTicks == ScyllaLightningStormModifier.CLOUD_TICKS + 1) {
					// 本体 attackState 9 tick 114：雷云收束时的 SUPER_LIGHTNING + 闪电爆裂
					GolemCataProxy.scyllaThunderCloudBurst(golem);
				} else if (castTicks <= ScyllaLightningStormModifier.CLOUD_TICKS + ScyllaLightningStormModifier.SPEAR_TICKS) {
					LivingEntity target = golem.getTarget();
					if (target == null || target.isRemoved()) {
						target = castTarget;
					}
					if (target != null && !target.isRemoved()) {
						GolemCataProxy.scyllaThunderCloudSpear(golem, target, x, y, z);
					}
				} else {
					casting = false;
					castTarget = null;
				}
			}
		}
		super.tick();
	}

	/**
	 * 幻影水蛇起手，时序对齐本体 {@code SummonSnake}（attackState 26）：
	 * tick 1~19 水粒子向本体汇聚、tick 28 召第一对、tick 30 蓝色冲击环 + 咆哮、
	 * tick 33/36/39 再补声波、tick 40 召第二对、31~39 雷暴光球。
	 * <p>
	 * 召唤数量走「生命值门槛」，与本体一致：≤2/3 一对（2 条）、≤1/3 再来一对（4 条）。
	 * 风暴胸甲的「无视生命值要求」会把这两道门槛直接跳过（{@code stormChest} 即视为达标），
	 * 所以穿胸甲时满血也会召满 4 条 —— 这正是胸甲那条 {@code SKILL_IGNORE_HEALTH} 的语义。
	 */
	private void serpentPhase() {
		GolemCataProxy.scyllaSummonGather(golem, castTicks);
		if (castTicks == SERPENT_TICK_1) {
			// 本体 tick 28：phase 1（≤2/3）召一对
			if (CataArmorHelper.stormFinalFormAt(golem, CataArmorHelper.TWO_THIRDS)) {
				summonSerpents(ScyllaLightningStormModifier.SERPENT_SPREAD_1);
			}
		} else if (castTicks == SERPENT_TICK_2) {
			// 本体 tick 40：phase 2（≤1/3）再来一对，共 4 条
			if (CataArmorHelper.stormFinalFormAt(golem, CataArmorHelper.ONE_THIRD)) {
				summonSerpents(ScyllaLightningStormModifier.SERPENT_SPREAD_2);
			}
		}
		if (castTicks == ScyllaLightningStormModifier.SERPENT_RING_TICK) {
			GolemCataProxy.scyllaSummonRing(golem);
			GolemCataProxy.scyllaSummonRoar(golem);
		}
		for (int i = 1; i <= ScyllaLightningStormModifier.SERPENT_ROAR_COUNT; i++) {
			if (castTicks == ScyllaLightningStormModifier.SERPENT_RING_TICK + i * ScyllaLightningStormModifier.SERPENT_ROAR_GAP) {
				GolemCataProxy.scyllaSummonRoar(golem);
			}
		}
		if (castTicks > ScyllaLightningStormModifier.SERPENT_RING_TICK
				&& castTicks < ScyllaLightningStormModifier.SERPENT_ORB_END) {
			GolemCataProxy.scyllaStormParticles(golem);
		}
	}

	/**
	 * 一对水蛇，沿朝向的左右两侧分开（本体 {@code dis}）。mux 侧把每条蛇钉在地面并朝向目标，
	 * 所以腾空施法时也不会把蛇丢到半空。
	 */
	private void summonSerpents(float spread) {
		LivingEntity target = golem.getTarget();
		if (target == null || target.isRemoved()) target = castTarget;
		if (target == null || target.isRemoved()) return;
		float half = spread / 2.0F;
		// 风暴胸甲按倍率放大落点伤害半径（落点位置不变）
		double area = CataArmorHelper.serpentAreaScale(golem);
		GolemCataProxy.summonStormSerpent(golem, target, -half, 0, area);
		GolemCataProxy.summonStormSerpent(golem, target, half, 8, area);
	}

	/** 本体 {@code SummonSnake} 的召唤帧：attackTicks 28 / 40。 */
	private static final int SERPENT_TICK_1 = 28, SERPENT_TICK_2 = 40;

	/**
	 * 复刻本体 {@code Scylla_Entity.Stormknockback(0.7F, 5.5D)}：把作用半径内可攻击的目标
	 * 沿径向推开，避免它们贴到脸上。本体按距离平方反比给推力，这里把距离下限压到
	 * {@link ScyllaLightningStormModifier#STORM_KNOCKBACK_MIN_DIST} 格，免得贴脸时把人弹飞上天。
	 */
	private void stormKnockback() {
		if (golem.level().isClientSide()) return;
		double radius = ScyllaLightningStormModifier.STORM_KNOCKBACK_RADIUS;
		for (LivingEntity e : golem.level().getEntitiesOfClass(LivingEntity.class,
				golem.getBoundingBox().inflate(radius))) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			double dx = e.getX() - golem.getX();
			double dz = e.getZ() - golem.getZ();
			double d2 = Math.max(dx * dx + dz * dz,
					ScyllaLightningStormModifier.STORM_KNOCKBACK_MIN_DIST * ScyllaLightningStormModifier.STORM_KNOCKBACK_MIN_DIST);
			double power = e.isShiftKeyDown()
					? ScyllaLightningStormModifier.STORM_KNOCKBACK / 3
					: ScyllaLightningStormModifier.STORM_KNOCKBACK;
			e.push(dx / d2 * power, 0.0, dz / d2 * power);
		}
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (casting) return false;
		castTarget = target;
		castTicks = 0;
		casting = true;
		return true;
	}

	@Override
	protected BackstepHelper backstep() {
		return new BackstepHelper(golem, BackstepHelper.DEFAULT_DISTANCE, BackstepHelper.DEFAULT_HEIGHT);
	}


	@Override
	protected double getLeapRange() {
		return BackstepHelper.DEFAULT_LEAP_RANGE;
	}
}
