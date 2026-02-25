package dev.xkmc.modulargolems.content.menu.tabs;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public record DelegateTabScreen(AbstractContainerScreen<?> screen) implements ITabScreen {

	@Override
	public int getGuiLeft() {
		return screen.getGuiLeft();
	}

	@Override
	public int getGuiTop() {
		return screen.getGuiTop();
	}

	@Override
	public int screenWidth() {
		return screen.width;
	}

	@Override
	public int screenHeight() {
		return screen.height;
	}

	@Override
	public int getXSize() {
		return screen.getXSize();
	}

	@Override
	public int getYSize() {
		return screen.getYSize();
	}

	@Override
	public Screen asScreen() {
		return screen;
	}

}
