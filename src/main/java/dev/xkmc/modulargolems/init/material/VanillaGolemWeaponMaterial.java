package dev.xkmc.modulargolems.init.material;

import java.util.Locale;

public enum VanillaGolemWeaponMaterial implements IGolemWeaponMaterial {
	IRON(6, false),
	DIAMOND(8, false),
	NETHERITE(10, true);

	private final int damage;
	private final boolean fireResistant;

	VanillaGolemWeaponMaterial(int damage, boolean fireResistant) {
		this.damage = damage;
		this.fireResistant = fireResistant;
	}

	@Override
	public int getDamage() {
		return damage;
	}

	@Override
	public String getName() {
		return name().toLowerCase(Locale.ROOT);
	}

	@Override
	public boolean fireResistant() {
		return fireResistant;
	}

}
