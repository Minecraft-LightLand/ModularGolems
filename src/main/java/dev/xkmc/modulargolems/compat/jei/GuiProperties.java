package dev.xkmc.modulargolems.compat.jei;

import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.common.platform.IPlatformScreenHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import org.jetbrains.annotations.Nullable;

public record GuiProperties(Class<? extends Screen> screenClass, int guiLeft, int guiTop, int guiXSize, int guiYSize,
							int screenWidth, int screenHeight) implements IGuiProperties {
	@Nullable
	public static GuiProperties create(AbstractContainerScreen<?> containerScreen) {
		if (containerScreen.width <= 0 || containerScreen.height <= 0) {
			return null;
		}
		IPlatformScreenHelper screenHelper = Services.PLATFORM.getScreenHelper();
		int x = screenHelper.getGuiLeft(containerScreen);
		int y = screenHelper.getGuiTop(containerScreen);
		int width = screenHelper.getXSize(containerScreen);
		int height = screenHelper.getYSize(containerScreen);
		if (containerScreen instanceof RecipeUpdateListener r) {
			ImmutableRect2i bookArea = screenHelper.getBookArea(r);
			if (!bookArea.isEmpty()) {
				width += (x - bookArea.getX());
				x = bookArea.getX();
			}
		}

		if (x < 0) {
			width -= x;
			x = 0;
		}
		if (y < 0) {
			height -= y;
			y = 0;
		}
		if (width <= 0 || height <= 0) {
			return null;
		}
		return new GuiProperties(
				containerScreen.getClass(),
				x,
				y,
				width,
				height,
				containerScreen.width,
				containerScreen.height
		);
	}

}
