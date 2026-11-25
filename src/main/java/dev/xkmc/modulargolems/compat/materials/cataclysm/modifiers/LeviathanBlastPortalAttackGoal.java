package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BurstRangedGoal;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class LeviathanBlastPortalAttackGoal extends BurstRangedGoal {

	public LeviathanBlastPortalAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 0, 35, golem, lv);
		useMultiTargetSemantics();
	}

	@Override
	protected double searchRange() {
		return 35;
	}

	@Override
	protected int getBurstCount() {
		return lv * 3;
	}

	@Override
	protected int getBurstDelay() {
		return 10;
	}

	@Override
	protected void fire(LivingEntity target, float lateral) {
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

