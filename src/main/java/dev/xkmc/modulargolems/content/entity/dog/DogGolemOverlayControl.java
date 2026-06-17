package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.modulargolems.content.client.overlay.GolemStatusOverlay;
import dev.xkmc.modulargolems.content.core.GolemOverlayControl;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;

public class DogGolemOverlayControl extends GolemOverlayControl<DogGolemEntity> {

	public DogGolemOverlayControl(DogGolemEntity golem) {
		super(golem);
	}

	public void renderImage(GolemStatusOverlay.GolemEquipmentTooltip tooltip, Font font, int mx, int my, GuiGraphics g) {
		tooltip.renderSlot(g, mx, my, golem.getItemBySlot(EquipmentSlot.HEAD), "altas_helmet");
		tooltip.renderSlot(g, mx, my + 18, golem.getItemBySlot(EquipmentSlot.CHEST), "slotbg_dog_armor");
	}

	public int getHeight() {
		return 38;
	}

	public int getWidth(Font pFont) {
		return 18;
	}

}
