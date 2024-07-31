package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2menustacker.init.MouseCache;
import dev.xkmc.l2tabs.tabs.core.TabBase;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.registry.OpenEquipmentMenuToServer;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.chat.Component;

public class EquipmentTab extends TabBase<EquipmentGroup, EquipmentTab> {

	public EquipmentTab(int index, TabToken<EquipmentGroup, EquipmentTab> token, TabManager<EquipmentGroup> manager, Component title) {
		super(index, token, manager, title);
	}

	@Override
	public void onTabClicked() {
		MouseCache.cacheMousePos();
		ModularGolems.HANDLER.toServer(new OpenEquipmentMenuToServer(manager.token.golem.getUUID(), OpenEquipmentMenuToServer.Type.EQUIPMENT));
	}

}
