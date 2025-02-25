package dev.xkmc.mob_weapon_api.integration.l2complements;

import dev.xkmc.l2complements.content.item.wand.SonicShooter;
import dev.xkmc.l2complements.init.registrate.LCItems;
import dev.xkmc.mob_weapon_api.api.simple.IHoldWeaponBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SonicShooterBehavior implements IHoldWeaponBehavior {

	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return LCItems.SONIC_SHOOTER.get().getDistance(stack) - 2;
	}

	@Override
	public int holdTime(LivingEntity user, ItemStack stack) {
		return LCItems.SONIC_SHOOTER.get().getUseDuration(stack) - 1;
	}

	@Override
	public int trigger(LivingEntity user, ItemStack stack, LivingEntity target, int time) {
		if (user.level() instanceof ServerLevel sl) {
			Vec3 dst = target.getEyePosition();
			Vec3 src = user.getEyePosition();
			Vec3 dir = dst.subtract(src).normalize();
			SonicShooter.shoot(sl, user, stack, src, dir);
		}
		return 5;
	}

}
