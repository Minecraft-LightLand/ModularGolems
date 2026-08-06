package dev.xkmc.modulargolems.editor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class EditorList extends ObjectSelectionList<EditorList.Entry> {

	public EditorList(Minecraft mc, int width, int height, int y0, int y1) {
		super(mc, width, height, y0, y1, 20);
	}

	@Override
	public int getRowWidth() {
		return Math.min(width - 24, 300);
	}

	@Override
	public int getScrollbarPosition() {
		return x1 - 6;
	}

	public void setData(java.util.List<Entry> entries) {
		clearEntries();
		entries.forEach(this::addEntry);
	}

	public static class Entry extends ObjectSelectionList.Entry<Entry> {

		private final Component text;
		@Nullable
		private final ItemStack icon;
		@Nullable
		private final Runnable onClick;
		@Nullable
		private final Object data;
		private final boolean header;

		public Entry(Component text, @Nullable ItemStack icon, @Nullable Runnable onClick) {
			this(text, icon, onClick, false, null);
		}

		public Entry(Component text, @Nullable ItemStack icon, @Nullable Runnable onClick, @Nullable Object data) {
			this(text, icon, onClick, false, data);
		}

		public Entry(Component text, boolean header) {
			this(text, null, null, header, null);
		}

		private Entry(Component text, @Nullable ItemStack icon, @Nullable Runnable onClick, boolean header, @Nullable Object data) {
			this.text = text;
			this.icon = icon;
			this.onClick = onClick;
			this.header = header;
			this.data = data;
		}

		@Nullable
		public Object getData() {
			return data;
		}

		@Override
		public Component getNarration() {
			return text;
		}

		@Override
		public void render(GuiGraphics g, int index, int top, int left, int rowWidth, int itemHeight, int mx, int my, boolean hovered, float partialTick) {
			if (header) {
				g.fill(left, top - 2, left + rowWidth, top + itemHeight + 2, 0x20AAAAAA);
				g.drawString(Minecraft.getInstance().font, text, left + 2, top + 5, 0xAAAAAA);
				return;
			}
			if (hovered) {
				g.fill(left, top - 2, left + rowWidth, top + itemHeight + 2, 0x20FFFFFF);
			}
			int x = left + 2;
			if (icon != null) {
				g.renderItem(icon, left + 2, top + 1);
				x = left + 22;
			}
			g.drawString(Minecraft.getInstance().font, text, x, top + 5, 0xFFFFFF);
		}

		public void activate() {
			if (onClick != null) {
				onClick.run();
			}
		}

		@Override
		public boolean mouseClicked(double mx, double my, int button) {
			if (button == 0 && !header) {
				this.list.setSelected(this);
				activate();
				return true;
			}
			return super.mouseClicked(mx, my, button);
		}

	}

}
