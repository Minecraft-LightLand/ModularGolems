package dev.xkmc.modulargolems.compat.materials.eeeab.guardian;

import dev.xkmc.modulargolems.compat.materials.eeeab.EEEABProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Ranged goal for {@link GuardianLaserModifier}.
 * Reference: {@code GuardianShootLaserGoal} and {@code HarbingerDeathBeamAttackGoal}
 * <p>
 * Original logic (decompiled):
 * <pre>
 *  double px = guardian.getX(), py = guardian.getY()+1.4, pz = guardian.getZ()
 *  EntityGuardianLaser laser = new EntityGuardianLaser(level, caster, px, py, pz, 70)
 *  laser.setDamage(attackDamage/3)
 *  level.addFreshEntity(laser)
 * </pre>
 * See: /tmp/eeeab_out/com/eeeab/eeeabsmobs/sever/entity/ai/goal/animate/GuardianShootLaserGoal.java:43-48
 * and com/eeeab/eeeabsmobs/sever/entity/effect/EntityGuardianLaser.java:76-95
 */
public class GuardianLaserAttackGoal extends BaseRangedAttackGoal {

	private Entity beam;

	public GuardianLaserAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		// wait 100, range 4..32 (matches EntityGuardianLaser.UserType.NAMELESS_GUARDIAN.beamLength = 32)
		super(100, 4, 32, golem, lv);
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
				// keep beam attached to golem eye position (mirrors HarbingerDeathBeamAttackGoal.tick)
				beam.setPosRaw(golem.getX(), golem.getEyeY(), golem.getZ());
			}
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (golem.level().isClientSide) return true;
		Entity laser = EEEABProxy.spawnGuardianLaser(golem, lv);
		if (laser == null) return true;
		beam = laser;
		golem.getNavigation().stop();
		golem.specialAttackCoolDown = 20;
		return true;
	}

}
