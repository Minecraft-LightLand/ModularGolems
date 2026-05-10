package dev.xkmc.modulargolems.content.menu.table;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ItemListClientTooltip(List<ItemStack> inv) implements ClientTooltipComponent {

	public static final Identifier TEXTURE_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/bundle.png");

	public ItemListClientTooltip(ItemListTooltip comp) {
		this(comp.inv());
	}

	public int getHeight() {
		return (inv.size() + 8) / 9 * 18 + 2;
	}

	public int getWidth(Font font) {
		return Math.min(9, inv.size()) * 18;
	}

	public void renderImage(Font font, int mx, int my, GuiGraphics g) {
		int w = Math.min(9, inv.size());
		for (int i = 0; i < inv.size(); ++i) {
			this.renderSlot(font, mx + i % w * 18, my + i / w * 18, g, inv.get(i));
		}

	}

	private void renderSlot(Font font, int x, int y, GuiGraphics g, ItemStack stack) {
		this.blit(g, x, y);
		if (!stack.isEmpty()) {
			g.renderItem(stack, x + 1, y + 1, 0);
			g.renderItemDecorations(font, stack, x + 1, y + 1);
		}
	}

	private void blit(GuiGraphics g, int x, int y) {
		g.blit(TEXTURE_LOCATION, x, y, 0, 0.0F, 0.0F, 18, 18, 128, 128);
	}

}
