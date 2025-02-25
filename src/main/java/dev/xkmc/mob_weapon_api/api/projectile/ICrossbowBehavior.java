package dev.xkmc.mob_weapon_api.api.projectile;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ICrossbowBehavior {

	int chargeTime(LivingEntity user, ItemStack stack);

	void release(ItemStack stack);

	boolean tryCharge(LivingEntity user, ItemStack stack);

	void performRangedAttack(CrossbowUseContext user, ItemStack stack, InteractionHand hand);

	boolean hasProjectile(ProjectileWeaponUser user, ItemStack stack);

	default boolean hasLoadedProjectile(ItemStack stack) {
		return !getLoadedProjectile(stack).isEmpty();
	}

	List<ItemStack> getLoadedProjectile(ItemStack stack);

}
