package dev.xkmc.modulargolems.content.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2library.base.menu.BaseContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EquipmentsScreen extends BaseContainerScreen<EquipmentsMenu> {

	public EquipmentsScreen(EquipmentsMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void renderBg(PoseStack pose, float pTick, int mx, int my) {
		var sr = menu.sprite.getRenderer(this);
		sr.start(pose);
		if (menu.getAsPredSlot("hand", 0, 1).getItem().isEmpty())
			sr.draw(pose, "hand", "altas_shield", 1, 19);
		if (menu.getAsPredSlot("armor", 0, 0).getItem().isEmpty())
			sr.draw(pose, "armor", "altas_helmet", 1, 1);
		if (menu.getAsPredSlot("armor", 0, 1).getItem().isEmpty())
			sr.draw(pose, "armor", "altas_chestplate", 1, 1 + 18);
		if (menu.getAsPredSlot("armor", 0, 2).getItem().isEmpty())
			sr.draw(pose, "armor", "altas_leggings", 1, 1 + 18 * 2);
		if (menu.getAsPredSlot("armor", 0, 3).getItem().isEmpty())
			sr.draw(pose, "armor", "altas_boots", 1, 1 + 18 * 3);
	}

}
