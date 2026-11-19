package dev.xkmc.modulargolems.content.entity.humanoid;

import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemScreenControl;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class HumanoidGolemScreenControl extends SweepGolemScreenControl<HumanoidGolemEntity> {

	public HumanoidGolemScreenControl(GolemMenuControl<HumanoidGolemEntity> ctrl) {
		super(ctrl);
	}

	public List<Component> addSlotTooltip(Slot hoveredSlot) {
		if (hoveredSlot.getContainerSlot() == 0) {
			return List.of(MGLangData.SLOT_MAIN.get(),
					MGLangData.SLOT_MAIN_DESC.get());
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
		return List.of();
	}

}
