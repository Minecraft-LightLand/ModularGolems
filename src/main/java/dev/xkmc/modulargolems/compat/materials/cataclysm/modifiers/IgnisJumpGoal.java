package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;

public class IgnisJumpGoal extends BaseRangedAttackGoal {

	public IgnisJumpGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 8, 35, golem, lv);
	}

	@Override
	public boolean canUse() {
		return golem.onGround() && !golem.isInFluidType() && !golem.isInRangedMode() && super.canUse();
	}

	@Override
	protected boolean performAttack(LivingEntity target) {
		golem.setDeltaMovement(
				(target.getX() - golem.getX()) * 0.15,
				1.3,
				(target.getZ() - golem.getZ()) * 0.15
		);
		return true;
	}

}

