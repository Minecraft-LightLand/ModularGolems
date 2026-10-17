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
	private boolean busyPrev;

	public JumpAttackHelper(AbstractGolemEntity<?, ?> golem) {
		this.golem = mover = golem;
	}

	public void tick() {
		mover = golem.getVehicle() instanceof AbstractGolemEntity<?, ?> dog ? dog : golem;
		if (ins != null && ins.owner() != golem && ins.owner() != mover) ins = null;
		// 滞空期间持有共用技能锁，一落地就释放，避免另一个离地技能叠上来
		if (ins != null) EarthquakeHelper.setBusy(golem, true);
		else if (busyPrev) EarthquakeHelper.setBusy(golem, false);
		busyPrev = ins != null;
		// 下落加速：起跳后 3 tick 起只要在空气里且正在下坠就持续加重力，
		// 否则斜向目标的大跳会飘很久才落地，技能节奏被拖慢
		if (ins != null && !mover.isInFluidType() && !mover.onGround()
				&& mover.getDeltaMovement().y < -0.05 && golem.tickCount - startJumpingTime > 3) {
			mover.setDeltaMovement(mover.getDeltaMovement().add(0, -0.45, 0));
		}
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
		// 只在真正落地时结算。用 hit 会让上升途中擦到目标碰撞箱就提前触发，
		// 结果是技能在半空炸开、傀儡却还在往上飞
		if (ins != null && valid) {
			goal.resetAttackCooldown();
			ins.modifier().performEarthQuake(ins.owner(), ins.lv());
			ins.modifier().onEarthQuakeLanded(ins.owner(), ins.lv());
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
				EarthquakeHelper.setBusy(golem, true);
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
