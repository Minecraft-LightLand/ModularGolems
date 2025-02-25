package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.WeaponGoalsRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class GolemSimpleRangedAttackGoal extends GolemRangedAttackGoal {

	private int attackTime = -1;

	public GolemSimpleRangedAttackGoal(HumanoidGolemEntity mob, GolemMeleeGoal melee, double speed) {
		super(mob, melee, speed, 0);
	}

	@Override
	public void stop() {
		attackTime = -1;
	}

	@Override
	public double attackRadiusSqr() {
		ItemStack stack = mob.getItemInHand(mob.getWeaponHand());
		var weapon = WeaponGoalsRegistry.INSTANT.get(mob, stack);
		if (weapon.isPresent()) {
			double rad = weapon.get().range(mob, stack);
			return rad * rad;
		}
		return 0;
	}

	public void tick() {
		doMelee();
		strafing();
		if (attackTime > 0) {
			attackTime--;
			return;
		}
		ItemStack stack = mob.getItemInHand(mob.getWeaponHand());
		var weapon = WeaponGoalsRegistry.INSTANT.get(mob, stack);
		if (weapon.isEmpty()) return;
		LivingEntity target = mob.getTarget();
		if (seeTime > 0 && target != null) {
			attackTime = weapon.get().trigger(mob, stack, target);
		}
	}


	@Override
	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float power, ItemStack stack, InteractionHand hand) {
	}

}
