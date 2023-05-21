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
	}

}
