package dev.xkmc.mob_weapon_api.integration.l2complements;

import dev.xkmc.l2complements.content.item.wand.HellfireWand;
import dev.xkmc.mob_weapon_api.api.simple.IHoldWeaponBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class HellfireWandBehavior implements IHoldWeaponBehavior {

	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return 25;
	}

	@Override
	public int holdTime(LivingEntity user, ItemStack stack) {
		return 60;
	}

	@Override
	public int trigger(LivingEntity user, ItemStack stack, LivingEntity target, int time) {
		if (user.level() instanceof ServerLevel sl) {
			HellfireWand.trigger(user, sl, target.position(), time);
		}
		return 10;
	}

	@Override
	public void tickUsing(LivingEntity user, ItemStack stack, int time) {
		var target = user instanceof Mob mob ? mob.getTarget() : null;
		if (target == null) return;
		HellfireWand.renderRegionServer(user, target.position(), time);
	}

}
