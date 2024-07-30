package dev.xkmc.modulargolems.content.entity.goals;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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

	private boolean earthQuake = false;

	public GolemMeleeGoal(AbstractGolemEntity<?, ?> entity) {
		super(entity, 1, true);
		golem = entity;
	}

	@Override
	protected int adjustedTickDelay(int tick) {
		double speed = mob.getAttributeValue(Attributes.ATTACK_SPEED);
		return (int) Math.ceil(super.adjustedTickDelay(tick) / Math.min(1, speed));
	}

	public double getAttackReachSqr(LivingEntity pAttackTarget) {
		double val = mob.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
		return val * val;
	}

	public boolean canReachTarget(LivingEntity le) {
		golem.isWithinMeleeAttackRange(le);
		return getAttackReachSqr(le) >= golem.getPerceivedTargetDistanceSquareForMeleeAttack(le);
	}

	@Override
	public void tick() {
		if (isTimeToAttack() && golem.getTarget() != null) {
			timeNoMovement++;
		}
		super.tick();
	}

	@Override
	protected void checkAndPerformAttack(LivingEntity target) {
		double distSqr = golem.getPerceivedTargetDistanceSquareForMeleeAttack(target);
		if (isTimeToAttack()) {
			double dist = Math.sqrt(distSqr);
			if (dist < lastDist - getTargetDistanceDelta()) {
				lastDist = dist;
				timeNoMovement = 0;
			}
		}
		doRealAttack(target, distSqr);
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

	protected void doRealAttack(LivingEntity target, double distSqr) {
		if (isTimeToAttack()) {
			if (golem.hasFlag(GolemFlags.EARTH_QUAKE) && !golem.isInWater() && golem.onGround()) {
				if (earthQuake) {
					earthQuake = false;
					resetAttackCooldown();
					//TODO NetheriteMonstrosityEarthquakeModifier.performEarthQuake(golem);
					return;
				} else {
					double d0 = this.getAttackReachSqr(target);
					if (d0 < distSqr && distSqr <= d0  /*TODO + NetheriteMonstrosityEarthquakeModifier.RANGE*/) {
						golem.addDeltaMovement(new Vec3(0, 1, 0));
						golem.hasImpulse = true;
						earthQuake = true;
						return;
					}
				}
			}
		}
		if (earthQuake && !golem.onGround()) return;
		super.checkAndPerformAttack(target);
	}

}
