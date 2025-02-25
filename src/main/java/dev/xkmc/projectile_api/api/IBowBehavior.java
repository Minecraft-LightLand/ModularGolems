package dev.xkmc.projectile_api.api;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public interface IBowBehavior {

	boolean hasProjectile(ProjectileWeaponUser user, ItemStack stack);

	void shootArrow(ProjectileWeaponContext user, float power, ItemStack stack, InteractionHand hand);

	default int getPreferredPullTime(ProjectileWeaponContext user, ItemStack stack, double distToTarget) {
		return getStandardPullTime(user, stack);
	}

	int getStandardPullTime(ProjectileWeaponContext user, ItemStack stack);

	float getPowerForTime(ProjectileWeaponContext user, ItemStack stack, int pullTime);

}
