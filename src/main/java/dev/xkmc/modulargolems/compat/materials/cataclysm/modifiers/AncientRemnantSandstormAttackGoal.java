package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BackstepHelper;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

public class AncientRemnantSandstormAttackGoal extends BaseRangedAttackGoal {

	private static final int WAIT_TIME = 100;

	public AncientRemnantSandstormAttackGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(WAIT_TIME, 0, 35, golem, lv);
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		var group = SandstormGroup.of(golem);
		group.maintain(golem, lv);
		group.assign(golem);
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
