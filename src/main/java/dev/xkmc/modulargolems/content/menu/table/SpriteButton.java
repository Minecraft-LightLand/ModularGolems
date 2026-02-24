package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;

public class SpriteButton extends Button {

	private final MenuLayoutConfig config;
	private final String normal, down, inactive;

	private boolean pressed = false;

	protected SpriteButton(Builder builder, MenuLayoutConfig config, String normal, String down, String inactive) {
		super(builder);
		this.config = config;
		this.normal = normal;
		this.down = down;
		this.inactive = inactive;
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
		var side = config.getSide(isActive() ? pressed ?
				isHoveredOrFocused() ? down + "_hover" : down :
				isHoveredOrFocused() ? normal + "_hover" : normal :
				inactive);
		this.renderTexture(g, config.getTexture(), this.getX(), this.getY(),
				side.x, side.y, 0, this.width, this.height, 256, 256);
	}

}
