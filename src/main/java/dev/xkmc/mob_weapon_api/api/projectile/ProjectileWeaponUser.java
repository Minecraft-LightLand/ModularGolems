package dev.xkmc.mob_weapon_api.api.projectile;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.function.Predicate;

public interface ProjectileWeaponUser {

	LivingEntity user();

	/**
	 * Returns the preferred arrow matching specific criteria. <br>
	 * Special criteria usually includes everything in general criteria, plus a few arrows that supports only special slots
	 *
	 * @param special arrow search in special slots (offhand) only.
	 * @param general arrow search in all slots.
	 * @return the arrow stack this user attempts to use.
	 * Note that bows might not call this method at all and could opt to use its own arrow search algorithm.
	 * Entities may choose to ignore the standard searching procedure and return an arrow it deems fit,
	 * but such arrow should at least past the "special" test.
	 */
	ItemStack getPreferredProjectile(ItemStack weapon, Predicate<ItemStack> special, Predicate<ItemStack> general);

	default ItemStack getPreferredProjectile(ItemStack weapon) {
		return weapon.getItem() instanceof ProjectileWeaponItem item ? getPreferredProjectile(weapon,
				item.getSupportedHeldProjectiles(), item.getAllSupportedProjectiles()) : ItemStack.EMPTY;
	}

	/**
	 * Bypass arrow consumption and durability consumption of the bow.
	 *
	 * @return true for hostile mobs and creative player
	 */
	boolean bypassAllConsumption();

	boolean hasInfiniteArrow(ItemStack weapon, ItemStack ammo);

}
