package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.l2core.base.menu.base.LayoutRenderer;
import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.core.GolemScreenControl;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class DogGolemScreenControl extends GolemScreenControl<DogGolemEntity> {

	public DogGolemScreenControl(GolemMenuControl<DogGolemEntity> ctrl) {
		super(ctrl);
	}

	public void render(LayoutRenderer sr, GuiGraphicsExtractor g, float pTick) {
		sr.draw(g, "chest", "slot", -1, -1);
		sr.draw(g, "legs", "slot", -1, -1);
		if (menu.getAsPredSlot("chest", 0, 0).getItem().isEmpty())
			sr.draw(g, "chest", "altas_helmet", 0, 0);
		if (menu.getAsPredSlot("legs", 0, 0).getItem().isEmpty())
			sr.draw(g, "legs", "altas_chestplate", 0, 0);
	}

}
