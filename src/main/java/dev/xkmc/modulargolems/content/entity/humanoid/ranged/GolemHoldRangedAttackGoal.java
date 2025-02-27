package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class GolemHoldRangedAttackGoal extends GolemRangedAttackGoal {

	private int attackTime = -1;

	public GolemHoldRangedAttackGoal(HumanoidGolemEntity mob, GolemMeleeGoal melee, double speed) {
		super(mob, melee, speed, 0);
	}

	@Override
	public boolean mayActivate(HumanoidGolemEntity golem, ItemStack stack) {
		var weapon = WeaponRegistry.HOLD.get(mob, stack);
		return weapon.isPresent() && weapon.get().isValid(new GolemUser(golem, null), stack);
	}

	@Override
	public void stop() {
		attackTime = -1;
	}


	@Override
	public double attackRadiusSqr() {
		ItemStack stack = mob.getItemInHand(mob.getWeaponHand());
		var weapon = WeaponRegistry.HOLD.get(mob, stack);
		if (weapon.isPresent()) {
			double rad = weapon.get().range(mob, stack);
			return rad * rad;
		}
		return 0;
	}

	public void tick() {
		doMelee();
		strafing();
		ItemStack stack = mob.getItemInHand(mob.getWeaponHand());
		var weapon = WeaponRegistry.HOLD.get(mob, stack);
		if (weapon.isEmpty()) return;
		LivingEntity target = mob.getTarget();
		var user = new GolemUser(mob, target);
		if (mob.isUsingItem() && target != null) {
			if (seeTime < -60) {
				mob.stopUsingItem();
			} else if (seeTime > 0) {
				int i = mob.getTicksUsingItem();
				if (i >= weapon.get().holdTime(mob, stack)) {
					attackTime = weapon.get().trigger(user, stack, target, i);
					mob.stopUsingItem();
				} else {
					weapon.get().tickUsing(user, stack, i);
				}
			}
		} else if (--attackTime <= 0 && seeTime >= -60 && target != null &&
				mob.distanceTo(target) < weapon.get().range(mob, stack)) {
			mob.startUsingItem(mob.getWeaponHand());
		}
	}


	@Override
	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float power, ItemStack stack, InteractionHand hand) {
	}

}
