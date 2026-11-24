package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.modulargolems.content.menu.equipment.OpenItemEquipmentMenuToServer;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.registry.OpenEquipmentMenuToServer;
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
		if (manager.token.isItemMode()) {
			// 物品界面里的傀儡只是临时 dummy，世界里没有实体，只能带着物品重新打开
			ModularGolems.HANDLER.toServer(OpenItemEquipmentMenuToServer.of(manager.token.source.copy(), manager.token.sourceSlot));
			return;
		}
		ModularGolems.HANDLER.toServer(new OpenEquipmentMenuToServer(manager.token.golem.getUUID(), OpenEquipmentMenuToServer.Type.EQUIPMENT));
	}

}
