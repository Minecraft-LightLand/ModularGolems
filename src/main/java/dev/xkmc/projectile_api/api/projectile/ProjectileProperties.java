package dev.xkmc.projectile_api.api.projectile;

public record ProjectileProperties(
		float velocity,
		float gravity,
		float inaccuracy,
		boolean infinite
) {
}
