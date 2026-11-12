package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.goety.multi.MultiTargetRangedGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class LeviathanBlastPortalAttackGoal extends MultiTargetRangedGoal {

	public LeviathanBlastPortalAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(LeviathanBlastPortalModifier.COOLDOWN, LeviathanBlastPortalModifier.MIN_RANGE, LeviathanBlastPortalModifier.MAX_RANGE, golem, lv);
	}

	@Override
	protected int searchRange() {
		return 35;
	}

	@Override
	protected int getMaxTarget() {
		return lv * 3;
	}

	@Override
	protected int cd() {
		return 10;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		LeviathanBlastPortalModifier.addBeam(golem, target);
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

