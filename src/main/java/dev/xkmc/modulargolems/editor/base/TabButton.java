package dev.xkmc.modulargolems.editor.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TabButton extends Button {

	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/tab_button.png");
	private static final int TEXTURE_WIDTH = 130;
	private static final int TEXTURE_HEIGHT = 24;
	private static final int TEXTURE_BORDER = 2;
	private static final int TEXTURE_BORDER_BOTTOM = 0;
	private static final int SELECTED_OFFSET = 3;

	private final boolean selected;

	public TabButton(int x, int y, int width, int height, Component message, boolean selected, OnPress onPress) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
		this.selected = selected;
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mx, int my, float partialTick) {
		g.blitNineSliced(TEXTURE_LOCATION, getX(), getY(), getWidth(), getHeight(), TEXTURE_BORDER, TEXTURE_BORDER, TEXTURE_BORDER, TEXTURE_BORDER_BOTTOM, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, getTextureY());
		var font = Minecraft.getInstance().font;
		int color = selected ? 0xFFFFFFFF : 0xFFA0A0A0;
		int top = getY() + (selected ? 0 : SELECTED_OFFSET);
		g.drawCenteredString(font, getMessage(), getX() + getWidth() / 2,
				top + (getY() + getHeight() - top - font.lineHeight) / 2, color);
		if (selected) {
			int w = Math.min(font.width(getMessage()), getWidth() - 4);
			int x = getX() + (getWidth() - w) / 2;
			int y = getY() + getHeight() - 2;
			g.fill(x, y, x + w, y + 1, color);
		}
	}

	private int getTextureY() {
		int i = 2;
		if (selected && isHoveredOrFocused()) {
			i = 1;
		} else if (selected) {
			i = 0;
		} else if (isHoveredOrFocused()) {
			i = 3;
		}
		return i * TEXTURE_HEIGHT;
	}

}
