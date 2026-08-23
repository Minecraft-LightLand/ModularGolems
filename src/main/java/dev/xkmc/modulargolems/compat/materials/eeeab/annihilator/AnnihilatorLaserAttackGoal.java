package dev.xkmc.modulargolems.compat.materials.eeeab.annihilator;

import dev.xkmc.modulargolems.compat.materials.eeeab.EEEABProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Single-target laser goal for {@link AnnihilatorLaserModifier}.
 * Spawns {@code EntityInfraredRay} (indicator) + {@code EntityGuardianLaser} (damage).
 * Mirrors {@code EntityRelicAnnihilator:1374-1388} but simplified to immediate spawn
 * (guardian laser handling already deals damage over duration).
 */
public class AnnihilatorLaserAttackGoal extends BaseRangedAttackGoal {

	private Entity beam;

	public AnnihilatorLaserAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 2, 24, golem, lv);
	}

	@Override
	public boolean canContinueToUse() {
		if (beam != null) return true;
		return super.canContinueToUse();
	}

	@Override
	public void tick() {
		if (beam != null) {
			if (beam.isRemoved()) {
				beam = null;
			} else {
				var target = golem.getTarget();
				if (target != null) {
					golem.getLookControl().setLookAt(target, 30, 90);
				}
				beam.setPosRaw(golem.getX(), golem.getEyeY(), golem.getZ());
			}
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

}
