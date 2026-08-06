package dev.xkmc.modulargolems.editor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class LinkButton extends Button {

	public LinkButton(int x, int y, int width, int height, Component message, OnPress onPress) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mx, int my, float partialTick) {
		super.renderWidget(g, mx, my, partialTick);
		if (isHoveredOrFocused() && active) {
			int w = Minecraft.getInstance().font.width(getMessage());
			int left = getX() + (getWidth() - w) / 2;
			g.fill(left, getY() + getHeight() - 2, left + w, getY() + getHeight() - 1, getFGColor());
		}
	}

}
