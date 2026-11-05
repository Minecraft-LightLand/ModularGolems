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

	/**
	 * Mirrors the dog's own equipment screen, which has no head slot and repurposes the sword, bow and boot
	 * positions for the banner, the skin and the patrol path.
	 */
	public void renderImage(GolemStatusOverlay.GolemEquipmentTooltip tooltip, Font font, int mx, int my, GuiGraphics g) {
		tooltip.renderSlot(g, mx, my, golem.getBannerSlot().getItem(), "slotbg_banner");
		tooltip.renderSlot(g, mx + 18, my + 18, golem.getWrapperOfHand(EquipmentSlot.CHEST).getItem(), "slotbg_dog_armor");

		tooltip.renderSlot(g, mx + 18, my, golem.getShoulderSlot().getItem(), "slotbg_shoulder");

		tooltip.renderSlot(g, mx, my + 18, golem.getSkinSlot().getItem(), "slotbg_skin");
		tooltip.renderSlot(g, mx + 36, my + 18, golem.getArrowSlot().getItem(), "slotbg_arrow");

		tooltip.renderSlot(g, mx + 36, my + 36, golem.getArrowSlot2().getItem(), "slotbg_arrow");
		tooltip.renderSlot(g, mx + 18, my + 36, golem.getRouteSlot().getItem(), "slotbg_route");
	}

	public int getHeight() {
		return 56;
	}

	public int getWidth(Font pFont) {
		return 54;
	}

}
