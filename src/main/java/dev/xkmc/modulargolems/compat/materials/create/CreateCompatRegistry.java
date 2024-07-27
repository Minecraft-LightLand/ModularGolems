package dev.xkmc.modulargolems.compat.materials.create;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class CreateCompatRegistry {

	public static final RegistryEntry<CoatingModifier> COATING;
	public static final RegistryEntry<AttributeGolemModifier> PUSH;

	public static final RegistryEntry<SimpleUpgradeItem> UP_COATING, UP_PUSH;

	static {
		COATING = reg("coating", CoatingModifier::new, "Reduce damage taken by %s");
		PUSH = reg("push", () -> new AttributeGolemModifier(1,
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ATKKB, () -> 1)
		)).register();
		UP_COATING = regModUpgrade("coating", () -> COATING, CreateDispatch.MODID).lang("Zinc Upgrade").register();
		UP_PUSH = regModUpgrade("push", () -> PUSH, CreateDispatch.MODID).lang("Extendo Upgrade").register();
	}

	public static void register() {

	}

}
