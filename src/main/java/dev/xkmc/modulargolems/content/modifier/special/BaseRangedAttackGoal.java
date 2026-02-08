package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

// 基于 Minecraft 的 Goal 类，并且用于实现一个通用的远程攻击机制
public abstract class BaseRangedAttackGoal extends Goal {

	// 冷却\内限\外限
	private final int waitTime, near, far;

	// 傀儡示例
	protected final AbstractGolemEntity<?, ?> golem;
	// 傀儡等级
	protected final int lv;

	// 初始攻击延迟
	public long attackTime;
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

	public void setInitialDelay(int delay) {
		attackTime = delay;
	}

	protected abstract boolean performAttack(LivingEntity target);

}

