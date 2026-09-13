package dev.xkmc.modulargolems.compat.materials.royalvariation;

import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.modulargolems.content.core.StatFilterType;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class RVCompatRegistry {

	public static final Val<CalvaryModifier> CALVARY;
	public static final Val<MarkingModifier> MARKING;

	static {
		CALVARY = reg("calvary", () -> new CalvaryModifier(StatFilterType.HEALTH, 3),
				"Calvary", "Apply royal blessing to allies when damaged");

		MARKING = reg("marking", () -> new MarkingModifier(StatFilterType.ATTACK, 3),
				"Marking", "Inflict marked effect on hit");

	}

	public static void register() {
	}

}
