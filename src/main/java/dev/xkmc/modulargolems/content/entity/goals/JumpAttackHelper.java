package dev.xkmc.modulargolems.content.entity.goals;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import net.minecraft.world.entity.LivingEntity;

public class JumpAttackHelper {

	private static final int JUMP_MAX_TIME = 60;
	private static final float IMPACT_SPEED = 0.05f, THRESHOLD = 0.01f;

	private final AbstractGolemEntity<?, ?> golem;
	private AbstractGolemEntity<?, ?> mover;

	private EarthquakeHelper.Instance ins = null;
	private double wasFalling;
	private int startJumpingTime = 0;

	public JumpAttackHelper(AbstractGolemEntity<?, ?> golem) {
		this.golem = mover = golem;
	}

	public void tick() {
		mover = golem.getVehicle() instanceof AbstractGolemEntity<?, ?> dog ? dog : golem;
		if (ins != null && ins.owner() != golem && ins.owner() != mover) ins = null;
		wasFalling = ins != null && !mover.isInFluidType() && !mover.onGround() ?
				Math.min(wasFalling, mover.getDeltaMovement().y) : 0;
	}


	public boolean tryJumpAttack(GolemMeleeGoal goal, LivingEntity target, double distSqr) {
		if (!golem.hasFlag(GolemFlags.EARTH_QUAKE) && !mover.hasFlag(GolemFlags.EARTH_QUAKE)) return false;
		boolean wet = mover.isInFluidType();
		boolean valid = !wet && mover.onGround();
		boolean hit = wasFalling < -IMPACT_SPEED && (wet || mover.getDeltaMovement().y > IMPACT_SPEED) ||
				mover.getBoundingBox().intersects(target.getBoundingBox());
		boolean stop = !valid && !hit && (golem.tickCount - startJumpingTime > JUMP_MAX_TIME ||
				wasFalling < -THRESHOLD && (wet || mover.getDeltaMovement().y > -THRESHOLD));
		if (ins != null && stop)
			ins = null;
		if (ins != null && (valid || hit)) {
			goal.resetAttackCooldown();
			ins.modifier().performEarthQuake(ins.owner(), ins.lv());
			mover.level().broadcastEntityEvent(mover, (byte) 83);
			ins = null;
			return true;
		}
		if (ins == null && valid) {
			double d0 = goal.getAttackReachSqr(target);
			ins = EarthquakeHelper.findInstance(golem, target, distSqr - d0);
			if (ins != null) {
				ins.performJump(mover);
				ins.addCD();
				mover.hasImpulse = true;
				startJumpingTime = golem.tickCount;
				return true;
			}
		}
		return false;
	}

	public boolean preventAttack() {
		return ins != null && !mover.onGround() && golem.tickCount - startJumpingTime < JUMP_MAX_TIME;
	}

	public boolean shouldRetreat(LivingEntity target, double dist, double end) {
		return EarthquakeHelper.shouldRetreat(golem, target, dist, end);
	}

}
