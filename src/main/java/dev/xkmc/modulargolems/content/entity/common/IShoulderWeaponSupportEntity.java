package dev.xkmc.modulargolems.content.entity.common;

/**
 * Implemented by golem types that can carry shoulder mounted weapons.
 * <p>
 * The returned values are the slot indexes understood by {@code IGenericShoulderWeapon}: negative
 * indexes are the off-hand side, positive indexes are the main-hand side, and zero is a weapon
 * carried centered on the mount. A two shouldered golem returns {@code {-1, 1}} while a golem that
 * carries a single weapon on its back returns {@code {0}}.
 */
public interface IShoulderWeaponSupportEntity {

	int[] shoulderSlotIndexes();

}
