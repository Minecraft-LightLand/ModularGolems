package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.content.core.StatFilterType;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class LMCompatRegistry {

	public static final RegistryEntry<AncientAnchorModifier> ANCHOR;

	static {
		ANCHOR = reg("ancient_anchor", () -> new AncientAnchorModifier(StatFilterType.MASS, 4),
				"Ancient Anchor", "Deal fall attack, cause shockwaves, and stun targets");
	}

	public static void register() {
	}
}
