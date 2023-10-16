package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.modulargolems.content.menu.attribute.AttributeTab;
import dev.xkmc.modulargolems.content.menu.config.ConfigToggleTab;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentTab;
import dev.xkmc.modulargolems.content.menu.filter.ConfigItemTab;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class GolemTabRegistry {

	public static final List<GolemTabToken<ConfigGroup, ?>> LIST_CONFIG = new ArrayList<>();
	public static final List<GolemTabToken<EquipmentGroup, ?>> LIST_EQUIPMENT = new ArrayList<>();

	public static final GolemTabToken<ConfigGroup, ConfigToggleTab> CONFIG_TOGGLE =
			new GolemTabToken<>(ConfigToggleTab::new, GolemItems.CARD[0]::get, MGLangData.TAB_TOGGLE.get());

	public static final GolemTabToken<ConfigGroup, ConfigItemTab> CONFIG_ITEM =
			new GolemTabToken<>(ConfigItemTab::new, () -> Items.HOPPER, MGLangData.TAB_PICKUP.get());

	public static final GolemTabToken<EquipmentGroup, EquipmentTab> EQUIPMENT =
			new GolemTabToken<>(EquipmentTab::new, () -> Items.DIAMOND_CHESTPLATE, MGLangData.TAB_EQUIPMENT.get());

	public static final GolemTabToken<EquipmentGroup, AttributeTab> ATTRIBUTE =
			new GolemTabToken<>(AttributeTab::new, () -> Items.IRON_SWORD, MGLangData.TAB_ATTRIBUTE.get());

	public static void register() {
		LIST_CONFIG.add(CONFIG_TOGGLE);
		LIST_CONFIG.add(CONFIG_ITEM);
		LIST_EQUIPMENT.add(EQUIPMENT);
		LIST_EQUIPMENT.add(ATTRIBUTE);
	}

}
