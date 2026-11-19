package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2core.base.menu.base.MenuLayoutConfig;
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
		if (menu.getAsPredSlot("left_hand", 0, 0).getItem().isEmpty())
			sr.draw(g, "left_hand", "altas_shield", 0, 0);
		if (menu.getAsPredSlot("right_hand", 0, 0).getItem().isEmpty())
			sr.draw(g, "right_hand", "slotbg_sword", -1, -1);
		if (menu.getAsPredSlot("head", 0, 0).getItem().isEmpty())
			sr.draw(g, "head", "altas_helmet", 0, 0);
		if (menu.getAsPredSlot("chest", 0, 0).getItem().isEmpty())
			sr.draw(g, "chest", "altas_chestplate", 0, 0);
		if (menu.getAsPredSlot("legs", 0, 0).getItem().isEmpty())
			sr.draw(g, "legs", "altas_leggings", 0, 0);
		if (menu.getAsPredSlot("feet", 0, 0).getItem().isEmpty())
			sr.draw(g, "feet", "altas_boots", 0, 0);

		sr.draw(g, "arrow", "slot", -1, -1);
		sr.draw(g, "backup", "slot", -1, -1);
		if (menu.getAsPredSlot("arrow", 0, 0).getItem().isEmpty())
			sr.draw(g, "arrow", "slotbg_arrow", -1, -1);
		if (menu.getAsPredSlot("backup", 0, 0).getItem().isEmpty())
			sr.draw(g, "backup", "slotbg_bow", -1, -1);
	}

}
