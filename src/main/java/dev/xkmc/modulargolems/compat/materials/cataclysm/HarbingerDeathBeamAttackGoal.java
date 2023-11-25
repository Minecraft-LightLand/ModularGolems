package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.entity.projectile.Death_Laser_Beam_Entity;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

public class HarbingerDeathBeamAttackGoal extends BaseRangedAttackGoal {

	private Death_Laser_Beam_Entity beam;

	public HarbingerDeathBeamAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 4, 35, golem, lv);
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
				beam.setPosRaw(golem.getX(), golem.getEyeY(), golem.getZ());
			}
		}
		super.tick();
	}

	@Override
	protected void performAttack(LivingEntity target) {
		beam = HarbingerDeathBeamModifier.addBeam(golem);
		golem.getNavigation().stop();
	}

}

