package dev.xkmc.modulargolems.content.entity.common;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class GolemMeleeGoal extends MeleeAttackGoal {

	public GolemMeleeGoal(AbstractGolemEntity<?, ?> entity, double speedModifier, boolean bypassSightCheck) {
		super(entity, speedModifier, bypassSightCheck);
	}

	@Override
	protected int adjustedTickDelay(int tick) {
		return (int) Math.ceil(super.adjustedTickDelay(tick) / mob.getAttributeValue(Attributes.ATTACK_SPEED));
	}
}
