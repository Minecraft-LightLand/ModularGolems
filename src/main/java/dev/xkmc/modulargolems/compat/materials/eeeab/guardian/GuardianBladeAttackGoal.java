package dev.xkmc.modulargolems.compat.materials.eeeab.guardian;

import dev.xkmc.modulargolems.compat.materials.eeeab.EEEABProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

/**
 * Ranged goal for {@link GuardianBladeModifier}.
 * Reference: {@code GuardianLobedAttackGoal#doSpawnBlade()} and
 * {@code HarbingerHomingMissileAttackGoal}, {@code ManaBurstAttackGoal}, {@code IgnisFireballAttackGoal}
 * <p>
 * Original logic (decompiled):
 * <pre>
 *  float offset = toRadians(random*360-180)
 *  for i in 0..7:
 *    f1 = yRot + (i+offset)*PI*0.25
 *    x = gx + sin(f1)*3.0, z = gz + cos(f1)*3.0
 *    blade = new EntityGuardianBlade(level, caster, x, y, z, f1, true)
 *    level.addFreshEntity(blade)
 * </pre>
 * See: /tmp/eeeab_out/com/eeeab/eeeabsmobs/sever/entity/ai/goal/animate/GuardianLobedAttackGoal.java:59-70
 */
public class GuardianBladeAttackGoal extends BaseRangedAttackGoal {

	public GuardianBladeAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		// wait 80 ticks, attack within 3..16 blocks (close-mid range, matches original smash range 25 sqr)
		super(160, 3, 16, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (golem.level().isClientSide) return true;
		golem.getNavigation().stop();
		EEEABProxy.spawnGuardianBladeBurst(golem);
		golem.specialAttackCoolDown = 20;
		return true;
	}

}
