package dev.xkmc.mob_weapon_api.integration.l2complements;

import dev.xkmc.l2complements.content.item.wand.WinterStormWand;
import dev.xkmc.mob_weapon_api.api.simple.IHoldWeaponBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class WinterstormWandBehavior implements IHoldWeaponBehavior {

	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return 6;
	}

	@Override
	public int holdTime(LivingEntity user, ItemStack stack) {
		return 10000;
	}

	@Override
	public int trigger(LivingEntity user, ItemStack stack, LivingEntity target, int time) {
		return 1;
	}

	@Override
	public void tickUsing(LivingEntity user, ItemStack stack, int time) {
		WinterStormWand.tickServer(user, user.level(), user.position(), time);
		if (time % 20 == 0) {
			stack.hurtAndBreak(1, user, e -> e.broadcastBreakEvent(user.getUsedItemHand()));
		}
	}

}
