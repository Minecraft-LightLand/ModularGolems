package dev.xkmc.modulargolems.compat.materials.eeeab.annihilator;

import dev.xkmc.modulargolems.compat.materials.eeeab.EEEABProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

/**
 * Single-target laser goal for {@link AnnihilatorLaserModifier}.
 * Spawns {@code EntityInfraredRay} (indicator) + {@code EntityGuardianLaser} (damage).
 * Mirrors {@code EntityRelicAnnihilator:1374-1388} but simplified to immediate spawn
 * (guardian laser handling already deals damage over duration).
 */
public class AnnihilatorLaserAttackGoal extends BaseRangedAttackGoal {

	private Entity beam;

	public AnnihilatorLaserAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 0, 24, golem, lv);
	}

	@Override
	public boolean canContinueToUse() {
		if (beam != null) return true;
		return super.canContinueToUse();
	}

	@Override
	public void tick() {
		// the beam entity places itself on the golem's head mount every tick, so there is nothing to
		// correct here; only the facing is the goal's business
		if (beam != null && beam.isRemoved()) beam = null;
		if (beam != null) {
			var target = golem.getTarget();
			if (target != null) golem.getLookControl().setLookAt(target, 30, 90);
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (golem.level().isClientSide) return true;
		Entity laser = EEEABProxy.spawnAnnihilatorLaser(golem);
		if (laser == null) return true;
		beam = laser;
		golem.getNavigation().stop();
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
