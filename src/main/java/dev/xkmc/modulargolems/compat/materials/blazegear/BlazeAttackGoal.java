package dev.xkmc.modulargolems.compat.materials.blazegear;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.SmallFireball;

public class BlazeAttackGoal extends Goal {

	private final AbstractGolemEntity<?, ?> blaze;
	private final int lv;
	private int attackStep;
	private int attackTime;
	private boolean charged;

	public BlazeAttackGoal(AbstractGolemEntity<?, ?> pBlaze, int lv) {
		this.blaze = pBlaze;
		this.lv = lv;
	}

	/**
	 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
	 * method as well.
	 */
	public boolean canUse() {
		LivingEntity livingentity = this.blaze.getTarget();
		return livingentity != null && livingentity.isAlive() && this.blaze.canAttack(livingentity);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void start() {
		this.attackStep = 0;
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	public void stop() {
		this.charged = false;
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick() {
		--this.attackTime;
		LivingEntity livingentity = this.blaze.getTarget();
		if (livingentity != null) {
			boolean flag = this.blaze.getSensing().hasLineOfSight(livingentity);
			double distSqr = this.blaze.distanceToSqr(livingentity);
			if (distSqr < 0) {
				return;
			} else if (flag) {
				double d1 = livingentity.getX() - this.blaze.getX();
				double d2 = livingentity.getY(0.5D) - this.blaze.getY(0.5D);
				double d3 = livingentity.getZ() - this.blaze.getZ();
				if (this.attackTime <= 0) {
					++this.attackStep;
					if (this.attackStep == 1) {
						this.attackTime = 60;
						this.charged = true;
					} else if (this.attackStep < lv + 2) {
						this.attackTime = 6;
					} else {
						this.attackTime = 100;
						this.attackStep = 0;
						this.charged = false;
					}
					if (this.attackStep > 1) {
						double d4 = Math.sqrt(Math.sqrt(distSqr)) * 0.5D;
						if (!this.blaze.isSilent()) {
							this.blaze.level().levelEvent(null, 1018, this.blaze.blockPosition(), 0);
						}
						for (int i = 0; i < 1; ++i) {
							SmallFireball smallfireball = new SmallFireball(this.blaze.level(), this.blaze, this.blaze.getRandom().triangle(d1, 2.297D * d4), d2, this.blaze.getRandom().triangle(d3, 2.297D * d4));
							smallfireball.setPos(smallfireball.getX(), this.blaze.getY(0.5D) + 0.5D, smallfireball.getZ());
							this.blaze.level().addFreshEntity(smallfireball);
						}
					}
				}
			}
			super.tick();
		}
	}

}
