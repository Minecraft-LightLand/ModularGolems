package dev.xkmc.modulargolems.content.entity.humanoid;

import dev.xkmc.modulargolems.content.client.overlay.GolemStatusOverlay;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemOverlayControl;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;

public class HumanoidGolemOverlayControl extends SweepGolemOverlayControl<HumanoidGolemEntity> {

	public HumanoidGolemOverlayControl(HumanoidGolemEntity golem) {
		super(golem);
	}

	public void renderImage(GolemStatusOverlay.GolemEquipmentTooltip tooltip, Font font, int mx, int my, GuiGraphics g) {
		tooltip.renderSlot(g, mx + 18, my, golem.getItemBySlot(EquipmentSlot.HEAD), "altas_helmet");
		tooltip.renderSlot(g, mx + 18, my + 18, golem.getItemBySlot(EquipmentSlot.CHEST), "altas_chestplate");
		tooltip.renderSlot(g, mx + 18, my + 36, golem.getItemBySlot(EquipmentSlot.LEGS), "altas_leggings");
		tooltip.renderSlot(g, mx + 18, my + 54, golem.getItemBySlot(EquipmentSlot.FEET), "altas_boots");

		tooltip.renderSlot(g, mx, my + 18, golem.getItemBySlot(EquipmentSlot.MAINHAND), "slotbg_sword");
		tooltip.renderSlot(g, mx + 36, my + 18, golem.getItemBySlot(EquipmentSlot.OFFHAND), "altas_shield");

		tooltip.renderSlot(g, mx, my + 36, golem.getBackupHand().getItem(), "slotbg_bow");
		tooltip.renderSlot(g, mx + 36, my + 36, golem.getArrowSlot().getItem(), "slotbg_arrow");

		tooltip.renderSlot(g, mx + 36, my + 54, golem.getArrowSlot2().getItem(), "slotbg_arrow");
		tooltip.renderSlot(g, mx + 36, my, golem.getBannerSlot().getItem(), "slotbg_banner");

		tooltip.renderSlot(g, mx, my, golem.getSkinSlot().getItem(), "slotbg_skin");
		tooltip.renderSlot(g, mx, my + 54, golem.getRouteSlot().getItem(), "slotbg_route");
	}

	@Override
	public int getHeight() {
		return 74;
	}

	@Override
	public int getWidth(Font pFont) {
		return 54;
	}

}
