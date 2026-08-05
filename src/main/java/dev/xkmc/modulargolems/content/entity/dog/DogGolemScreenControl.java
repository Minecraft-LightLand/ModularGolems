package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.core.GolemScreenControl;
import net.minecraft.client.gui.GuiGraphics;

public class DogGolemScreenControl extends GolemScreenControl<DogGolemEntity> {

	public DogGolemScreenControl(GolemMenuControl<DogGolemEntity> ctrl) {
		super(ctrl);
	}

	public void render(MenuLayoutConfig.ScreenRenderer sr, GuiGraphics g, float pTick) {
		sr.draw(g, "chest", "slot", -1, -1);
		sr.draw(g, "legs", "slot", -1, -1);
		if (menu.getAsPredSlot("chest", 0, 0).getItem().isEmpty())
			sr.draw(g, "chest", "altas_helmet", 0, 0);
		if (menu.getAsPredSlot("legs", 0, 0).getItem().isEmpty())
			sr.draw(g, "legs", "slotbg_dog_armor", -1, -1);
	}

}
