package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.l2complements.modifiers.ConduitModifier;

import static dev.xkmc.modulargolems.init.registrate.GolemModifierRegistry.reg;

public class LCCompatRegistry {

	public static final RegistryEntry<ConduitModifier> CONDUIT;

	static {
		CONDUIT = reg("conduit", ConduitModifier::new, "Boosts all stats in water, attacks mobs in water like conduit.");
	}

	public static void register() {

	}

}
