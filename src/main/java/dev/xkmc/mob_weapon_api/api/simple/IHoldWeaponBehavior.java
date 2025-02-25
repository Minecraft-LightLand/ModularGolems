package dev.xkmc.mob_weapon_api.api.simple;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IHoldWeaponBehavior {

	double range(LivingEntity user, ItemStack stack);

	int holdTime(LivingEntity user, ItemStack stack);

	int trigger(LivingEntity user, ItemStack stack, LivingEntity target, int time);

	default void tickUsing(LivingEntity user, ItemStack stack, int time) {

	}

}
