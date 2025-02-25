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
		return weapon.get().hasProjectile(new GolemUser(mob, null), stack);
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
			var user = new GolemUser(mob, target);
			double dist = mob.distanceTo(target);
			if (seeTime < -60) {
				mob.stopUsingItem();
			} else if (seeTime > 0) {
				ItemStack stack = mob.getUseItem();
				var weapon = WeaponGoalsRegistry.BOW.get(mob, stack);
				int i = mob.getTicksUsingItem();
				if (weapon.isPresent() && i >= weapon.get().getPreferredPullTime(user, stack, dist)) {
					mob.stopUsingItem();
					mob.performRangedAttack(target, weapon.get().getPowerForTime(user, stack, i));
					attackTime = mob.getRandom().nextInt(5) + 5;
				}
			}
		} else if (--attackTime <= 0 && seeTime >= -60) {
			mob.startUsingItem(mob.getWeaponHand());
		}
	}


	@Override
	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float power, ItemStack stack, InteractionHand hand) {
		WeaponGoalsRegistry.BOW.get(golem, stack).ifPresent(e -> e.shootArrow(new GolemUser(golem, target), power, stack, hand));
	}

}
