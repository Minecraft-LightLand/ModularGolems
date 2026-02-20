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

	public static final int MAX_TABS = 8;
	private final int textureX;
	private final int textureY;
	private final int width;
	private final int height;

	GolemTabType(int tx, int ty, int w, int h, int max) {
		this.textureX = tx;
		this.textureY = ty;
		this.width = w;
		this.height = h;
	}

	public void draw(GuiGraphics g, int x, int y, boolean selected, int index) {
		index = index % MAX_TABS;
		int tx = this.textureX;
		if (index > 0) {
			tx += this.width;
		}

		if (index == MAX_TABS - 1) {
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

	public int getX(int w, int h, int pIndex) {
		return switch (this) {
			case ABOVE, BELOW -> (this.width + 1) * pIndex;
			case LEFT -> -this.width + 4;
			case RIGHT -> w - 4;
		};
	}

	public int getY(int w, int h, int pIndex) {
		return switch (this) {
			case ABOVE -> -this.height + 4;
			case BELOW -> h - 4;
			case LEFT, RIGHT -> (this.height + 1) * pIndex;
		};
	}

	public boolean isMouseOver(int w, int h, int left, int top, int index, double mx, double my) {
		int i = left + this.getX(w, h, index);
		int j = top + this.getY(w, h, index);
		return mx > (double) i && mx < (double) (i + this.width) && my > (double) j && my < (double) (j + this.height);
	}
}
