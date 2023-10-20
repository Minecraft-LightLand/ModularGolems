package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.l2tabs.tabs.core.TabGroup;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.l2tabs.tabs.core.TabType;
import dev.xkmc.modulargolems.content.menu.attribute.AttributeTab;
import dev.xkmc.modulargolems.content.menu.config.ConfigToggleTab;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentTab;
import dev.xkmc.modulargolems.content.menu.filter.ConfigItemTab;
import dev.xkmc.modulargolems.content.menu.target.ConfigTargetTab;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.Items;

public class GolemTabRegistry {

	public static final TabGroup<ConfigGroup> CONFIG = new TabGroup<>(TabType.RIGHT);
	public static final TabGroup<EquipmentGroup> GOLEM = new TabGroup<>(TabType.RIGHT);

	public static final TabToken<ConfigGroup, ConfigToggleTab> CONFIG_TOGGLE =
			CONFIG.registerTab(1000, ConfigToggleTab::new, GolemItems.CARD[0]::get, MGLangData.TAB_TOGGLE.get());

	public static final TabToken<ConfigGroup, ConfigItemTab> CONFIG_ITEM =
			CONFIG.registerTab(2000, ConfigItemTab::new, () -> Items.HOPPER, MGLangData.TAB_PICKUP.get());

	public static final TabToken<ConfigGroup, ConfigTargetTab> CONFIG_TARGET =
			CONFIG.registerTab(3000, ConfigTargetTab::new, () -> Items.BELL, MGLangData.TAB_TARGET.get());

	public static final TabToken<EquipmentGroup, EquipmentTab> EQUIPMENT =
			GOLEM.registerTab(1000, EquipmentTab::new, () -> Items.DIAMOND_CHESTPLATE, MGLangData.TAB_EQUIPMENT.get());

	public static final TabToken<EquipmentGroup, AttributeTab> ATTRIBUTE =
			GOLEM.registerTab(2000, AttributeTab::new, () -> Items.IRON_SWORD, MGLangData.TAB_ATTRIBUTE.get());

	public static void register() {
	}

}
