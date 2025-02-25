package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.WeaponGoalsRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class GolemBowAttackGoal extends GolemRangedAttackGoal {

	private int attackTime = -1;

	public GolemBowAttackGoal(HumanoidGolemEntity mob, GolemMeleeGoal melee, double speed, double radius) {
		super(mob, melee, speed, radius * radius);
	}

	@Override
	public boolean mayActivate(HumanoidGolemEntity golem, ItemStack stack) {
		var weapon = WeaponGoalsRegistry.BOW.get(mob, stack);
		if (weapon.isEmpty()) return false;
		return weapon.get().hasProjectile(mob, stack);
	}

	@Override
	public void stop() {
		attackTime = -1;
	}

	public void tick() {
		doMelee();
		strafing();
		LivingEntity target = mob.getTarget();
		if (mob.isUsingItem() && target != null) {
			if (seeTime < -60) {
				mob.stopUsingItem();
			} else if (seeTime > 0) {
				var weapon = WeaponGoalsRegistry.BOW.get(mob, mob.getUseItem());
				int i = mob.getTicksUsingItem();
				if (weapon.isPresent() && i >= weapon.get().pullTime(mob, mob.getUseItem())) {
					mob.stopUsingItem();
					mob.performRangedAttack(target, weapon.get().powerForTime(i));
					attackTime = mob.getRandom().nextInt(5) + 5;
				}
			}
		} else if (--attackTime <= 0 && seeTime >= -60) {
			mob.startUsingItem(mob.getWeaponHand());
		}
	}


	@Override
	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float power, ItemStack stack, InteractionHand hand) {
		WeaponGoalsRegistry.BOW.get(golem, stack).ifPresent(e -> e.performRangedAttack(golem, target, power, stack, hand));
	}

}
