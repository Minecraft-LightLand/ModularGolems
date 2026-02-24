package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.l2core.init.reg.simple.SR;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.l2tabs.init.L2Tabs;
import dev.xkmc.l2tabs.tabs.core.TabGroup;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.l2tabs.tabs.core.TabType;
import dev.xkmc.l2tabs.tabs.inventory.InvTabData;
import dev.xkmc.modulargolems.content.client.tracker.GolemInvTab;
import dev.xkmc.modulargolems.content.client.tracker.TrackerTab;
import dev.xkmc.modulargolems.content.menu.attribute.AttributeTab;
import dev.xkmc.modulargolems.content.menu.config.ConfigToggleTab;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentTab;
import dev.xkmc.modulargolems.content.menu.filter.ConfigItemTab;
import dev.xkmc.modulargolems.content.menu.path.ConfigPathTab;
import dev.xkmc.modulargolems.content.menu.table.TableTab;
import dev.xkmc.modulargolems.content.menu.table.TableTabType;
import dev.xkmc.modulargolems.content.menu.target.ConfigTargetTab;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.resources.ResourceLocation;

public class GolemTabRegistry {

	public static final ResourceLocation DUMMY = L2Tabs.loc(ModularGolems.MODID);

	public static final TabGroup<ConfigGroup> CONFIG = new TabGroup<>(TabType.RIGHT, 8, false);
	public static final TabGroup<EquipmentGroup> EQUIPMENTS = new TabGroup<>(TabType.RIGHT, 8, false);
	public static final TabGroup<TrackerGroup> TRACKERS = new TabGroup<>(TabType.RIGHT, 8, false);
	public static final TabGroup<TableGroup> TABLE = new TabGroup<>(TabType.ABOVE, 7, true);

	public static final SR<TabToken<?, ?>> TAB_REG = SR.of(ModularGolems.REG, L2Tabs.TABS.reg());

	public static final Val<TabToken<ConfigGroup, ConfigToggleTab>> CONFIG_TOGGLE =
			TAB_REG.reg("toggles", () -> CONFIG.registerTab(
					() -> ConfigToggleTab::new, MGLangData.TAB_TOGGLE.get()));

	public static final Val<TabToken<ConfigGroup, ConfigItemTab>> CONFIG_ITEM =
			TAB_REG.reg("item_filter", () -> CONFIG.registerTab(
					() -> ConfigItemTab::new, MGLangData.TAB_PICKUP.get()));

	public static final Val<TabToken<ConfigGroup, ConfigTargetTab>> CONFIG_TARGET =
			TAB_REG.reg("target_filter", () -> CONFIG.registerTab(
					() -> ConfigTargetTab::new, MGLangData.TAB_TARGET.get()));

	public static final Val<TabToken<ConfigGroup, ConfigPathTab>> CONFIG_PATH =
			TAB_REG.reg("path", () -> CONFIG.registerTab(
					() -> ConfigPathTab::new, MGLangData.TAB_PATH.get()));

	public static final Val<TabToken<EquipmentGroup, EquipmentTab>> EQUIPMENT =
			TAB_REG.reg("equipment", () -> EQUIPMENTS.registerTab(
					() -> EquipmentTab::new, MGLangData.TAB_EQUIPMENT.get()));

	public static final Val<TabToken<EquipmentGroup, AttributeTab>> ATTRIBUTE =
			TAB_REG.reg("attribute", () -> EQUIPMENTS.registerTab(
					() -> AttributeTab::new, MGLangData.TAB_ATTRIBUTE.get()));

	public static final Val<TabToken<InvTabData, GolemInvTab>> TRACKER =
			TAB_REG.reg("golem", () -> L2Tabs.GROUP.registerTab(
					() -> GolemInvTab::new, MGLangData.TAB_ALIVE.get()));

	public static final Val<TabToken<TrackerGroup, TrackerTab>> TRACKER_ALIVE =
			TAB_REG.reg("golem_alive", () -> TRACKERS.registerTab(
					() -> TrackerTab.Type.ALIVE::create, MGLangData.TAB_ALIVE.get()));

	public static final Val<TabToken<TrackerGroup, TrackerTab>> TRACKER_DEAD =
			TAB_REG.reg("golem_dead", () -> TRACKERS.registerTab(
					() -> TrackerTab.Type.DEAD::create, MGLangData.TAB_DEAD.get()));

	public static final Val<TabToken<TrackerGroup, TrackerTab>> TRACKER_RETRIEVE =
			TAB_REG.reg("golem_retrieve", () -> TRACKERS.registerTab(
					() -> TrackerTab.Type.RETRIEVE::create, MGLangData.TAB_RETRIEVE.get()));

	public static final Val<TabToken<TableGroup, TableTab>> TABLE_DISINTEGRATE =
			TAB_REG.reg("table_disassembly", () -> TABLE.registerTab(TableTabType.DISINTEGRATE, () -> TableTab::from));
	public static final Val<TabToken<TableGroup, TableTab>> TABLE_UPGRADE =
			TAB_REG.reg("table_upgrade", () -> TABLE.registerTab(TableTabType.UPGRADE, () -> TableTab::from));
	public static final Val<TabToken<TableGroup, TableTab>> TABLE_CRAFT =
			TAB_REG.reg("table_craft", () -> TABLE.registerTab(TableTabType.CRAFT, () -> TableTab::from));
	public static final Val<TabToken<TableGroup, TableTab>> TABLE_STONECUTTER =
			TAB_REG.reg("table_stonecutter", () -> TABLE.registerTab(TableTabType.STONECUTTER, () -> TableTab::from));
	public static final Val<TabToken<TableGroup, TableTab>> TABLE_ANVIL =
			TAB_REG.reg("table_anvil", () -> TABLE.registerTab(TableTabType.ANVIL, () -> TableTab::from));
	public static final Val<TabToken<TableGroup, TableTab>> TABLE_SMITHING =
			TAB_REG.reg("table_smithing", () -> TABLE.registerTab(TableTabType.SMITHING, () -> TableTab::from));
	public static final Val<TabToken<TableGroup, TableTab>> TABLE_GRINDSTONE =
			TAB_REG.reg("table_grindstone", () -> TABLE.registerTab(TableTabType.GRINDSTONE, () -> TableTab::from));

	public static void register() {
	}

}
