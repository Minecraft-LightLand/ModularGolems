package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.cataclysm_mux.MWCataProxy;
import dev.xkmc.modulargolems.compat.materials.goety.multi.MultiTargetRangedGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class EnderGuardianVoidRuneAttackGoal extends MultiTargetRangedGoal {

	public EnderGuardianVoidRuneAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 0, 15, golem, lv);
	}

	@Override
	protected int searchRange() {
		return 15;
	}

	@Override
	protected int getMaxTarget() {
		return lv * 2;
	}

	@Override
	protected int cd() {
		return 20;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		GolemCataProxy.guardianStompFangs(golem, target);
		MWCataProxy.spawnVortex(golem, target.position());
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
