package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

// 基于 Minecraft 的 Goal 类，并且用于实现一个通用的远程攻击机制
public abstract class BaseRangedAttackGoal extends Goal implements ICooldownSkill {

	protected String cooldownNameKey;

	// 冷却\内限\外限
	private final int waitTime, near, far;

	// 傀儡示例
	protected final AbstractGolemEntity<?, ?> golem;
	// 傀儡等级
	protected final int lv;

	// 初始攻击延迟
	public long attackTime;
	private boolean lock;
	private BackstepHelper step;
	private boolean backstepped;

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
			this.attackTime = golem.level().getGameTime() + getWaitTime();
		}
	}

	/**
	 * Ticks between two shots. Subclasses override this when the interval depends on the golem's state
	 * (health, equipment) instead of being a constant.
	 */
	protected int getWaitTime() {
		return waitTime;
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	public void stop() {
		if (step != null) {
			// the goal can be cut short mid leap, and leaving the hold behind would lock every charge
			// skill out for good
			step.cancel();
			step = null;
		}
		backstepped = false;
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
		// the leap is ticked every frame regardless of the attack timers. Landing is what releases the
		// hold, and while the golem is still airborne it must not spend the attack cooldown either, or a
		// shot would leave in mid air and the hold would never be released once the timers moved on.
		if (step != null && step.tick()) step = null;
		if (step != null) {
			super.tick();
			return;
		}
		boolean mayAttack = golem.specialAttackCoolDown <= 0 || lock;
		if (attackTime <= time && le != null && le.isAlive() && mayAttack) {
			// one leap per attack. Without this a short leap lands the golem back inside the leap range
			// and it immediately starts hopping again, so the attack would never leave the golem.
			if (!backstepped && beginBackstep(le)) {
				backstepped = true;
				lock = true;
			} else if (performAttack(le)) {
				// only a completed attack clears the mark. A goal that reports false is still part way
				// through a burst, and letting it leap again would interrupt the shots it is sending.
				backstepped = false;
				this.attackTime = time + getWaitTime();
				lock = false;
				golem.specialAttackCoolDown = 20;
			} else {
				lock = true;
				golem.specialAttackCoolDown = 20;
			}
		}
		super.tick();
	}

	public void setInitialDelay(int delay) {
		attackTime = delay;
	}

	/** Ticks until this skill can fire again; zero when ready. */
	public int getCooldownRemaining() {
		return (int) Math.max(0, this.attackTime - golem.level().getGameTime());
	}

	@Override
	public int getRemainingCooldown(AbstractGolemEntity<?, ?> golem) {
		return getCooldownRemaining();
	}

	public BaseRangedAttackGoal withName(String key) {
		this.cooldownNameKey = key;
		return this;
	}

	@Override
	public String getCooldownNameKey() {
		return cooldownNameKey;
	}

	/** Human readable skill name derived from the goal class (e.g. Harbinger Death Beam). */
	public String getSkillName() {
		String name = getClass().getSimpleName();
		if (name.endsWith("AttackGoal")) name = name.substring(0, name.length() - "AttackGoal".length());
		else if (name.endsWith("Goal")) name = name.substring(0, name.length() - "Goal".length());
		return name.replaceAll("([a-z])([A-Z])", "$1 $2");
	}

	protected abstract boolean performAttack(LivingEntity target);

	/**
	 * The backward leap this skill uses to open space before it fires, or null when it does not leap.
	 * {@link BackstepHelper} keeps the golem busy for the flight and spends the charge cooldowns when it
	 * lands, so an implementation only has to describe the movement itself.
	 */
	protected BackstepHelper backstep() {
		return null;
	}

	/**
	 * How close the target has to be before the golem bothers to back away from it. Zero or less skips the
	 * check.
	 */
	protected double getLeapRange() {
		return 0;
	}

	/**
	 * Starts the leap when the skill is ready for it. Returns true on that tick only. The attack then waits
	 * for {@link BackstepHelper#tick()} to report the landing, so it leaves the moment the golem is back on
	 * its feet instead of after a fixed delay that would leave it standing there.
	 */
	private boolean beginBackstep(LivingEntity target) {
		if (step != null) return false;
		var next = backstep();
		if (next == null || !next.canUse(target, getLeapRange())) return false;
		step = next;
		step.start();
		return true;
	}

}

