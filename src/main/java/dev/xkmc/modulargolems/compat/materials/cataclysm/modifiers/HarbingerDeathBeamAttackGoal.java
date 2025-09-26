package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.CataclysmProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class HarbingerDeathBeamAttackGoal extends BaseRangedAttackGoal {

	private Entity beam;

	public HarbingerDeathBeamAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 2, 35, golem, lv);
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
				CataclysmProxy.updateLaser(golem, beam);
				beam.setPosRaw(golem.getX(), golem.getEyeY(), golem.getZ());
			}
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		beam = CataclysmProxy.addLaserBeam(golem, 60);
		golem.getNavigation().stop();
		return true;
	}

}

