package dev.xkmc.modulargolems.compat.materials.royalvariation;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.content.core.StatFilterType;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class RVCompatRegistry {

	public static final RegistryEntry<CalvaryModifier> CALVARY;
	public static final RegistryEntry<MarkingModifier> MARKING;

	static {
		CALVARY = reg("calvary", () -> new CalvaryModifier(StatFilterType.HEALTH, 3),
				"Calvary", "Apply royal blessing to allies when damaged");

		MARKING = reg("marking", () -> new MarkingModifier(StatFilterType.ATTACK, 3),
				"Marking", "Inflict marked effect on hit");

	}

	public static void register() {
	}

}
