package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.modulargolems.content.menu.attribute.AttributeTab;
import dev.xkmc.modulargolems.content.menu.config.ConfigToggleTab;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentTab;
import dev.xkmc.modulargolems.content.menu.ghost.ConfigItemTab;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class GolemTabRegistry {

	public static final List<GolemTabToken<ConfigGroup, ?>> LIST_CONFIG = new ArrayList<>();
	public static final List<GolemTabToken<EquipmentGroup, ?>> LIST_EQUIPMENT = new ArrayList<>();

	public static final GolemTabToken<ConfigGroup, ConfigToggleTab> CONFIG_TOGGLE =
			new GolemTabToken<>(ConfigToggleTab::new, GolemItems.CARD[0]::get, Component.literal("toggle"));//TODO

	public static final GolemTabToken<ConfigGroup, ConfigItemTab> CONFIG_ITEM =
			new GolemTabToken<>(ConfigItemTab::new, () -> Items.HOPPER, Component.literal("item"));

	public static final GolemTabToken<EquipmentGroup, EquipmentTab> EQUIPMENT =
			new GolemTabToken<>(EquipmentTab::new, GolemItems.COMMAND_WAND::get, Component.literal("equipment"));

	public static final GolemTabToken<EquipmentGroup, AttributeTab> ATTRIBUTE =
			new GolemTabToken<>(AttributeTab::new, () -> Items.IRON_SWORD, Component.literal("attribute"));

	public static void register() {
		LIST_CONFIG.add(CONFIG_TOGGLE);
		LIST_CONFIG.add(CONFIG_ITEM);
		LIST_EQUIPMENT.add(EQUIPMENT);
		LIST_EQUIPMENT.add(ATTRIBUTE);
	}

}
