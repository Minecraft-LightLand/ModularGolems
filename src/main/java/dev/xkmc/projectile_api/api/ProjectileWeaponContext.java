package dev.xkmc.projectile_api.api;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public interface ProjectileWeaponContext extends ProjectileWeaponUser {

	/**
	 * Bypass arrow consumption and durability consumption of the bow.
	 *
	 * @return true for hostile mobs and creative player
	 */
	boolean bypassAllConsumption();

	boolean hasInfiniteArrow(ItemStack weapon, ItemStack ammo);

	/**
	 * Create default arrows for respective arrow stack, before modified by bow.
	 */
	default AbstractArrow createArrow(ItemStack ammo, float velocity) {
		return ProjectileUtil.getMobArrow(user(), ammo, velocity);
	}

	/**
	 * @return aiming result based on input parameters.
	 */
	AimResult aim(Vec3 arrowOrigin, float velocity, float gravity, float inaccuracy);

	float getInitialVelocityFactor();

	float getInitialInaccuracy();

	interface AimResult {

		/**
		 * Shoot the projectile based on the aim result, with angle offset for multi-shot features
		 */
		void shoot(Projectile projectile, float angleInDegree);

	}

}
