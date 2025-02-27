package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class GolemSimpleRangedAttackGoal extends GolemRangedAttackGoal {

	private int attackTime = -1;

	public GolemSimpleRangedAttackGoal(HumanoidGolemEntity mob, GolemMeleeGoal melee, double speed) {
		super(mob, melee, speed, 0);
	}

	@Override
	public boolean mayActivate(HumanoidGolemEntity golem, ItemStack stack) {
		var weapon = WeaponRegistry.INSTANT.get(mob, stack);
		return weapon.isPresent() && weapon.get().isValid(new GolemUser(golem, null), stack);
	}

	@Override
	public void stop() {
		attackTime = -1;
	}

	@Override
	public double radius(ItemStack stack) {
		var weapon = WeaponRegistry.INSTANT.get(mob, stack);
		return weapon.map(b -> b.range(new GolemUser(mob, null), stack)).orElse(0.0);
	}

	public void tick() {
		doMelee();
		strafing();
		if (attackTime > 0) {
			attackTime--;
			return;
		}
		ItemStack stack = mob.getItemInHand(mob.getWeaponHand());
		var weapon = WeaponRegistry.INSTANT.get(mob, stack);
		if (weapon.isEmpty()) return;
		LivingEntity target = mob.getTarget();
		var user = new GolemUser(mob, target);
		if (seeTime > 0 && target != null &&
				mob.distanceTo(target) < weapon.get().range(user, stack)) {
			attackTime = weapon.get().trigger(user, stack, target);
		}
	}


	@Override
	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float power, ItemStack stack, InteractionHand hand) {
	}

}
