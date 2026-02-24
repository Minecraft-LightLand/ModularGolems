package dev.xkmc.modulargolems.init.data;

import com.tterrag.registrate.providers.RegistrateDataMapProvider;
import dev.xkmc.l2tabs.init.L2Tabs;
import dev.xkmc.l2tabs.init.data.AttrDispEntry;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import top.theillusivec4.curios.api.CuriosApi;

public class MGDataMapGen {

	public static void genDataMap(RegistrateDataMapProvider pvd) {
		pvd.builder(L2Tabs.ICON.reg())
				.add(GolemTabRegistry.CONFIG_TOGGLE.id(), GolemItems.CARD[0].get(), false)
				.add(GolemTabRegistry.CONFIG_ITEM.id(), Items.HOPPER, false)
				.add(GolemTabRegistry.CONFIG_TARGET.id(), Items.BELL, false)
				.add(GolemTabRegistry.CONFIG_PATH.id(), Items.DIRT_PATH, false)
				.add(GolemTabRegistry.EQUIPMENT.id(), Items.DIAMOND_CHESTPLATE, false)
				.add(GolemTabRegistry.ATTRIBUTE.id(), Items.IRON_SWORD, false)
				.add(GolemTabRegistry.TRACKER.id(), GolemItems.HOLDER_GOLEM.get(), false)
				.add(GolemTabRegistry.TRACKER_ALIVE.id(), Items.POPPY, false)
				.add(GolemTabRegistry.TRACKER_DEAD.id(), Items.WITHER_ROSE, false)
				.add(GolemTabRegistry.TRACKER_RETRIEVE.id(), GolemItems.RETRIEVAL_WAND.get(), false)
				.add(GolemTabRegistry.TABLE_DISINTEGRATE.id(), GolemItems.SLICING_AXE.get(), false)
				.add(GolemTabRegistry.TABLE_UPGRADE.id(), GolemItems.EMPTY_UPGRADE.get(), false)
				.add(GolemTabRegistry.TABLE_CRAFT.id(), Items.CRAFTING_TABLE, false)
				.add(GolemTabRegistry.TABLE_STONECUTTER.id(), Items.STONECUTTER, false)
				.add(GolemTabRegistry.TABLE_ANVIL.id(), Items.ANVIL, false)
				.add(GolemTabRegistry.TABLE_SMITHING.id(), Items.SMITHING_TABLE, false)
				.add(GolemTabRegistry.TABLE_GRINDSTONE.id(), Items.GRINDSTONE, false);

		pvd.builder(L2Tabs.ORDER.reg())
				.add(GolemTabRegistry.CONFIG_TOGGLE.id(), 0, false)
				.add(GolemTabRegistry.CONFIG_ITEM.id(), 10, false)
				.add(GolemTabRegistry.CONFIG_TARGET.id(), 20, false)
				.add(GolemTabRegistry.CONFIG_PATH.id(), 30, false)
				.add(GolemTabRegistry.EQUIPMENT.id(), 0, false)
				.add(GolemTabRegistry.ATTRIBUTE.id(), 10, false)
				.add(CurioCompatRegistry.get().tab.id(), 20, false,
						new ModLoadedCondition(CuriosApi.MODID))
				.add(GolemTabRegistry.TRACKER.id(), 3400, false)
				.add(GolemTabRegistry.TRACKER_ALIVE.id(), 100, false)
				.add(GolemTabRegistry.TRACKER_DEAD.id(), 200, false)
				.add(GolemTabRegistry.TRACKER_RETRIEVE.id(), 300, false)
				.add(GolemTabRegistry.TABLE_DISINTEGRATE.id(), 100, false)
				.add(GolemTabRegistry.TABLE_UPGRADE.id(), 200, false)
				.add(GolemTabRegistry.TABLE_CRAFT.id(), 300, false)
				.add(GolemTabRegistry.TABLE_STONECUTTER.id(), 400, false)
				.add(GolemTabRegistry.TABLE_ANVIL.id(), 500, false)
				.add(GolemTabRegistry.TABLE_SMITHING.id(), 600, false)
				.add(GolemTabRegistry.TABLE_GRINDSTONE.id(), 700, false);

		pvd.builder(L2Tabs.ATTRIBUTE_ENTRY.reg())
				.add(GolemTypes.GOLEM_REGEN.key(), new AttrDispEntry(false, 7100, 0), false)
				.add(GolemTypes.GOLEM_SWEEP.key(), new AttrDispEntry(false, 7200, 0), false)
				.add(GolemTypes.GOLEM_SIZE.key(), new AttrDispEntry(false, 7300, 0), false)
				.add(GolemTypes.GOLEM_JUMP.key(), new AttrDispEntry(false, 7400, 0), false)
		;

	}

}
