package dev.xkmc.modulargolems.compat.materials.eeeab.guardian;

import com.eeeab.eeeabsmobs.sever.entity.effect.EntityGuardianLaser;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

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
		double px = golem.getX();
		double py = golem.getY() + 1.4;
		double pz = golem.getZ();
		// duration 70 matches original GuardianShootLaserGoal; could scale with lv if desired
		int duration = 70;
		EntityGuardianLaser laser = new EntityGuardianLaser(golem.level(), golem, px, py, pz, duration);
		// damage = attackDamage / 3, as in original; scale slightly with lv
		float base = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		laser.setDamage(base / 3.0F * (1 + 0.2F * (lv - 1)));
		golem.level().addFreshEntity(laser);
		beam = laser;
		golem.getNavigation().stop();
		golem.specialAttackCoolDown = 20;
		return true;
	}

}
