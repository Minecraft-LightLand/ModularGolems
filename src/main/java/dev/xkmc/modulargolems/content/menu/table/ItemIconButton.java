package dev.xkmc.modulargolems.content.menu.table;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ItemIconButton extends Button {

	private final BooleanSupplier selected;
	private final Supplier<ItemStack> icon;

	public ItemIconButton(int x, int y, int w, int h, Supplier<ItemStack> icon, BooleanSupplier selected, OnPress onPress) {
		super(Button.builder(Component.empty(), onPress).pos(x, y).size(w, h));
		this.icon = icon;
		this.selected = selected;
	}

	@Override
	public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
		int x = getX();
		int y = getY();
		if (selected.getAsBoolean()) {
			g.fill(x, y, x + width, y + height, 0xFF5738A0);
		}
		g.renderItem(icon.get(), x - 1, y - 1);
		if (isHoveredOrFocused()) {
			g.fill(x, y, x + width, y + height, 0x80FFFFFF);
		}
	}

}