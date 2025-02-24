package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.bow.BowBehaviorRegistry;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.IRangedWeaponGoal;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class GolemBowAttackGoal extends Goal implements IRangedWeaponGoal {

	private static double attackRadiusSqr() {
		return 25 * 25;
	}

	private final HumanoidGolemEntity mob;
	private final double speedModifier;
	private int attackIntervalMin;
	private int attackTime = -1;
	private int seeTime;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;

	public GolemBowAttackGoal(HumanoidGolemEntity pMob, double pSpeedModifier, int pAttackIntervalMin) {
		mob = pMob;
		speedModifier = pSpeedModifier;
		attackIntervalMin = pAttackIntervalMin;
		setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	public void setMinAttackInterval(int pAttackCooldown) {
		attackIntervalMin = pAttackCooldown;
	}

	/**
	 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
	 * method as well.
	 */
	public boolean canUse() {
		return mob.getTarget() != null && isHoldingBow() && !mob.getProjectile(
				mob.getItemInHand(mob.getWeaponHand())).isEmpty();
	}

	protected boolean isHoldingBow() {
		return mob.isHolding(BowBehaviorRegistry::isValidBowItem);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean canContinueToUse() {
		return (canUse() || !mob.getNavigation().isDone()) && isHoldingBow();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void start() {
		super.start();
		mob.setAggressive(true);
		mob.setInRangeAttack(true);
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	public void stop() {
		super.stop();
		mob.setAggressive(false);
		mob.setInRangeAttack(false);
		seeTime = 0;
		attackTime = -1;
		mob.stopUsingItem();
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	public void tick() {
		LivingEntity target = mob.getTarget();
		if (target == null) return;
		double d0 = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
		boolean sight = mob.getSensing().hasLineOfSight(target);
		boolean oldSight = seeTime > 0;
		if (sight != oldSight) {
			seeTime = 0;
		}
		if (sight) {
			++seeTime;
		} else {
			--seeTime;
		}
		straf(target, d0);

		if (mob.isUsingItem()) {
			if (!sight && seeTime < -60) {
				mob.stopUsingItem();
			} else if (sight) {
				var weapon = BowBehaviorRegistry.get(mob, mob.getUseItem());
				int i = mob.getTicksUsingItem();
				if (weapon.isPresent() && i >= weapon.get().pullTime()) {
					mob.stopUsingItem();
					mob.performRangedAttack(target, BowItem.getPowerForTime(i));
					attackTime = attackIntervalMin;
				}
			}
		} else if (--attackTime <= 0 && seeTime >= -60) {
			mob.startUsingItem(mob.getWeaponHand());
		}
	}

	private void straf(LivingEntity target, double d0) {
		if (!(d0 > attackRadiusSqr()) && seeTime >= 20) {
			mob.getNavigation().stop();
			++strafingTime;
		} else {
			mob.getNavigation().moveTo(target, speedModifier);
			strafingTime = -1;
		}
		if (strafingTime >= 20) {
			if ((double) mob.getRandom().nextFloat() < 0.3D) {
				strafingClockwise = !strafingClockwise;
			}
			if ((double) mob.getRandom().nextFloat() < 0.3D) {
				strafingBackwards = !strafingBackwards;
			}
			strafingTime = 0;
		}
		if (strafingTime > -1) {
			if (d0 > attackRadiusSqr() * 0.75) {
				strafingBackwards = false;
			} else if (d0 < attackRadiusSqr() * 0.25) {
				strafingBackwards = true;
			}
			mob.getMoveControl().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
			mob.lookAt(target, 30.0F, 30.0F);
		} else {
			mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
		}
	}

	@Override
	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float dist, ItemStack stack, InteractionHand hand) {
		BowBehaviorRegistry.get(golem, stack).ifPresent(e -> e.shoot().performRangedAttack(golem, target, dist, stack, hand));
	}

}
