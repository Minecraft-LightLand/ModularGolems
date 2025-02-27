package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.IRangedWeaponGoal;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public abstract class GolemRangedAttackGoal extends Goal implements IRangedWeaponGoal {

	protected final HumanoidGolemEntity mob;
	protected final GolemMeleeGoal melee;
	protected final double speedModifier;
	protected final double radius;
	protected int seeTime;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;

	private int meleeTime = 0;

	protected GolemRangedAttackGoal(HumanoidGolemEntity mob, GolemMeleeGoal melee, double speedModifier, double r) {
		this.mob = mob;
		this.melee = melee;
		this.speedModifier = speedModifier;
		this.radius = r;
		setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	public boolean canUse() {
		if (mob.getTarget() == null || !mob.getTarget().isAlive()) return false;
		ItemStack stack = mob.getItemInHand(mob.getWeaponHand());
		return mayActivate(mob, stack);
	}

	public boolean canContinueToUse() {
		return canUse() || !mob.getNavigation().isDone();
	}

	public void start() {
		super.start();
		mob.setAggressive(true);
		mob.setInRangeAttack(true);
	}

	public void stop() {
		super.stop();
		mob.setAggressive(false);
		mob.setInRangeAttack(false);
		seeTime = 0;
		meleeTime = 0;
		mob.stopUsingItem();
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	protected void doMelee() {
		if (meleeTime > 0) {
			meleeTime--;
			return;
		}
		var target = mob.getTarget();
		if (target == null) return;
		if (melee.canReachTarget(target)) {
			this.mob.swing(InteractionHand.MAIN_HAND);
			this.mob.doHurtTarget(target);
			meleeTime = melee.adjustedTickDelay(20);
		}
	}

	@Override
	public double range(HumanoidGolemEntity golem, ItemStack stack) {
		return radius(stack);
	}

	protected double radius(ItemStack stack) {
		return radius;
	}

	public final double attackRadiusSqr() {
		ItemStack stack = mob.getItemInHand(mob.getWeaponHand());
		return radius(stack) * radius(stack);
	}

	protected void strafing() {
		var target = mob.getTarget();
		if (target == null) return;
		double dist = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
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
		double sqr = attackRadiusSqr();
		if (dist <= sqr && seeTime >= 20) {
			mob.getNavigation().stop();
			++strafingTime;
		} else {
			mob.getNavigation().moveTo(target, speedModifier);
			strafingTime = -1;
		}
		if (sqr < 15) {
			strafingTime = 0;
			return;
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
			if (dist > sqr * 0.75) {
				strafingBackwards = false;
			} else if (dist < sqr * 0.25) {
				strafingBackwards = true;
			}
			mob.getMoveControl().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
			mob.lookAt(target, 30.0F, 30.0F);
		} else {
			mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
		}
	}

}
