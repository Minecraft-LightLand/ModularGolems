package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.l2menustacker.init.MouseCache;
import dev.xkmc.l2tabs.init.L2Tabs;
import dev.xkmc.l2tabs.tabs.core.TabBase;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.registry.OpenEquipmentMenuToServer;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GolemCurioTab extends TabBase<EquipmentGroup, GolemCurioTab> {

	private static final ResourceLocation ICON = L2Tabs.loc("curios");

	public GolemCurioTab(int index, TabToken<EquipmentGroup, GolemCurioTab> token, TabManager<EquipmentGroup> manager, Component title) {
		super(index, token, manager, title);
	}

	@Override
	public void onTabClicked() {
		MouseCache.cacheMousePos();
		ModularGolems.HANDLER.toServer(new OpenEquipmentMenuToServer(manager.token.golem.getUUID(), OpenEquipmentMenuToServer.Type.CURIOS));
	}

	@Override
	protected void renderIcon(GuiGraphics g) {
		g.blitSprite(ICON, getX() + 7, getY() + 7, 14, 14);
	}

}
