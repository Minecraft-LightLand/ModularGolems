package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.core.GolemScreenControl;
import net.minecraft.client.gui.GuiGraphics;

public class SweepGolemScreenControl<T extends SweepGolemEntity<T, ?>> extends GolemScreenControl<T> {

	public SweepGolemScreenControl(GolemMenuControl<T> ctrl) {
		super(ctrl);
	}

	public void render(MenuLayoutConfig.ScreenRenderer sr, GuiGraphics g, float pTick) {
		sr.draw(g, "right_hand", "slot", -1, -1);
		sr.draw(g, "left_hand", "slot", -1, -1);
		sr.draw(g, "head", "slot", -1, -1);
		sr.draw(g, "chest", "slot", -1, -1);
		sr.draw(g, "legs", "slot", -1, -1);
		sr.draw(g, "feet", "slot", -1, -1);
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

		sr.draw(g, "arrow_1", "slot", -1, -1);
		sr.draw(g, "arrow_2", "slot", -1, -1);
		sr.draw(g, "backup", "slot", -1, -1);
		if (menu.isSlotEmpty("arrow_1"))
			sr.draw(g, "arrow_1", "slotbg_arrow", -1, -1);
		if (menu.isSlotEmpty("arrow_2"))
			sr.draw(g, "arrow_2", "slotbg_arrow", -1, -1);
		if (menu.isSlotEmpty("backup"))
			sr.draw(g, "backup", "slotbg_bow", -1, -1);

		sr.draw(g, "banner", "slot", -1, -1);
		sr.draw(g, "skin", "slot", -1, -1);
		sr.draw(g, "route", "slot", -1, -1);
		if (menu.isSlotEmpty("banner"))
			sr.draw(g, "banner", "slotbg_banner", -1, -1);
		if (menu.isSlotEmpty("skin"))
			sr.draw(g, "skin", "slotbg_skin", -1, -1);
		if (menu.isSlotEmpty("route"))
			sr.draw(g, "route", "slotbg_route", -1, -1);
	}

}
