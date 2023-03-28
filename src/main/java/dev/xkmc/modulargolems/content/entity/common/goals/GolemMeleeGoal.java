package dev.xkmc.modulargolems.content.entity.common.goals;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraftforge.common.ForgeMod;

public class GolemMeleeGoal extends MeleeAttackGoal {

	public GolemMeleeGoal(AbstractGolemEntity<?, ?> entity, double speedModifier, boolean bypassSightCheck) {
		super(entity, speedModifier, bypassSightCheck);
	}

	@Override
	protected int adjustedTickDelay(int tick) {
		double speed = mob.getAttributeValue(Attributes.ATTACK_SPEED);
		return (int) Math.ceil(super.adjustedTickDelay(tick) / Math.min(1, speed));
	}

	public double getAttackReachSqr(LivingEntity pAttackTarget) {
		return super.getAttackReachSqr(pAttackTarget) + mob.getAttributeValue(ForgeMod.ATTACK_RANGE.get());
	}

}
