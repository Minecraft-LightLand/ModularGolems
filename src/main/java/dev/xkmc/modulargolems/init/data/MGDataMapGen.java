package dev.xkmc.modulargolems.init.data;

import com.tterrag.registrate.providers.RegistrateDataMapProvider;
import dev.xkmc.l2tabs.init.L2Tabs;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.Items;

public class MGDataMapGen {

	public static void genDataMap(RegistrateDataMapProvider pvd) {
		pvd.builder(L2Tabs.ICON.reg())
				.add(GolemTabRegistry.CONFIG_TOGGLE.id(), GolemItems.CARD[0].get(), false)
				.add(GolemTabRegistry.CONFIG_ITEM.id(), Items.HOPPER, false)
				.add(GolemTabRegistry.CONFIG_TARGET.id(), Items.BELL, false)
				.add(GolemTabRegistry.CONFIG_PATH.id(), Items.DIRT_PATH, false)
				.add(GolemTabRegistry.EQUIPMENT.id(), Items.DIAMOND_CHESTPLATE, false)
				.add(GolemTabRegistry.ATTRIBUTE.id(), Items.IRON_SWORD, false);
		
		pvd.builder(L2Tabs.ORDER.reg())
				.add(GolemTabRegistry.CONFIG_TOGGLE.id(), 0, false)
				.add(GolemTabRegistry.CONFIG_ITEM.id(), 10, false)
				.add(GolemTabRegistry.CONFIG_TARGET.id(), 20, false)
				.add(GolemTabRegistry.CONFIG_PATH.id(), 30, false)
				.add(GolemTabRegistry.EQUIPMENT.id(), 0, false)
				.add(GolemTabRegistry.ATTRIBUTE.id(), 10, false)
				.add(CurioCompatRegistry.get().tab.id(), 20, false);

	}

}
