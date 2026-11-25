package dev.xkmc.modulargolems.compat.materials.eeeab.annihilator;

import dev.xkmc.modulargolems.content.modifier.special.BurstRangedGoal;
import dev.xkmc.modulargolems.compat.materials.eeeab.EEEABProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

/**
 * Multi-target goal for {@link AnnihilatorMissileModifier}.
 * See {@code EntityRelicAnnihilator:performRangedAttack} for original logic.
 */
public class AnnihilatorMissileAttackGoal extends BurstRangedGoal {

	public AnnihilatorMissileAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(100, 0, 32, golem, lv);
		useMultiTargetSemantics();
	}

	@Override
	protected double searchRange() {
		return 32;
	}

	@Override
	protected int getBurstCount() {
		return Math.max(1, lv * 2);
	}

	@Override
	protected int getBurstDelay() {
		return 5;
	}

	@Override
	protected void fire(LivingEntity target, float lateral) {
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
