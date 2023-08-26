package dev.xkmc.modulargolems.content.entity.common.goals;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class TeleportToOwnerGoal extends Goal {
	private final AbstractGolemEntity<?, ?> golem;
	private final LevelReader level;
	private final PathNavigation navigation;
	private final boolean canFly;
	private final float maxDist;

	public TeleportToOwnerGoal(AbstractGolemEntity<?, ?> golem) {
		this(golem, 30, false);
	}

	private TeleportToOwnerGoal(AbstractGolemEntity<?, ?> golem, float max, boolean fly) {
		this.golem = golem;
		this.level = golem.level();
		this.navigation = golem.getNavigation();
		this.canFly = fly;
		this.maxDist = max;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		if (!(golem.getNavigation() instanceof GroundPathNavigation) && !(golem.getNavigation() instanceof FlyingPathNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	/**
	 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
	 * method as well.
	 */
	public boolean canUse() {
		if (this.golem.isInSittingPose() || !this.golem.getMode().isMovable())
			return false;
		if (this.golem.isLeashed() || this.golem.isPassenger()) {
			return false;
		}
		Vec3 target = this.golem.getTargetPos();
		return this.golem.distanceToSqr(target) >= this.maxDist * this.maxDist;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean canContinueToUse() {
		return false;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void start() {
		teleportToOwner();
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	public void stop() {
		this.navigation.stop();
	}

	private void teleportToOwner() {
		BlockPos blockpos = BlockPos.containing(this.golem.getTargetPos());

		for (int i = 0; i < 10; ++i) {
			int j = this.randomIntInclusive(-3, 3);
			int k = this.randomIntInclusive(-1, 1);
			int l = this.randomIntInclusive(-3, 3);
			boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
			if (flag) {
				this.golem.setTarget(null);
				return;
			}
		}

	}

	private boolean maybeTeleportTo(int pX, int pY, int pZ) {
		Vec3 target = this.golem.getTargetPos();
		if (Math.abs((double) pX - target.x()) < 2.0D && Math.abs((double) pZ - target.z()) < 2.0D) {
			return false;
		} else if (!this.canTeleportTo(new BlockPos(pX, pY, pZ))) {
			return false;
		} else {
			this.golem.moveTo((double) pX + 0.5D, pY, (double) pZ + 0.5D, this.golem.getYRot(), this.golem.getXRot());
			this.navigation.stop();
			return true;
		}
	}

	private boolean canTeleportTo(BlockPos pPos) {
		BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pPos.mutable());
		boolean allow = blockpathtypes == BlockPathTypes.WALKABLE;
		if (golem.hasFlag(GolemFlags.FLOAT) || golem.hasFlag(GolemFlags.SWIM)) {
			allow |= blockpathtypes == BlockPathTypes.WATER;
			allow |= blockpathtypes == BlockPathTypes.WATER_BORDER;
		}
		if (!allow) return false;
		BlockState blockstate = this.level.getBlockState(pPos.below());
		if (!this.canFly && blockstate.getBlock() instanceof LeavesBlock) {
			return false;
		} else {
			BlockPos blockpos = pPos.subtract(this.golem.blockPosition());
			return this.level.noCollision(this.golem, this.golem.getBoundingBox().move(blockpos));
		}
	}

	private int randomIntInclusive(int pMin, int pMax) {
		return this.golem.getRandom().nextInt(pMax - pMin + 1) + pMin;
	}
}
