package dev.xkmc.modulargolems.content.entity.common.goals;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FollowOwnerGoal extends Goal {
	private final AbstractGolemEntity<?, ?> golem;
	private final double speedModifier;
	private int timeToRecalcPath;
	private final float stopDistance;
	private final float startDistance;
	private float oldWaterCost;

	public FollowOwnerGoal(AbstractGolemEntity<?, ?> golem) {
		this(golem, 1, 10, 3);
	}

	private FollowOwnerGoal(AbstractGolemEntity<?, ?> golem, double speed, float start, float stop) {
		this.golem = golem;
		this.speedModifier = speed;
		this.startDistance = start;
		this.stopDistance = stop;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	/**
	 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
	 * method as well.
	 */
	public boolean canUse() {
		if (this.golem.isInSittingPose() || !this.golem.getMode().isMovable())
			return false;
		Vec3 target = this.golem.getTargetPos();
		return !(this.golem.distanceToSqr(target) < (double) (this.startDistance * this.startDistance));
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean canContinueToUse() {
		if (golem.getNavigation().isDone())
			return false;
		if (this.golem.isInSittingPose() || !this.golem.getMode().isMovable())
			return false;
		Vec3 target = this.golem.getTargetPos();
		return !(this.golem.distanceToSqr(target) <= (double) (this.stopDistance * this.stopDistance));
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void start() {
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.golem.getPathfindingMalus(BlockPathTypes.WATER);
		this.golem.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	public void stop() {
		golem.getNavigation().stop();
		this.golem.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick() {
		Vec3 target = this.golem.getTargetPos();
		LivingEntity owner = this.golem.getOwner();
		if (owner != null)
			this.golem.getLookControl().setLookAt(owner, 10.0F, (float) this.golem.getMaxHeadXRot());
		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = this.adjustedTickDelay(10);
			if (!this.golem.isLeashed() && !this.golem.isPassenger()) {
				golem.getNavigation().moveTo(target.x(), target.y(), target.z(), this.speedModifier);
			}
		}
	}

}
