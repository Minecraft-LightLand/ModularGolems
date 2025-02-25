package dev.xkmc.projectile_api.example;

import dev.xkmc.projectile_api.api.ICrossbowBehavior;
import dev.xkmc.projectile_api.api.ProjectileWeaponContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public class SimpleCrossbowBehavior implements ICrossbowBehavior {

	@Override
	public int chargeTime(LivingEntity golem, ItemStack stack) {
		return CrossbowItem.getChargeDuration(stack);
	}

	@Override
	public void release(ItemStack stack) {
		CrossbowItem.setCharged(stack, false);
	}

	@Override
	public boolean tryCharge(LivingEntity golem, ItemStack stack) {
		return false;
	}

	@Override
	public void performRangedAttack(LivingEntity golem, ProjectileWeaponContext strategy, float dist, ItemStack stack, InteractionHand hand) {

	}

	@Override
	public boolean hasProjectile(LivingEntity mob, ItemStack stack) {
		return !mob.getProjectile(stack).isEmpty();
	}

	@Override
	public boolean hasLoadedProjectile(ItemStack stack) {
		return false;
	}
}
