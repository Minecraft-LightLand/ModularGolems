package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.crossbow.CrossbowBehaviorRegistry;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.IRangedWeaponGoal;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class GolemCrossbowAttackGoal extends Goal implements IRangedWeaponGoal {
	public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
	private final HumanoidGolemEntity mob;
	private GolemCrossbowAttackGoal.CrossbowState crossbowState = GolemCrossbowAttackGoal.CrossbowState.UNCHARGED;
	private final double speedModifier;
	private final float attackRadiusSqr;
	private int seeTime;
	private int attackDelay;
	private int updatePathDelay;

	public GolemCrossbowAttackGoal(HumanoidGolemEntity pMob, double pSpeedModifier, float pAttackRadius) {
		this.mob = pMob;
		this.speedModifier = pSpeedModifier;
		this.attackRadiusSqr = pAttackRadius * pAttackRadius;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	public boolean canUse() {
		return this.isValidTarget() && this.isHoldingCrossbow();
	}

	private boolean isHoldingCrossbow() {
		return this.mob.isHolding(CrossbowBehaviorRegistry::isValidCrossbowItem);
	}

	public boolean canContinueToUse() {
		return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingCrossbow();
	}

	private boolean isValidTarget() {
		return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
	}

	@Override
	public void start() {
		super.start();
		mob.setAggressive(true);
		mob.setInRangeAttack(true);
	}

	public void stop() {
		super.stop();
		mob.setAggressive(false);
		mob.setInRangeAttack(false);
		this.mob.setTarget(null);
		this.seeTime = 0;
		if (this.mob.isUsingItem()) {
			this.mob.stopUsingItem();
			this.mob.setChargingCrossbow(false);
			CrossbowBehaviorRegistry.get(mob, mob.getUseItem()).ifPresent(e -> e.behavior().setCharged(this.mob.getUseItem(), false));
		}

	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	public void tick() {
		LivingEntity livingentity = this.mob.getTarget();
		if (livingentity != null) {
			boolean flag = this.mob.getSensing().hasLineOfSight(livingentity);
			boolean flag1 = this.seeTime > 0;
			if (flag != flag1) {
				this.seeTime = 0;
			}

			if (flag) {
				++this.seeTime;
			} else {
				--this.seeTime;
			}

			double d0 = this.mob.distanceToSqr(livingentity);
			boolean flag2 = (d0 > (double) this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
			if (flag2) {
				--this.updatePathDelay;
				if (this.updatePathDelay <= 0) {
					this.mob.getNavigation().moveTo(livingentity, this.canRun() ? this.speedModifier : this.speedModifier * 0.5D);
					this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(this.mob.getRandom());
				}
			} else {
				this.updatePathDelay = 0;
				this.mob.getNavigation().stop();
			}

			this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
			if (this.crossbowState == GolemCrossbowAttackGoal.CrossbowState.UNCHARGED) {
				if (!flag2 && !this.mob.getProjectile(this.mob.getItemInHand(mob.getWeaponHand())).isEmpty()) {
					this.mob.startUsingItem(mob.getWeaponHand());
					this.crossbowState = GolemCrossbowAttackGoal.CrossbowState.CHARGING;
					this.mob.setChargingCrossbow(true);
				}
			} else if (this.crossbowState == GolemCrossbowAttackGoal.CrossbowState.CHARGING) {
				if (!this.mob.isUsingItem()) {
					this.crossbowState = GolemCrossbowAttackGoal.CrossbowState.UNCHARGED;
				}

				int i = this.mob.getTicksUsingItem();
				ItemStack stack = this.mob.getUseItem();
				var weapon = CrossbowBehaviorRegistry.get(mob, stack);
				if (weapon.isPresent()) {
					if (i >= weapon.get().chargeDuration()) {
						this.mob.releaseUsingItem();
						this.crossbowState = GolemCrossbowAttackGoal.CrossbowState.CHARGED;
						this.attackDelay = 20 + this.mob.getRandom().nextInt(20);
						this.mob.setChargingCrossbow(false);
						weapon.get().behavior().setCharged(stack, true);
					}
				}
			} else if (this.crossbowState == GolemCrossbowAttackGoal.CrossbowState.CHARGED) {
				--this.attackDelay;
				if (this.attackDelay == 0) {
					this.crossbowState = GolemCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK;
				}
			} else if (this.crossbowState == GolemCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK && flag) {
				this.mob.performRangedAttack(livingentity, 1.0F);
				ItemStack stack = this.mob.getItemInHand(mob.getWeaponHand());
				CrossbowBehaviorRegistry.get(mob, stack).ifPresent(e -> e.behavior().setCharged(stack, false));
				this.crossbowState = GolemCrossbowAttackGoal.CrossbowState.UNCHARGED;
			}
		}
	}

	@Override
	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float dist, ItemStack stack, InteractionHand hand) {
		CrossbowBehaviorRegistry.get(mob, stack).ifPresent(e -> e.behavior().performRangedAttack(golem, target, dist, stack, hand));
	}

	private boolean canRun() {
		return this.crossbowState == GolemCrossbowAttackGoal.CrossbowState.UNCHARGED;
	}

	enum CrossbowState {
		UNCHARGED,
		CHARGING,
		CHARGED,
		READY_TO_ATTACK
	}

}
