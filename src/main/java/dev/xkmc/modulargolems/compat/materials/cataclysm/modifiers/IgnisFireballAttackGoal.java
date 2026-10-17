package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;

public class IgnisFireballAttackGoal extends BaseRangedAttackGoal {

	public IgnisFireballAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 0, 25, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		IgnisFireballModifier.addFireball(golem, lv);
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

