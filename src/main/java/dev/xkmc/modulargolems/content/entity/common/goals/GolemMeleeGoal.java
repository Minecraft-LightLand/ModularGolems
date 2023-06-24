package dev.xkmc.modulargolems.content.entity.common.goals;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;

public class GolemMeleeGoal extends MeleeAttackGoal {

	private static double getDistance(double a0, double a1, double b0, double b1) {
		if (a1 < b0) {
			return b0 - a1;
		} else if (b1 < a0) {
			return a0 - b1;
		}
		return 0;
	}

	public static double calculateDistSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target) {
		AABB aabb0 = golem.getBoundingBox();
		AABB aabb1 = target.getBoundingBox();
		double x = getDistance(aabb0.minX, aabb0.maxX, aabb1.minX, aabb1.maxX);
		double y = getDistance(aabb0.minY, aabb0.maxY, aabb1.minY, aabb1.maxY);
		double z = getDistance(aabb0.minZ, aabb0.maxZ, aabb1.minZ, aabb1.maxZ);
		return x * x + y * y + z * z;
	}

	public GolemMeleeGoal(AbstractGolemEntity<?, ?> entity, double speedModifier, boolean bypassSightCheck) {
		super(entity, speedModifier, bypassSightCheck);
	}

	@Override
	protected int adjustedTickDelay(int tick) {
		double speed = mob.getAttributeValue(Attributes.ATTACK_SPEED);
		return (int) Math.ceil(super.adjustedTickDelay(tick) / Math.min(1, speed));
	}

	public double getAttackReachSqr(LivingEntity pAttackTarget) {
		double val = mob.getAttributeValue(ForgeMod.ENTITY_REACH.get());
		return val * val;
	}

}
