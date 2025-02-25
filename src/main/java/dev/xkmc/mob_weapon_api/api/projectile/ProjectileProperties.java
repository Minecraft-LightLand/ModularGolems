package dev.xkmc.mob_weapon_api.api.projectile;

public record ProjectileProperties(
		float velocity,
		float gravity,
		float inaccuracy,
		boolean infinite
) {
}
