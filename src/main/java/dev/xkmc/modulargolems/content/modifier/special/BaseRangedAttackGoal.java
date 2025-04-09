package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class BaseRangedAttackGoal extends Goal {

	private final int waitTime, near, far;

	protected final AbstractGolemEntity<?, ?> golem;
	protected final int lv;

	private long attackTime;
	private boolean lock;

	public BaseRangedAttackGoal(int waitTime, int near, int far, AbstractGolemEntity<?, ?> golem, int lv) {
		this.golem = golem;
		this.lv = lv;
		this.waitTime = waitTime;
		this.attackTime = 0;
		this.near = near * near;
		this.far = far * far;
	}

	/**
	 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
	 * method as well.
	 */
	public boolean canUse() {
		LivingEntity livingentity = this.golem.getTarget();
		return livingentity != null &&
				livingentity.isAlive() &&
				this.golem.canAttack(livingentity) &&
				this.golem.distanceToSqr(livingentity) < far &&
				(this.golem.getNavigation().isStuck() || this.golem.distanceToSqr(livingentity) > near);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void start() {
		if (attackTime == 0) {
			this.attackTime = golem.level().getGameTime() + waitTime;
		}
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	public void stop() {
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick() {
		LivingEntity le = golem.getTarget();
		long time = golem.level().getGameTime();
		boolean mayAttack = golem.specialAttackCoolDown <= 0 || lock;
		if (attackTime <= time && le != null && le.isAlive() && mayAttack) {
			if (performAttack(le)) {
				this.attackTime = time + waitTime;
				lock = false;
			} else {
				lock = true;
			}
			golem.specialAttackCoolDown = 20;
		}
		super.tick();
	}

	protected abstract boolean performAttack(LivingEntity target);

}

