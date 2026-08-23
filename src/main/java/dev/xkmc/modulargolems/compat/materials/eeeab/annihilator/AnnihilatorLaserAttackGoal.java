package dev.xkmc.modulargolems.compat.materials.eeeab.annihilator;

import com.eeeab.eeeabsmobs.sever.entity.effect.EntityGuardianLaser;
import com.eeeab.eeeabsmobs.sever.entity.effect.EntityInfraredRay;
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
		// original LASER condition: distance 9..24, healthBelow 0.8, random 0.4, hasLineOfSight
		// use wait 90 (laser animation 90), range 9..24
		super(90, 9, 24, golem, lv);
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
		// spawn infrared ray as telegraph (29 ticks like original tick9)
		double x = golem.getX();
		double y = golem.getEyeY() - 0.3; // approx scope offset 0.24*height
		double z = golem.getZ();
		EntityInfraredRay ray = new EntityInfraredRay(golem.level(), golem, x, y, z, 29);
		golem.level().addFreshEntity(ray);

		// spawn guardian laser (RELIC_ANNIHILATOR type) original tick49
		EntityGuardianLaser laser = new EntityGuardianLaser(golem.level(), golem,
				golem.getX(), golem.getY(), golem.getZ(), 20);
		laser.setCountDown(1);
		EntityGuardianLaser.UserType type = EntityGuardianLaser.UserType.RELIC_ANNIHILATOR;
		laser.updateWithEntity(golem, type.wOffset, type.hOffset);
		golem.level().addFreshEntity(laser);
		beam = laser;
		golem.getNavigation().stop();
		golem.specialAttackCoolDown = 20;
		return true;
	}

}
