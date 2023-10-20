package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2tabs.tabs.core.TabBase;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.registry.OpenEquipmentMenuToServer;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class EquipmentTab extends TabBase<EquipmentGroup, EquipmentTab> {

	public EquipmentTab(int index, TabToken<EquipmentGroup, EquipmentTab> token, TabManager<EquipmentGroup> manager,
						ItemStack stack, Component title) {
		super(index, token, manager, stack, title);
	}

	@Override
	public void onTabClicked() {
		ModularGolems.HANDLER.toServer(new OpenEquipmentMenuToServer(manager.token.golem.getUUID(),
				OpenEquipmentMenuToServer.Type.EQUIPMENT));
	}

}
