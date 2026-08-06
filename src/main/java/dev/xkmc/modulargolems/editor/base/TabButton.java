package dev.xkmc.modulargolems.editor.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class TabButton extends Button {

	private final boolean active;

	public TabButton(int x, int y, int width, int height, Component message, boolean active, OnPress onPress) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
		this.active = active;
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mx, int my, float partialTick) {
		int bg = active ? 0xFF9A9A9A : (isHoveredOrFocused() ? 0xFF6A6A6A : 0xFF4A4A4A);
		g.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), bg);
		g.fill(getX(), getY(), getX() + getWidth(), getY() + 1, 0xFF202020);
		g.fill(getX(), getY(), getX() + 1, getY() + getHeight(), 0xFF202020);
		g.fill(getX() + getWidth() - 1, getY(), getX() + getWidth(), getY() + getHeight(), 0xFF202020);
		g.fill(getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight(),
				active ? bg : 0xFF202020);
		int fg = active ? 0xFF000000 : (isHoveredOrFocused() ? 0xFFFFFFFF : 0xFFAAAAAA);
		g.drawCenteredString(Minecraft.getInstance().font, getMessage(),
				getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, fg);
	}

}
