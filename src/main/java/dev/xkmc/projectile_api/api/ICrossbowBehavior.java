package dev.xkmc.projectile_api.api;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ICrossbowBehavior {

	int chargeTime(LivingEntity golem, ItemStack stack);

	void release(ItemStack stack);

	boolean tryCharge(LivingEntity golem, ItemStack stack);

	void performRangedAttack(LivingEntity golem, ProjectileWeaponContext strategy, float dist, ItemStack stack, InteractionHand hand);

	boolean hasProjectile(LivingEntity mob, ItemStack stack);

	boolean hasLoadedProjectile(ItemStack stack);

}
