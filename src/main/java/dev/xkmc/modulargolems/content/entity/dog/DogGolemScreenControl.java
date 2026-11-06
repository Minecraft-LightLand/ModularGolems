package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.core.GolemScreenControl;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class DogGolemScreenControl extends GolemScreenControl<DogGolemEntity> {

	public DogGolemScreenControl(GolemMenuControl<DogGolemEntity> ctrl) {
		super(ctrl);
	}

	public void render(MenuLayoutConfig.ScreenRenderer sr, GuiGraphics g, float pTick) {
		sr.draw(g, "right_hand", "slot", -1, -1);
		sr.draw(g, "legs", "slot", -1, -1);
		sr.draw(g, "backup", "slot", -1, -1);
		sr.draw(g, "feet", "slot", -1, -1);
		sr.draw(g, "arrow_1", "slot", -1, -1);
		sr.draw(g, "arrow_2", "slot", -1, -1);
		sr.draw(g, "chest", "slot", -1, -1);
		if (menu.isSlotEmpty("right_hand"))
			sr.draw(g, "right_hand", "slotbg_banner", -1, -1);
		if (menu.isSlotEmpty("legs"))
			sr.draw(g, "legs", "slotbg_dog_armor", -1, -1);
		if (menu.isSlotEmpty("backup"))
			sr.draw(g, "backup", "slotbg_skin", -1, -1);
		if (menu.isSlotEmpty("feet"))
			sr.draw(g, "feet", "slotbg_route", -1, -1);
		if (menu.isSlotEmpty("arrow_1"))
			sr.draw(g, "arrow_1", "slotbg_arrow", -1, -1);
		if (menu.isSlotEmpty("arrow_2"))
			sr.draw(g, "arrow_2", "slotbg_arrow", -1, -1);
		if (menu.isSlotEmpty("chest"))
			sr.draw(g, "chest", "slotbg_shoulder", -1, -1);
	}

	@Override
	public List<Component> addSlotTooltip(Slot hoveredSlot) {
		return switch (menu.getSlotNameAtMenuSlot(hoveredSlot.index)) {
			case "right_hand" -> List.of(MGLangData.SLOT_BANNER.get(), MGLangData.SLOT_BANNER_DESC.get());
			case "legs" -> List.of(MGLangData.SLOT_DOG_ARMOR.get(), MGLangData.SLOT_DOG_ARMOR_DESC.get());
			case "backup" -> List.of(MGLangData.SLOT_SKIN.get(), MGLangData.SLOT_SKIN_DESC.get());
			case "feet" -> List.of(MGLangData.SLOT_ROUTE.get(), MGLangData.SLOT_ROUTE_DESC.get());
			case "arrow_1", "arrow_2" -> List.of(MGLangData.SLOT_ARROW.get(), MGLangData.SLOT_ARROW_DESC.get());
			case "chest" -> List.of(MGLangData.SLOT_SHOULDER.get(), MGLangData.SLOT_SHOULDER_DESC.get());
			default -> List.of();
		};
	}

}
