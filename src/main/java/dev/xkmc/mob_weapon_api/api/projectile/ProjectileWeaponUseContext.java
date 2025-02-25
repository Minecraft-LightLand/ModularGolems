package dev.xkmc.mob_weapon_api.api.projectile;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public interface ProjectileWeaponUseContext extends ProjectileWeaponUser {

	float getInitialInaccuracy();

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

	interface AimResult {

		/**
		 * Shoot the projectile based on the aim result, with angle offset for multi-shot features
		 */
		void shoot(Projectile projectile, float angleInDegree);

	}

}
