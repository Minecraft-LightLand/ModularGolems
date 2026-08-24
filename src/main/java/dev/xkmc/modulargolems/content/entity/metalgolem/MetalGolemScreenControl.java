package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.l2core.base.menu.base.LayoutRenderer;
import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemScreenControl;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class MetalGolemScreenControl extends SweepGolemScreenControl<MetalGolemEntity> {

	public MetalGolemScreenControl(GolemMenuControl<MetalGolemEntity> ctrl) {
		super(ctrl);
	}

	public void render(LayoutRenderer sr, GuiGraphicsExtractor g, float pTick) {
		super.render(sr, g, pTick);
		sr.draw(g, "left_shoulder", "slot", -1, -1);
		sr.draw(g, "right_shoulder", "slot", -1, -1);
		if (menu.getAsPredSlot("left_shoulder", 0, 0).getItem().isEmpty())
			sr.draw(g, "left_shoulder", "slotbg_shoulder", -1, -1);
		if (menu.getAsPredSlot("right_shoulder", 0, 0).getItem().isEmpty())
			sr.draw(g, "right_shoulder", "slotbg_shoulder", -1, -1);
	}

	public List<Component> addSlotTooltip(Slot hoveredSlot) {
		if (hoveredSlot.getContainerSlot() == 0) {
			return List.of(MGLangData.SLOT_MAIN.get(),
					MGLangData.SLOT_MAIN_DESC_METAL.get());
		}
		if (hoveredSlot.getContainerSlot() == 1) {
			return List.of(MGLangData.SLOT_OFF.get());
		}
		if (hoveredSlot.getContainerSlot() == 6) {
			return List.of(MGLangData.SLOT_BACKUP.get(),
					MGLangData.SLOT_BACKUP_DESC.get(),
					MGLangData.SLOT_BACKUP_INFO.get());
		}
		if (hoveredSlot.getContainerSlot() == 7) {
			return List.of(MGLangData.SLOT_ARROW.get(),
					MGLangData.SLOT_ARROW_DESC.get());
		}
		if (hoveredSlot.getContainerSlot() == 8) {
			return List.of(MGLangData.SLOT_SHOULDER.get());
		}
		if (hoveredSlot.getContainerSlot() == 9) {
			return List.of(MGLangData.SLOT_SHOULDER.get());
		}
		return List.of();
	}

}
