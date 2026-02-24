package dev.xkmc.modulargolems.content.menu.tabs;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum GolemTabType {
	ABOVE(0, 120, 24, 28, 7),
	BELOW(72, 120, 24, 28, 7),
	LEFT(0, 176, 28, 24, 5),
	RIGHT(84, 176, 28, 24, 5);

	private final static ResourceLocation TEXTURE = new ResourceLocation(ModularGolems.MODID, "textures/gui/tabs.png");

	public final int max;
	private final int textureX;
	private final int textureY;
	final int width;
	final int height;

	GolemTabType(int tx, int ty, int w, int h, int max) {
		this.textureX = tx;
		this.textureY = ty;
		this.width = w;
		this.height = h;
		this.max = max;
	}

	public void draw(GuiGraphics g, int x, int y, boolean selected, int index) {
		index = index % max;
		int tx = this.textureX;
		if (index > 0) {
			tx += this.width;
		}

		if (index == max - 1) {
			tx += this.width;
		}

		int ty = selected ? this.textureY + this.height : this.textureY;
		g.blit(TEXTURE, x, y, tx, ty, this.width, this.height);
	}

	public void drawIcon(GuiGraphics g, int x, int y, int index, ItemStack stack) {
		int i = x;
		int j = y;
		switch (this) {
			case ABOVE -> {
				i += 4;
				j += 6;
			}
			case BELOW -> {
				i += 6;
				j += 6;
			}
			case LEFT -> {
				i += 10;
				j += 5;
			}
			case RIGHT -> {
				i += 6;
				j += 4;
			}
		}
		g.renderFakeItem(stack, i, j);
		g.renderItemDecorations(Minecraft.getInstance().font, stack, i, j);
	}

	public int getX(int w, int h, int index, int split) {
		int space = w - (width + 1) * max + 1;
		return switch (this) {
			case ABOVE, BELOW -> (width + 1) * index + (index >= split ? space : 0);
			case LEFT -> -width + 4;
			case RIGHT -> w - 4;
		};
	}

	public int getY(int w, int h, int index, int split) {
		int space = h - (height + 1) * max + 1;
		return switch (this) {
			case ABOVE -> -height + 4;
			case BELOW -> h - 4;
			case LEFT, RIGHT -> (height + 1) * index + (index >= split ? space : 0);
		};
	}

}
