package dev.xkmc.modulargolems.compat.materials.eeeab.annihilator;

import dev.xkmc.modulargolems.compat.materials.eeeab.EEEABProxy;
import dev.xkmc.modulargolems.compat.materials.goety.multi.MultiTargetRangedGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

/**
 * Multi-target goal for {@link AnnihilatorMissileModifier}.
 * See {@code EntityRelicAnnihilator:performRangedAttack} for original logic.
 */
public class AnnihilatorMissileAttackGoal extends MultiTargetRangedGoal {

	public AnnihilatorMissileAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 0, 32, golem, lv);
	}

	@Override
	protected int searchRange() {
		return 32;
	}

	@Override
	protected int getMaxTarget() {
		return Math.max(1, lv * 2);
	}

	@Override
	protected int cd() {
		return 5;
	}

	@Override
	protected void performAttackImpl(LivingEntity target) {
		if (golem.level().isClientSide) return;
		EEEABProxy.shootAnnihilatorMissile(golem, target);
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
