package dev.xkmc.modulargolems.content.entity.goals;

import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GolemMeleeGoal extends MeleeAttackGoal implements IMeleeGoal {

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
	private int repathDelay;
	private int ticksUntilNextAttack;
	private long lastCanUseCheck;
	private int failureDelay = 0;
	private boolean canPenalize = false;

	private final AbstractGolemEntity<?, ?> golem;

	private double lastDist;
	private double timeNoMovement;

	private final JumpAttackHelper jump;

	private int activeRepathTime = 0;
	private boolean maceJump = false;


	public GolemMeleeGoal(AbstractGolemEntity<?, ?> entity) {
		super(entity, 1, true);
		golem = entity;
		speedModifier = 1;
		pathingTarget = true;
		jump = new JumpAttackHelper(entity);
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	public boolean canUse() {
		long i = golem.level().getGameTime();
		if (i - this.lastCanUseCheck < 10L)
			return false;
		LivingEntity livingentity = golem.getTarget();
		if (livingentity == null)
			return false;
		if (!livingentity.isAlive())
			return false;
		if (canReachTarget(livingentity)) {
			return true;
		}
		this.lastCanUseCheck = i;
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
		return this.path != null;
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
		if (path != null) {
			golem.getNavigation().moveTo(this.path, this.speedModifier);
		}
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
		double val = golem.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
		return val * val + target.getBbWidth();
	}

	public boolean canReachTarget(LivingEntity le) {
		return getAttackReachSqr(le) >= golem.getPerceivedTargetDistanceSquareForMeleeAttack(le);
	}

	public boolean canReachTarget(LivingEntity le, double distSqr) {
		return getAttackReachSqr(le) >= distSqr;
	}

	@Override
	public void tick() {
		if (maceJump && golem.onGround()) maceJump = false;
		LivingEntity target = golem.getTarget();
		if (target == null) return;
		if (isTimeToAttack()) timeNoMovement++;
		golem.lookAt(target, 30.0F, 30.0F);
		double dist = golem.getPerceivedTargetDistanceSquareForMeleeAttack(target);
		if (maceJump) MaceHelper.doMaceAirMove(golem, target);
		else tickMove(target, dist);
		checkAndPerformAttack(target, dist);
		jump.tick();
	}

	protected void tickMove(LivingEntity target, double distSqr) {
		double dist = Math.sqrt(distSqr);
		double end = Math.sqrt(getAttackReachSqr(target));
		double far = end - 0.5;
		this.repathDelay = Math.max(this.repathDelay - 1, 0);
		boolean hasRange = golem.hasRangeAttack() || jump.shouldRetreat(target, dist, end);
		boolean maceRetreat = holdingMace() &&
				(canReachTarget(target, distSqr - 4) || !isTimeToAttack());
		if (dist < far && end > 2.4 || hasRange || maceRetreat) {
			if (!golem.getNavigation().isDone())
				golem.getNavigation().stop();
			golem.getMoveControl().strafe(hasRange || dist < far - 1 ? -1f : -0.5F, 0);
		} else if (dist > far) {
			repath(target, distSqr);
		}
	}

	public void clearDelay() {
		activeRepathTime = golem.tickCount + 10;
	}

	protected void repath(LivingEntity target, double dist) {
		boolean shouldPath = false;
		if (activeRepathTime > golem.tickCount && golem.getNavigation().isDone() && (golem.isInWaterOrBubble() || golem.onGround())) {
			repathDelay = failureDelay = 0;
			shouldPath = true;
		}
		if (repathDelay > 0) return;
		shouldPath |= pathedX == 0 && pathedY == 0 && pathedZ == 0;
		shouldPath |= target.distanceToSqr(pathedX, pathedY, pathedZ) >= 1.0D;
		shouldPath |= golem.getRandom().nextFloat() < 0.05F;
		if (!shouldPath) return;
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

		if (activeRepathTime > 0)
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
			doRealAttack(target, distSqr);
			if (timeNoMovement > getTargetResetTime()) {
				golem.resetTarget(null);
				lastDist = 1000;
				timeNoMovement = 0;
			}
		} else {
			lastDist = 1000;
			timeNoMovement = 0;
		}
	}

	protected void doRealAttack(LivingEntity target, double distSqr) {
		if (isTimeToAttack()) {
			if (jump.tryJumpAttack(this, target, distSqr)) return;
		}
		if (jump.preventAttack()) return;
		if (this.mob.hasLineOfSight(target)) {
			boolean mayJump = !golem.isInFluidType() && golem.onGround();
			boolean jump = false;
			if (mayJump && canReachTarget(target, distSqr - 4)) {
				if (holdingMace()) {
					ticksUntilNextAttack += 10;
					jump = true;
				}
			}
			if (!jump && canReachTarget(target, distSqr)) {
				if (holdingMace()) MaceHelper.doMaceAttack(golem, target);
				this.resetAttackCooldown();
				this.mob.swing(InteractionHand.MAIN_HAND);
				this.mob.doHurtTarget(target);
				MaceHelper.capGolemMovement(golem);
			} else if (mayJump) {
				var diff = target.position().subtract(golem.position());
				jump |= diff.horizontalDistanceSqr() < getAttackReachSqr(target) / 2 &&
						diff.y > 0 && diff.y < 3 + golem.getBbHeight();
			}
			if (jump) {
				maceJump = true;
				var v = golem.getDeltaMovement();
				golem.setDeltaMovement(new Vec3(v.x, Math.max(v.y, 0) + 1, v.z));
				golem.hasImpulse = true;
			}
		}
	}

	protected boolean holdingMace() {
		return golem.getMainHandItem().is(ItemTags.MACE_ENCHANTABLE);
	}

}
