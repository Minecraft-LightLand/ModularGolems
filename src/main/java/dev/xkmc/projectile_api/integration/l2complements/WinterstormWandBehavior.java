package dev.xkmc.projectile_api.integration.l2complements;

import dev.xkmc.l2complements.content.item.wand.WinterStormWand;
import dev.xkmc.projectile_api.api.IHoldWeaponBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class WinterstormWandBehavior implements IHoldWeaponBehavior {

	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return 16;
	}

	@Override
	public int holdTime(LivingEntity user, ItemStack stack) {
		return 20;
	}

	@Override
	public int trigger(LivingEntity user, ItemStack stack, LivingEntity target, int time) {
		stack.hurtAndBreak(1, user, e -> e.broadcastBreakEvent(user.getUsedItemHand()));
		return 1;
	}

	@Override
	public void tickUsing(LivingEntity user, ItemStack stack, int time) {
		WinterStormWand.tickServer(user, user.level(), user.position(), time);
	}

}
