package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

// 基于 Minecraft 的 Goal 类，并且用于实现一个通用的远程攻击机制
public abstract class BaseRangedAttackGoal extends Goal implements ICooldownSkill {

	protected String cooldownNameKey;

	// 冷却\内限\外限
	private final int waitTime, near, far;

	protected final AbstractGolemEntity<?, ?> golem;
	protected final int lv;

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

	public boolean canUse() {
		LivingEntity livingentity = this.golem.getTarget();
		return livingentity != null &&
				livingentity.isAlive() &&
				this.golem.canAttack(livingentity) &&
				this.golem.distanceToSqr(livingentity) < far &&
				(this.golem.getNavigation().isStuck() || this.golem.distanceToSqr(livingentity) > near);
	}

	public void start() {
		if (attackTime == 0) {
			this.attackTime = golem.level().getGameTime() + waitTime;
		}
	}

	public void stop() {
		if (step != null) {
			step.cancel();
			step = null;
		}
		backstepped = false;
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	public void tick() {
		LivingEntity le = golem.getTarget();
		long time = golem.level().getGameTime();
		if (step != null && step.tick()) step = null;
		if (step != null) {
			super.tick();
			return;
		}
		boolean mayAttack = golem.specialAttackCoolDown <= 0 || lock;
		if (attackTime <= time && le != null && le.isAlive() && mayAttack) {
			if (!backstepped && beginBackstep(le)) {
				backstepped = true;
				lock = true;
			} else if (performAttack(le)) {
				backstepped = false;
				this.attackTime = time + waitTime;
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

	public String getSkillName() {
		String name = getClass().getSimpleName();
		if (name.endsWith("AttackGoal")) name = name.substring(0, name.length() - "AttackGoal".length());
		else if (name.endsWith("Goal")) name = name.substring(0, name.length() - "Goal".length());
		return name.replaceAll("([a-z])([A-Z])", "$1 $2");
	}

	protected abstract boolean performAttack(LivingEntity target);

	protected BackstepHelper backstep() {
		return null;
	}

	protected double getLeapRange() {
		return 0;
	}

	private boolean beginBackstep(LivingEntity target) {
		if (step != null) return false;
		var next = backstep();
		if (next == null || !next.canUse(target, getLeapRange())) return false;
		step = next;
		step.start();
		return true;
	}

}
