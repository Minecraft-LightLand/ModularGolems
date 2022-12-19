package dev.xkmc.modulargolems.compat.materials.create;

import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.create.modifier.CoatingModifier;
import dev.xkmc.modulargolems.content.item.UpgradeItem;
import dev.xkmc.modulargolems.content.modifier.common.AttributeGolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class CreateCompatRegistry {

	public static final RegistryEntry<CoatingModifier> COATING;
	public static final RegistryEntry<AttributeGolemModifier> PUSH;

	public static final RegistryEntry<UpgradeItem> UP_COATING, UP_PUSH;

	static {
		COATING = reg("coating", CoatingModifier::new, "Reduce damage taken by %s");
		PUSH = reg("push", () -> new AttributeGolemModifier(1,
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ATKKB, 1)
		)).register();
		UP_COATING = regUpgrade("coating", () -> COATING).lang("Zinc Upgrade").register();
		UP_PUSH = regUpgrade("push", () -> PUSH).lang("Extendo Upgrade").register();
	}

	public static void register() {

	}

}
