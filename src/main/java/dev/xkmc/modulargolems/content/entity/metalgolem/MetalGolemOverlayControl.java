package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.modulargolems.content.client.overlay.GolemStatusOverlay;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemOverlayControl;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class MetalGolemOverlayControl extends SweepGolemOverlayControl<MetalGolemEntity> {

	public MetalGolemOverlayControl(MetalGolemEntity golem) {
		super(golem);
	}

	public void renderImage(GolemStatusOverlay.GolemEquipmentTooltip tooltip, Font font, int mx, int my, GuiGraphics g) {
		super.renderImage(tooltip, font, mx, my, g);
		tooltip.renderSlot(g, mx, my, golem.getRightShoulder().getItem(), "slotbg_shoulder");
		tooltip.renderSlot(g, mx + 36, my, golem.getLeftShoulder().getItem(), "slotbg_shoulder");
	}

}
