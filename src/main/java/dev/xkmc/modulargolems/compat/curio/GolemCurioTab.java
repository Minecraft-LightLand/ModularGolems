package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.registry.OpenEquipmentMenuToServer;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabBase;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.Curios;

public class GolemCurioTab extends GolemTabBase<EquipmentGroup, GolemCurioTab> {

	public GolemCurioTab(int index, GolemTabToken<EquipmentGroup, GolemCurioTab> token, GolemTabManager<EquipmentGroup> manager, ItemStack stack, Component title) {
		super(index, token, manager, stack, title);
	}

	@Override
	public void onTabClicked() {
		ModularGolems.HANDLER.toServer(new OpenEquipmentMenuToServer(manager.token.golem.getUUID(), OpenEquipmentMenuToServer.Type.CURIOS));
	}

	@Override
	protected void renderIcon(GuiGraphics g) {
		g.blit(new ResourceLocation(Curios.MODID, "textures/gui/inventory.png"),
				getX() + 7, getY() + 7, 50, 14, 14, 14);
	}

}
