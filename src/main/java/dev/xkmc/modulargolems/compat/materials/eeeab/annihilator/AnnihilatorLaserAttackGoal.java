package dev.xkmc.modulargolems.compat.materials.eeeab.annihilator;

import dev.xkmc.modulargolems.compat.materials.eeeab.EEEABProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

/**
 * 湮灭者激光的 Goal（单体），见 {@link AnnihilatorLaserModifier}。
 * 前摇：先出红外预警射线 {@code EntityInfraredRay}，之后生成 {@code EntityGuardianLaser} 造成持续光束伤害。
 */
public class AnnihilatorLaserAttackGoal extends BaseRangedAttackGoal {

	/** boss 的 LASER_ANIMATION：tick9 红外预警射线，tick49 实际激光。 */


	private Entity beam;
	private int chargeTicks = -1;
	private Entity infraredRay;

	public AnnihilatorLaserAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(AnnihilatorLaserModifier.COOLDOWN, AnnihilatorLaserModifier.MIN_RANGE, AnnihilatorLaserModifier.MAX_RANGE, golem, lv);
	}

	@Override
	public boolean canContinueToUse() {
		if (beam != null) return true;
		return super.canContinueToUse();
	}

	@Override
	public void tick() {
		// 光束实体每 tick 自行贴到傀儡头部，这里只需管朝向
		if (beam != null && beam.isRemoved()) beam = null;
		if (beam != null) {
			var target = golem.getTarget();
			if (target != null) golem.getLookControl().setLookAt(target, 30, 90);
		}
		super.tick();
	}

	@Override
	public void stop() {
		super.stop();
		chargeTicks = -1;
		if (infraredRay != null && !infraredRay.isRemoved()) infraredRay.discard();
		infraredRay = null;
	}

	@Override
	protected int getWaitTime() {
		return AnnihilatorLaserModifier.cooldownFor(lv);
	}

	protected boolean performAttack(LivingEntity target) {
		if (golem.level().isClientSide) return true;
		if (chargeTicks < 0) {
			chargeTicks = AnnihilatorLaserModifier.CHARGE_TICKS;
			return false;
		}
		if (chargeTicks > 0) {
			// EntityInfraredRay 只跟随 EntityRelicAnnihilator，故每 tick 用傀儡头部位置/朝向更新，使预警线跟随傀儡
			if (chargeTicks <= AnnihilatorLaserModifier.CHARGE_TICKS - AnnihilatorLaserModifier.WARN_TICK) {
				if (infraredRay == null || infraredRay.isRemoved()) {
					infraredRay = EEEABProxy.annihilatorInfraredRay(golem);
				} else {
					EEEABProxy.annihilatorInfraredRayUpdate(infraredRay, golem);
				}
			}
			chargeTicks--;
			return false;
		}
		chargeTicks = -1;
		if (infraredRay != null && !infraredRay.isRemoved()) infraredRay.discard();
		infraredRay = null;
		Entity laser = EEEABProxy.spawnAnnihilatorLaser(golem);
		if (laser == null) return true;
		beam = laser;
		golem.specialAttackCoolDown = 20;
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
