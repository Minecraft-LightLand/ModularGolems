package dev.xkmc.modulargolems.compat.materials.eeeab.guardian;

import com.eeeab.eeeabsmobs.sever.entity.effect.EntityGuardianBlade;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.util.Mth;
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
		super(80, 3, 16, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		if (golem.level().isClientSide) return true;
		// anchor / stop navigation similar to original GuardianLobedAttackGoal.m_8037_ line 37
		golem.getNavigation().stop();
		double y = golem.getY();
		float offset = (float) Math.toRadians(golem.getRandom().nextFloat() * 360.0F - 180.0F);
		int count = 8;
		// scale blade count slightly with level if desired, but keep 8 for fidelity
		for (int i = 0; i < count; i++) {
			// original: f1 = yRot + (i + offset) * PI * 0.25
			float f1 = (float) (golem.getYRot() + (i + offset) * Math.PI * 0.25);
			double x = golem.getX() + Mth.sin(f1) * 3.0;
			double z = golem.getZ() + Mth.cos(f1) * 3.0;
			EntityGuardianBlade blade = new EntityGuardianBlade(golem.level(), golem, x, y, z, f1, true);
			golem.level().addFreshEntity(blade);
		}
		golem.specialAttackCoolDown = 20;
		return true;
	}

}
