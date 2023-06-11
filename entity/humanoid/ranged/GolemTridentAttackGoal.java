package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.common.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;

public class GolemTridentAttackGoal extends RangedAttackGoal {
	private final HumanoidGolemEntity golem;
	private final GolemMeleeGoal melee;

	public GolemTridentAttackGoal(HumanoidGolemEntity pRangedAttackMob, double pSpeedModifier, int pAttackInterval, float pAttackRadius, GolemMeleeGoal melee) {
		super(pRangedAttackMob, pSpeedModifier, pAttackInterval, pAttackRadius);
		this.golem = pRangedAttackMob;
		this.melee = melee;
	}

	/**
	 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
	 * method as well.
	 */
	public boolean canUse() {
		LivingEntity livingentity = golem.getTarget();
		if (livingentity == null || !super.canUse()) return false;
		double d0 = golem.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
		if (melee.getAttackReachSqr(livingentity) > d0) return false;
		InteractionHand hand = golem.getWeaponHand();
		return GolemShooterHelper.isValidThrowableWeapon(this.golem, this.golem.getItemInHand(hand), hand).isThrowable();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void start() {
		super.start();
		this.golem.setAggressive(true);
		this.golem.startUsingItem(golem.getWeaponHand());
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	public void stop() {
		super.stop();
		this.golem.stopUsingItem();
		this.golem.setAggressive(false);
	}
}