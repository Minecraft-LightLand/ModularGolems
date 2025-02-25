package dev.xkmc.mob_weapon_api.api.simple;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IInstantWeaponBehavior {

	double range(LivingEntity user, ItemStack stack);

	int trigger(LivingEntity user, ItemStack stack, LivingEntity target);

}
