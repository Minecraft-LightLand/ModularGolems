package dev.xkmc.modulargolems.compat.materials.blazegear;

import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class BGCompatRegistry {

	public static final RegistryEntry<BlazingModifier> BLAZING;

	static {
		BLAZING = reg("blazing", BlazingModifier::new, "Shoot fireballs when approaching target.");
	}

	public static void register() {

	}

}
