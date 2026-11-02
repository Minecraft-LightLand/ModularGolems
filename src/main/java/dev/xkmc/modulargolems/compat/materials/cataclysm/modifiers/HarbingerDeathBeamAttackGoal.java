package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class HarbingerDeathBeamAttackGoal extends BaseRangedAttackGoal {

	private Entity beam;

	public HarbingerDeathBeamAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 0, 35, golem, lv);
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
				GolemCataProxy.updateLaser(golem, beam);
				Vec3 origin = golem.getBeamOrigin();
				beam.setPosRaw(origin.x, origin.y, origin.z);
			}
		}
		super.tick();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		beam = GolemCataProxy.addLaserBeam(golem, 60);
		golem.getNavigation().stop();
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

