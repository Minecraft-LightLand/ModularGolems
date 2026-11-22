package dev.xkmc.modulargolems.content.menu.tabs;

import dev.xkmc.l2tabs.tabs.core.TabType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/**
 * Legacy tab geometry used by the golem-type strip on the assemble screen. Rendering and placement
 * now delegate to l2tabs' split sprites ({@link TabType}) so the mod ships no whole tabs.png.
 */
public enum GolemTabType {
	ABOVE(TabType.ABOVE),
	BELOW(TabType.BELOW),
	LEFT(TabType.LEFT),
	RIGHT(TabType.RIGHT);

	public final int max = 7;
	public final TabType type;
	public final int width;
	public final int height;

	GolemTabType(TabType type) {
		this.type = type;
		this.width = type.width;
		this.height = type.height;
	}

	public void draw(GuiGraphics g, int x, int y, boolean selected, int index) {
		int ind = index == 0 ? 0 : index == max - 1 ? 2 : 1;
		type.draw(type.sprite, g, x, y, selected, ind);
	}

	public void drawIcon(GuiGraphics g, int x, int y, int index, ItemStack stack) {
		type.drawIcon(g, x, y, stack);
	}

	public int getX(int w, int h, int index, int split, int count) {
		if (this == ABOVE || this == BELOW) {
			int perPage = Math.min(count, max);
			int space = split < 0 ? 0 : w - (width + 1) * max + 1;
			return (w - ((width + 1) * perPage - 1)) / 2 + (width + 1) * index + (index >= split ? space : 0);
		}
		return type.getX(w, h, index, split);
	}

	public int getY(int w, int h, int index, int split) {
		if (this == LEFT || this == RIGHT) {
			int space = split < 0 ? 0 : h - (height + 1) * max + 1;
			return (height + 1) * index + (index >= split ? space : 0);
		}
		return type.getY(w, h, index, split);
	}

}
