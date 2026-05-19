package dev.xkmc.modulargolems.content.entity.goals;

import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import static dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal.getTargetDistanceDelta;
import static dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal.getTargetResetTime;

public class GolemRiddenMeleeGoal extends Goal implements IMeleeGoal {

	private int ticksUntilNextAttack;
	private long lastCanUseCheck;

	private final AbstractGolemEntity<?, ?> golem;

	private double lastDist;
	private double timeNoMovement;

	public GolemRiddenMeleeGoal(AbstractGolemEntity<?, ?> entity) {
		golem = entity;
	}

	public boolean canUse() {
		if (!(golem.getControllingPassenger() instanceof Mob)) return false;
		long i = golem.level().getGameTime();
		if (i - this.lastCanUseCheck < 20L) {
			return false;
		} else {
			this.lastCanUseCheck = i;
			LivingEntity livingentity = golem.getTarget();
			if (livingentity == null) {
				return false;
			} else if (!livingentity.isAlive()) {
				return false;
			} else {
				return this.getAttackReachSqr(livingentity) >= golem.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
			}
		}
	}

	public boolean canContinueToUse() {
		if (!(golem.getControllingPassenger() instanceof Mob)) return false;
		LivingEntity livingentity = golem.getTarget();
		if (livingentity == null) {
			return false;
		} else if (!livingentity.isAlive()) {
			return false;
		} else if (!golem.isWithinRestriction(livingentity.blockPosition())) {
			return false;
		} else {
			return !(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player) livingentity).isCreative();
		}
	}

	public void start() {
		golem.setAggressive(true);
		this.ticksUntilNextAttack = 0;
	}

	public void stop() {
		LivingEntity livingentity = golem.getTarget();
		if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
			golem.setTarget(null);
		}
		golem.setAggressive(false);
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	protected void resetAttackCooldown() {
		this.ticksUntilNextAttack = getMeleeInterval();
	}

	protected boolean isTimeToAttack() {
		return this.ticksUntilNextAttack <= 0;
	}

	@Override
	public int getMeleeInterval() {
		double speed = golem.getAttributeValue(Attributes.ATTACK_SPEED);
		return (int) Math.ceil(20 / Math.min(1, speed));
	}

	public double getAttackReachSqr(LivingEntity target) {
		double val = golem.getAttributeValue(ForgeMod.ENTITY_REACH.get());
		return val * val + target.getBbWidth();
	}

	public boolean canReachTarget(LivingEntity le) {
		return getAttackReachSqr(le) >= golem.getPerceivedTargetDistanceSquareForMeleeAttack(le);
	}

	@Override
	public void tick() {
		LivingEntity target = golem.getTarget();
		if (target == null) return;
		if (isTimeToAttack()) {
			timeNoMovement++;
		}
		double dist = golem.getPerceivedTargetDistanceSquareForMeleeAttack(target);
		checkAndPerformAttack(target, dist);
	}

	protected void checkAndPerformAttack(LivingEntity target, double distSqr) {
		this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
		if (isTimeToAttack()) {
			double dist = Math.sqrt(distSqr);
			if (dist < lastDist - getTargetDistanceDelta()) {
				lastDist = dist;
				timeNoMovement = 0;
			}
		}
		doRealAttack(target, distSqr);
		if (!isTimeToAttack()) {
			lastDist = 1000;
			timeNoMovement = 0;
		} else {
			if (timeNoMovement > getTargetResetTime()) {
				golem.resetTarget(null);
				lastDist = 1000;
				timeNoMovement = 0;
			}
		}
	}

	protected void doRealAttack(LivingEntity target, double distSqr) {
		double d0 = this.getAttackReachSqr(target);
		if (distSqr <= d0 && this.ticksUntilNextAttack <= 0) {
			this.resetAttackCooldown();
			golem.swing(InteractionHand.MAIN_HAND);
			golem.doHurtTarget(target);
		}
	}

}
