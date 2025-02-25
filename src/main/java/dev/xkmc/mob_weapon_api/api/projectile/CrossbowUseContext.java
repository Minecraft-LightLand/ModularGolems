package dev.xkmc.mob_weapon_api.api.projectile;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public interface CrossbowUseContext extends ProjectileWeaponUseContext {

	default float getCrossbowVelocity(List<ItemStack> ammo) {
		return !ammo.isEmpty() && ammo.get(0).is(Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
	}

}
