package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.modulargolems.content.menu.tabs.GolemTabBase;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class EquipmentTab extends GolemTabBase<EquipmentGroup, EquipmentTab> {

	public EquipmentTab(int index, GolemTabToken<EquipmentGroup, EquipmentTab> token, GolemTabManager<EquipmentGroup> manager, ItemStack stack, Component title) {
		super(index, token, manager, stack, title);
	}

	@Override
	public void onTabClicked() {
		ModularGolems.HANDLER.toServer(new OpenEquipmentMenuToServer(manager.token.golem, OpenEquipmentMenuToServer.Type.EQUIPMENT));
	}

}
