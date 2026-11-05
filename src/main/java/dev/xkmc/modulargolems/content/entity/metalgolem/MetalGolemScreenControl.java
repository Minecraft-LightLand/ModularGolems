package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemScreenControl;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class MetalGolemScreenControl extends SweepGolemScreenControl<MetalGolemEntity> {

	public MetalGolemScreenControl(GolemMenuControl<MetalGolemEntity> ctrl) {
		super(ctrl);
	}

	public void render(MenuLayoutConfig.ScreenRenderer sr, GuiGraphics g, float pTick) {
		super.render(sr, g, pTick);
		sr.draw(g, "left_shoulder", "slot", -1, -1);
		sr.draw(g, "right_shoulder", "slot", -1, -1);
		if (menu.isSlotEmpty("left_shoulder"))
			sr.draw(g, "left_shoulder", "slotbg_shoulder", -1, -1);
		if (menu.isSlotEmpty("right_shoulder"))
			sr.draw(g, "right_shoulder", "slotbg_shoulder", -1, -1);
	}

	public List<Component> addSlotTooltip(Slot hoveredSlot) {
		return switch (menu.getSlotNameAtMenuSlot(hoveredSlot.index)) {
			case "right_hand" -> List.of(MGLangData.SLOT_MAIN.get(), MGLangData.SLOT_MAIN_DESC_METAL.get());
			case "left_hand" -> List.of(MGLangData.SLOT_OFF.get(), MGLangData.SLOT_OFF_DESC_METAL.get());
			case "head", "chest", "legs", "feet" -> List.of(MGLangData.SLOT_ARMOR.get(),
					MGLangData.SLOT_ARMOR_DESC_METAL.get());
			case "backup" -> List.of(MGLangData.SLOT_BACKUP.get(), MGLangData.SLOT_BACKUP_DESC.get(),
					MGLangData.SLOT_BACKUP_INFO.get());
			case "arrow_1", "arrow_2" -> List.of(MGLangData.SLOT_ARROW.get(), MGLangData.SLOT_ARROW_DESC.get());
			case "right_shoulder", "left_shoulder" -> List.of(MGLangData.SLOT_SHOULDER.get(),
					MGLangData.SLOT_SHOULDER_DESC.get());
			default -> super.addSlotTooltip(hoveredSlot);
		};
	}

}
