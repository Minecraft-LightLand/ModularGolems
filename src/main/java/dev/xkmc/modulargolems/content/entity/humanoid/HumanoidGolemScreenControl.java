package dev.xkmc.modulargolems.content.entity.humanoid;

import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemScreenControl;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class HumanoidGolemScreenControl extends SweepGolemScreenControl<HumanoidGolemEntity> {

	public HumanoidGolemScreenControl(GolemMenuControl<HumanoidGolemEntity> ctrl) {
		super(ctrl);
	}

	/**
	 * The banner and the skin sit on the shoulder positions here, so the shared draw list cannot be used: it
	 * would paint slot backgrounds on the two banner and skin positions this golem does not have.
	 */
	public void render(MenuLayoutConfig.ScreenRenderer sr, GuiGraphics g, float pTick) {
		sr.draw(g, "left_hand", "slot", -1, -1);
		sr.draw(g, "right_hand", "slot", -1, -1);
		sr.draw(g, "head", "slot", -1, -1);
		sr.draw(g, "chest", "slot", -1, -1);
		sr.draw(g, "legs", "slot", -1, -1);
		sr.draw(g, "feet", "slot", -1, -1);
		sr.draw(g, "arrow_1", "slot", -1, -1);
		sr.draw(g, "arrow_2", "slot", -1, -1);
		sr.draw(g, "backup", "slot", -1, -1);
		sr.draw(g, "left_shoulder", "slot", -1, -1);
		sr.draw(g, "right_shoulder", "slot", -1, -1);
		sr.draw(g, "test", "slot", -1, -1);
		if (menu.isSlotEmpty("left_hand"))
			sr.draw(g, "left_hand", "altas_shield", 0, 0);
		if (menu.isSlotEmpty("right_hand"))
			sr.draw(g, "right_hand", "slotbg_sword", -1, -1);
		if (menu.isSlotEmpty("head"))
			sr.draw(g, "head", "altas_helmet", 0, 0);
		if (menu.isSlotEmpty("chest"))
			sr.draw(g, "chest", "altas_chestplate", 0, 0);
		if (menu.isSlotEmpty("legs"))
			sr.draw(g, "legs", "altas_leggings", 0, 0);
		if (menu.isSlotEmpty("feet"))
			sr.draw(g, "feet", "altas_boots", 0, 0);
		if (menu.isSlotEmpty("arrow_1"))
			sr.draw(g, "arrow_1", "slotbg_arrow", -1, -1);
		if (menu.isSlotEmpty("arrow_2"))
			sr.draw(g, "arrow_2", "slotbg_arrow", -1, -1);
		if (menu.isSlotEmpty("backup"))
			sr.draw(g, "backup", "slotbg_bow", -1, -1);
		if (menu.isSlotEmpty("left_shoulder"))
			sr.draw(g, "left_shoulder", "slotbg_banner", -1, -1);
		if (menu.isSlotEmpty("right_shoulder"))
			sr.draw(g, "right_shoulder", "slotbg_skin", -1, -1);
		if (menu.isSlotEmpty("test"))
			sr.draw(g, "test", "slotbg_route", -1, -1);
	}

	@Override
	public List<Component> addSlotTooltip(Slot hoveredSlot) {
		return switch (menu.getSlotNameAtMenuSlot(hoveredSlot.index)) {
			case "right_hand" -> List.of(MGLangData.SLOT_MAIN.get(), MGLangData.SLOT_MAIN_DESC.get());
			case "left_hand" -> List.of(MGLangData.SLOT_OFF.get(), MGLangData.SLOT_OFF_DESC.get());
			case "head", "chest", "legs", "feet" -> List.of(MGLangData.SLOT_ARMOR.get(),
					MGLangData.SLOT_ARMOR_DESC.get());
			case "backup" -> List.of(MGLangData.SLOT_BACKUP.get(), MGLangData.SLOT_BACKUP_DESC.get(),
					MGLangData.SLOT_BACKUP_INFO.get());
			case "arrow_1", "arrow_2" -> List.of(MGLangData.SLOT_ARROW.get(), MGLangData.SLOT_ARROW_DESC.get());
			case "left_shoulder" -> List.of(MGLangData.SLOT_BANNER.get(), MGLangData.SLOT_BANNER_DESC.get());
			case "right_shoulder" -> List.of(MGLangData.SLOT_SKIN.get(), MGLangData.SLOT_SKIN_DESC.get());
			case "test" -> List.of(MGLangData.SLOT_ROUTE.get(), MGLangData.SLOT_ROUTE_DESC.get());
			default -> super.addSlotTooltip(hoveredSlot);
		};
	}

}
