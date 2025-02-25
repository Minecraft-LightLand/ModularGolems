package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import java.util.Optional;

public record WeaponStatus(
		boolean isMelee,
		boolean isRanged,
		boolean isShield
) {

	public static final WeaponStatus MELEE = new WeaponStatus(true, false, false);
	public static final WeaponStatus RANGED = new WeaponStatus(false, true, false);
	public static final WeaponStatus SHIELD = new WeaponStatus(false, false, true);
	public static final WeaponStatus OFFENSIVE = new WeaponStatus(true, true, false);
	public static final WeaponStatus BATTLE_SHIELD = new WeaponStatus(true, false, true);

	public Optional<WeaponStatus> of(boolean enabled) {
		return enabled ? Optional.of(this) : Optional.empty();
	}

}
