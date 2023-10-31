package dev.xkmc.modulargolems.content.entity.goals;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
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

	public static int getTargetResetTime() {
		return MGConfig.COMMON.targetResetTime.get();
	}

	public static double getTargetDistanceDelta() {
		return MGConfig.COMMON.targetResetNoMovementRange.get();
	}

	private final AbstractGolemEntity<?, ?> golem;

	private double lastDist;
	private double timeNoMovement;

	public GolemMeleeGoal(AbstractGolemEntity<?, ?> entity, double speedModifier, boolean bypassSightCheck) {
		super(entity, speedModifier, bypassSightCheck);
		golem = entity;
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

	public boolean canReachTarget(LivingEntity le) {
		return getAttackReachSqr(le) >= mob.getPerceivedTargetDistanceSquareForMeleeAttack(le);
	}

	@Override
	public void tick() {
		if (isTimeToAttack() && golem.getTarget() != null) {
			timeNoMovement++;
		}
		super.tick();
	}

	@Override
	protected void checkAndPerformAttack(LivingEntity target, double distSqr) {
		if (isTimeToAttack()) {
			double dist = Math.sqrt(distSqr);
			if (dist < lastDist - getTargetDistanceDelta()) {
				lastDist = dist;
				timeNoMovement = 0;
			}
		}
		super.checkAndPerformAttack(target, distSqr);
		if (!isTimeToAttack()) {
			lastDist = 1000;
			timeNoMovement = 0;
		} else {
			if (timeNoMovement > getTargetResetTime()) {
				golem.resetTarget(null);
				lastDist = 1000;
				timeNoMovement = 0;
			}
		}
	}

}
