package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;

public class GolemTridentAttackGoal extends RangedAttackGoal {
	private final HumanoidGolemEntity drowned;

	public GolemTridentAttackGoal(HumanoidGolemEntity pRangedAttackMob, double pSpeedModifier, int pAttackInterval, float pAttackRadius) {
		super(pRangedAttackMob, pSpeedModifier, pAttackInterval, pAttackRadius);
		this.drowned = pRangedAttackMob;
	}

	/**
	 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
	 * method as well.
	 */
	public boolean canUse() {
		return super.canUse() && GolemShooterHelper.isValidThrowableWeapon(this.drowned.getMainHandItem().getItem());
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void start() {
		super.start();
		this.drowned.setAggressive(true);
		this.drowned.startUsingItem(InteractionHand.MAIN_HAND);
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	public void stop() {
		super.stop();
		this.drowned.stopUsingItem();
		this.drowned.setAggressive(false);
	}
}