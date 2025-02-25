package dev.xkmc.projectile_api.api;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public interface IBowBehavior {

	boolean hasProjectile(ProjectileWeaponUser user, ItemStack stack);

	void shootArrow(BowUseContext user, float power, ItemStack stack, InteractionHand hand);

	default int getPreferredPullTime(BowUseContext user, ItemStack stack, double distToTarget) {
		return getStandardPullTime(user, stack);
	}

	int getStandardPullTime(BowUseContext user, ItemStack stack);

	float getPowerForTime(BowUseContext user, ItemStack stack, int pullTime);

}
