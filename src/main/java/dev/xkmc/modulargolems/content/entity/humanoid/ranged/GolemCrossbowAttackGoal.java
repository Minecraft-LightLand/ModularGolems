package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class GolemCrossbowAttackGoal extends GolemRangedAttackGoal {
	private GolemCrossbowAttackGoal.CrossbowState crossbowState = GolemCrossbowAttackGoal.CrossbowState.UNCHARGED;
	private int attackDelay;

	public GolemCrossbowAttackGoal(HumanoidGolemEntity mob, GolemMeleeGoal melee, double speed, float radius) {
		super(mob, melee, speed, radius);
	}

	@Override
	public boolean mayActivate(HumanoidGolemEntity golem, ItemStack stack) {
		var weapon = WeaponRegistry.CROSSBOW.get(mob, stack);
		if (weapon.isEmpty()) return false;
		return weapon.get().hasProjectile(new GolemUser(mob, null), stack) ||
				weapon.get().hasLoadedProjectile(mob, stack);
	}

	public void stop() {
		super.stop();
		mob.setChargingCrossbow(false);
		attackDelay = 0;
	}

	public void tick() {
		doMelee();
		strafing();
		LivingEntity target = mob.getTarget();
		ItemStack stack = mob.getItemInHand(mob.getWeaponHand());
		var weapon = WeaponRegistry.CROSSBOW.get(mob, stack);
		if (weapon.isEmpty()) return;
		var user = new GolemUser(mob, target);
		var behavior = weapon.get();
		if (crossbowState == GolemCrossbowAttackGoal.CrossbowState.UNCHARGED) {
			if (behavior.hasLoadedProjectile(mob, stack)) {
				crossbowState = GolemCrossbowAttackGoal.CrossbowState.CHARGED;
				mob.setChargingCrossbow(false);
			} else if (behavior.hasProjectile(user, stack)) {
				if (behavior.chargeTime(mob, stack) <= 0) {
					if (weapon.get().tryCharge(user, stack)) {
						crossbowState = GolemCrossbowAttackGoal.CrossbowState.CHARGED;
					}
				} else {
					mob.startUsingItem(mob.getWeaponHand());
					crossbowState = GolemCrossbowAttackGoal.CrossbowState.CHARGING;
					mob.setChargingCrossbow(true);
				}
			}
		} else if (crossbowState == GolemCrossbowAttackGoal.CrossbowState.CHARGING) {
			if (!mob.isUsingItem()) {
				crossbowState = GolemCrossbowAttackGoal.CrossbowState.UNCHARGED;
			}
			if (mob.getTicksUsingItem() >= weapon.get().chargeTime(mob, stack)) {
				mob.releaseUsingItem();
				if (weapon.get().tryCharge(user, stack)) {
					crossbowState = GolemCrossbowAttackGoal.CrossbowState.CHARGED;
					mob.setChargingCrossbow(false);
				}
			}
		}

		if (target != null) {
			if (crossbowState == GolemCrossbowAttackGoal.CrossbowState.CHARGED) {
				if (attackDelay == 0) {
					attackDelay = 10;
				}
				--attackDelay;
				if (attackDelay == 0) {
					crossbowState = GolemCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK;
				}
			} else if (crossbowState == GolemCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK && seeTime > 0) {
				mob.performRangedAttack(target, 1.0F);
				behavior.release(stack);
				crossbowState = GolemCrossbowAttackGoal.CrossbowState.UNCHARGED;
			}
		}
	}

	@Override
	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float power, ItemStack stack, InteractionHand hand) {
		WeaponRegistry.CROSSBOW.get(mob, stack).ifPresent(e -> e.performRangedAttack(new GolemUser(golem, target), stack, hand));
	}

	enum CrossbowState {
		UNCHARGED,
		CHARGING,
		CHARGED,
		READY_TO_ATTACK
	}

}
