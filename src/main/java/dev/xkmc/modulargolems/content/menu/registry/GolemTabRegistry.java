package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class GolemTabRegistry {

	public static final List<GolemTabToken<ConfigGroup, ?>> LIST_CONFIG = new ArrayList<>();
	public static final List<GolemTabToken<EquipmentGroup, ?>> LIST_EQUIPMENT = new ArrayList<>();

	public static void register() {
		LIST_CONFIG.add(new GolemTabToken<>(ConfigToggleTab::new, GolemItems.CARD[0]::get, Component.literal("toggle")));
		LIST_CONFIG.add(new GolemTabToken<>(ConfigItemTab::new, () -> Items.HOPPER, Component.literal("item")));
	}

}
