package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;

public class SpriteButton extends Button {

	private final String id;

	private boolean pressed = false;

	protected SpriteButton(Builder builder, String id) {
		super(builder);
		this.id = id;
	}

	@Override
	public void onPress() {
		pressed = true;
	}

	@Override
	public void onRelease(double mx, double my) {
		if (!pressed) return;
		pressed = false;
		if (clicked(mx, my)) {
			super.onPress();
		}
	}

	public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
		if (pressed & !clicked(mx, my)) pressed = false;
		String tex = id;
		if (isActive()) {
			if (pressed) tex += "_down";
			else tex += "_on";
			if (isHoveredOrFocused())
				tex += "_hover";
		} else tex += "_ban";
		g.blitSprite(ModularGolems.loc(tex), getX(), getY(), width, height);
	}

}
