package dev.xkmc.modulargolems.content.entity.goals;

import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;

import java.util.EnumSet;

public class GolemMeleeGoal extends Goal implements IMeleeGoal {

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

	private final double speedModifier;
	private final boolean pathingTarget;
	private Path path;
	private double pathedX;
	private double pathedY;
	private double pathedZ;
	public int repathDelay;
	public int ticksUntilNextAttack;
	private long lastCanUseCheck;
	private int failureDelay = 0;
	private boolean canPenalize = false;

	private final AbstractGolemEntity<?, ?> golem;

	private double lastDist;
	private double timeNoMovement;

	private EarthquakeHelper.Instance earthQuake = null;
	private double wasFalling;
	private int startJumpingTime = 0;
	private int clearDelayFlag = 0;

	public GolemMeleeGoal(AbstractGolemEntity<?, ?> entity) {
		golem = entity;
		speedModifier = 1;
		pathingTarget = true;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	public boolean canUse() {
		long i = golem.level().getGameTime();
		if (i - this.lastCanUseCheck < 20L) {
			return false;
		} else {
			this.lastCanUseCheck = i;
			LivingEntity livingentity = golem.getTarget();
			if (livingentity == null) {
				return false;
			} else if (!livingentity.isAlive()) {
				return false;
			} else {
				if (canPenalize) {
					if (--this.repathDelay <= 0) {
						this.path = golem.getNavigation().createPath(livingentity, 0);
						this.repathDelay = 4 + golem.getRandom().nextInt(7);
						return this.path != null;
					} else {
						return true;
					}
				}
				this.path = golem.getNavigation().createPath(livingentity, 0);
				if (this.path != null) {
					return true;
				} else {
					return this.getAttackReachSqr(livingentity) >= golem.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
				}
			}
		}
	}

	public boolean canContinueToUse() {
		LivingEntity livingentity = golem.getTarget();
		if (livingentity == null) {
			return false;
		} else if (!livingentity.isAlive()) {
			return false;
		} else if (!this.pathingTarget) {
			return !golem.getNavigation().isDone();
		} else if (!golem.isWithinRestriction(livingentity.blockPosition())) {
			return false;
		} else {
			return !(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player) livingentity).isCreative();
		}
	}

	public void start() {
		golem.getNavigation().moveTo(this.path, this.speedModifier);
		golem.setAggressive(true);
		this.repathDelay = 0;
		this.ticksUntilNextAttack = 0;
	}

	public void stop() {
		LivingEntity livingentity = golem.getTarget();
		if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
			golem.setTarget(null);
		}
		golem.setAggressive(false);
		golem.getNavigation().stop();
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	protected void resetAttackCooldown() {
		this.ticksUntilNextAttack = getMeleeInterval();
	}

	protected boolean isTimeToAttack() {
		return this.ticksUntilNextAttack <= 0;
	}

	@Override
	public int getMeleeInterval() {
		double speed = golem.getAttributeValue(Attributes.ATTACK_SPEED);
		return (int) Math.ceil(20 / Math.min(1, speed));
	}

	public double getAttackReachSqr(LivingEntity target) {
		double val = golem.getAttributeValue(ForgeMod.ENTITY_REACH.get());
		return val * val + target.getBbWidth();
	}

	public boolean canReachTarget(LivingEntity le) {
		return getAttackReachSqr(le) >= golem.getPerceivedTargetDistanceSquareForMeleeAttack(le);
	}

	@Override
	public void tick() {
		LivingEntity target = golem.getTarget();
		if (target == null) return;
		if (isTimeToAttack()) {
			timeNoMovement++;
		}
		golem.lookAt(target, 30.0F, 30.0F);
		double dist = golem.getPerceivedTargetDistanceSquareForMeleeAttack(target);
		tickMove(target, dist);
		checkAndPerformAttack(target, dist);
		wasFalling = earthQuake != null && !golem.isInFluidType() && !golem.onGround() ? Math.min(wasFalling, golem.getDeltaMovement().y) : 0;
	}

	protected void tickMove(LivingEntity target, double distSqr) {
		double dist = Math.sqrt(distSqr);
		double reach = getAttackReachSqr(target);
		double end = Math.sqrt(reach);
		double far = end - 0.5;
		this.repathDelay = Math.max(this.repathDelay - 1, 0);
		boolean hasRange = golem.hasRangeAttack() ||
				EarthquakeHelper.shouldRetreat(golem, target, dist, end);
		if (dist < far && end > 2.4 || hasRange) {
			if (!golem.getNavigation().isDone())
				golem.getNavigation().stop();
			golem.getMoveControl().strafe(hasRange || dist < far - 1 ? -1f : -0.5F, 0);
		} else if (dist > far) {
			repath(target, distSqr);
		}
	}

	public void clearDelay() {
		clearDelayFlag = 5;
	}

	protected void repath(LivingEntity target, double dist) {
		if ((golem.isInWaterOrBubble() || golem.onGround()) && clearDelayFlag > 0) {
			clearDelayFlag--;
			repathDelay = failureDelay = 0;
			pathedX = pathedY = pathedZ = 0;
		}
		if (repathDelay > 0) return;
		if ((pathedX != 0.0D || pathedY != 0.0D || pathedZ != 0.0D) &&
				!(target.distanceToSqr(pathedX, pathedY, pathedZ) >= 1.0D) &&
				!(golem.getRandom().nextFloat() < 0.05F))
			return;
		this.pathedX = target.getX();
		this.pathedY = target.getY();
		this.pathedZ = target.getZ();
		this.repathDelay = 4 + golem.getRandom().nextInt(7);
		if (this.canPenalize) {
			this.repathDelay += failureDelay;
			if (golem.getNavigation().getPath() != null) {
				Node end = golem.getNavigation().getPath().getEndNode();
				if (end != null && target.distanceToSqr(end.x, end.y, end.z) < 1)
					failureDelay = 0;
				else
					failureDelay += 10;
			} else {
				failureDelay += 10;
			}
		}
		if (dist > 1024.0D) {
			this.repathDelay += 10;
		} else if (dist > 256.0D) {
			this.repathDelay += 5;
		}

		if (!golem.getNavigation().moveTo(target, this.speedModifier)) {
			this.repathDelay += 15;
		}
		if (clearDelayFlag > 0)
			repathDelay = Math.min(repathDelay, 2);
		this.repathDelay = this.adjustedTickDelay(this.repathDelay);
	}

	protected void checkAndPerformAttack(LivingEntity target, double distSqr) {
		this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
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
		float impactSpeed = 0.05f;
		float significantSpeed = 0.01f;
		int jumpMaxTime = 60;
		if (isTimeToAttack()) {
			if (golem.hasFlag(GolemFlags.EARTH_QUAKE)) {
				boolean wet = golem.isInFluidType();
				boolean valid = !wet && golem.onGround();
				boolean hit = wasFalling < -impactSpeed && (wet || golem.getDeltaMovement().y > impactSpeed) ||
						golem.getBoundingBox().intersects(target.getBoundingBox());
				boolean stop = !valid && !hit && (golem.tickCount - startJumpingTime > jumpMaxTime ||
						wasFalling < -significantSpeed && (wet || golem.getDeltaMovement().y > -significantSpeed));
				if (earthQuake != null && stop)
					earthQuake = null;
				if (earthQuake != null && (valid || hit)) {
					resetAttackCooldown();
					earthQuake.modifier().performEarthQuake(golem, earthQuake.lv());
					golem.level().broadcastEntityEvent(golem, (byte) 83);
					earthQuake = null;
					return;
				}
				if (earthQuake == null && valid) {
					double d0 = this.getAttackReachSqr(target);
					earthQuake = EarthquakeHelper.findInstance(golem, target, distSqr - d0);
					if (earthQuake != null) {
						golem.getPersistentData().putLong(((GolemModifier) earthQuake.modifier()).getID() + ":timestamp", golem.level().getGameTime());
						earthQuake.modifier().performJump(golem, earthQuake.lv());
						golem.hasImpulse = true;
						startJumpingTime = golem.tickCount;
						return;
					}
				}
			}
		}
		if (earthQuake != null && !golem.onGround() && golem.tickCount - startJumpingTime < jumpMaxTime) return;
		double d0 = this.getAttackReachSqr(target);
		if (distSqr <= d0 && this.ticksUntilNextAttack <= 0) {
			this.resetAttackCooldown();
			golem.swing(InteractionHand.MAIN_HAND);
			golem.doHurtTarget(target);
		}
	}

}
