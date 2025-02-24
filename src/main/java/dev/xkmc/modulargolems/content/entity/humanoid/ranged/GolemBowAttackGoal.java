package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.bow.BowBehaviorRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

public class GolemBowAttackGoal extends GolemRangedAttackGoal {

	private int attackIntervalMin;
	private int attackTime = -1;

	public GolemBowAttackGoal(HumanoidGolemEntity mob, double speed, double radius, int interval) {
		super(mob, speed, radius * radius);
		attackIntervalMin = interval;
	}

	@Override
	public boolean mayActivate(HumanoidGolemEntity golem, ItemStack stack) {
		var weapon = BowBehaviorRegistry.get(mob, stack);
		if (weapon.isEmpty()) return false;
		return weapon.get().behavior().hasProjectile(mob, stack);
	}

	@Override
	public void stop() {
		attackTime = -1;
	}

	public void tick() {
		strafing();
		LivingEntity target = mob.getTarget();
		if (mob.isUsingItem() && target != null) {
			if (seeTime < -60) {
				mob.stopUsingItem();
			} else if (seeTime > 0) {
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


	@Override
	public void performRangedAttack(HumanoidGolemEntity golem, LivingEntity target, float dist, ItemStack stack, InteractionHand hand) {
		BowBehaviorRegistry.get(golem, stack).ifPresent(e -> e.behavior().performRangedAttack(golem, target, dist, stack, hand));
	}

}
