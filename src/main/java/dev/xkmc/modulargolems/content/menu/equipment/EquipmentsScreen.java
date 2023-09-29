package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2library.base.menu.base.BaseContainerScreen;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EquipmentsScreen extends BaseContainerScreen<EquipmentsMenu> implements ITabScreen {

	public EquipmentsScreen(EquipmentsMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void renderBg(GuiGraphics g, float pTick, int mx, int my) {
		var sr = menu.sprite.get().getRenderer(this);
		sr.start(g);
		if (menu.getAsPredSlot("hand", 0, 1).getItem().isEmpty())
			sr.draw(g, "hand", "altas_shield", 1, 19);
		if (menu.getAsPredSlot("armor", 0, 0).getItem().isEmpty())
			sr.draw(g, "armor", "altas_helmet", 1, 1);
		if (menu.getAsPredSlot("armor", 0, 1).getItem().isEmpty())
			sr.draw(g, "armor", "altas_chestplate", 1, 1 + 18);
		if (menu.getAsPredSlot("armor", 0, 2).getItem().isEmpty())
			sr.draw(g, "armor", "altas_leggings", 1, 1 + 18 * 2);
		if (menu.getAsPredSlot("armor", 0, 3).getItem().isEmpty())
			sr.draw(g, "armor", "altas_boots", 1, 1 + 18 * 3);
	}

	@Override
	public int screenWidth() {
		return getXSize();
	}

	@Override
	public int screenHeight() {
		return getYSize();
	}

}
