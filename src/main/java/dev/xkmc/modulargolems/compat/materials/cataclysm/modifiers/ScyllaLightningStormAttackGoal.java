package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class ScyllaLightningStormAttackGoal extends BaseRangedAttackGoal {


	/**
	 * 幻影水蛇的召唤节奏与斯库拉本体一致：两波，每波两只，按体型横向分开、贴着地面出现。
	 * 本体在 attackTicks 28 与 40 各召一对（phase>1 时），这里用同样的两个时间点。
	 */
	private static final int SERPENT_TICK_1 = 28, SERPENT_TICK_2 = 40;

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
				double x = golem.getX();
				double y = golem.getY() + golem.getBbHeight() * 2.8;
				double z = golem.getZ();
				if (castTicks <= ScyllaLightningStormModifier.CLOUD_TICKS) {
					// 本体 attackState 9 在 tick 56~114 每 tick 推开 5.5 格内的目标，防止贴脸
					stormKnockback();
					GolemCataProxy.scyllaThunderCloudStorm(golem, x, y, z);
					// 雷云位置的圈状闪电粒子（本体 CircleLighning，EndHeight=9）
					GolemCataProxy.scyllaThunderCloudLightning(golem, x, y, z);
					// 风暴胸甲：风暴之怒额外召唤幻影水蛇，两波各一对，和本体一致。
					// 这里必须判定「是否穿戴风暴胸甲」而不是「是否处于完整形态」：
					// 完整形态在半血以下也会成立，会把胸甲专属的召唤放给没穿胸甲的傀儡。
					// 幻影水蛇数量按生命值：2/3 以下两条，1/3 以下四条。
					if (CataArmorHelper.stormChest(golem)) {
						boolean four = CataArmorHelper.isBelow(golem, CataArmorHelper.ONE_THIRD);
						if (castTicks == SERPENT_TICK_1) {
							summonSerpents(ScyllaLightningStormModifier.SERPENT_SPREAD_1, four);
						} else if (castTicks == SERPENT_TICK_2) {
							summonSerpents(ScyllaLightningStormModifier.SERPENT_SPREAD_2, four);
						}
					}
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

	/**
	 * One pair, fanned out to either side of the golem's facing. The mux side pins each serpent to the
	 * ground and aims it at the target, so a golem that is airborne while casting still does not drop
	 * its serpents in mid air.
	 */
	private void summonSerpents(float spread, boolean four) {
		LivingEntity target = golem.getTarget();
		if (target == null || !target.isAlive()) {
			target = castTarget;
		}
		if (target == null || !target.isAlive()) return;
		float half = spread / 2.0F;
		GolemCataProxy.summonStormSerpent(golem, target, -half, 0);
		GolemCataProxy.summonStormSerpent(golem, target, half, 8);
		if (four) {
			// 1/3 生命值以下：额外一对，更靠外
			GolemCataProxy.summonStormSerpent(golem, target, -spread, 16);
			GolemCataProxy.summonStormSerpent(golem, target, spread, 24);
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
