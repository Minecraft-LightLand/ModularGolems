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

	protected boolean getBan() {
		return !isActive();
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
		var side = config.getSide(getBan() ?
				isHovered() ? inactive + "_hover" : inactive :
				pressed ? (isHovered() ? down + "_hover" : down) :
				(isHovered() ? normal + "_hover" : normal));
		this.renderTexture(g, config.getTexture(), this.getX(), this.getY(),
				side.x, side.y, 0, this.width, this.height, 256, 256);
	}

}
