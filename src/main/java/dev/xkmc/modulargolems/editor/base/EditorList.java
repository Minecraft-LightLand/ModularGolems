package dev.xkmc.modulargolems.editor.base;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class EditorList extends ObjectSelectionList<EditorList.Entry> {

	@Nullable
	private Runnable onSelect;
	@Nullable
	private Runnable onDoubleClick;
	private long lastClick = -1;

	@Nullable
	private Component hoveredTooltip;
	private int hoveredX, hoveredY;

	public EditorList(Minecraft mc, int width, int height, int y0, int y1) {
		super(mc, width, height, y0, y1, 20);
	}

	public void setOnSelect(Runnable onSelect) {
		this.onSelect = onSelect;
	}

	public void setOnDoubleClick(Runnable onDoubleClick) {
		this.onDoubleClick = onDoubleClick;
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float partialTick) {
		hoveredTooltip = null;
		super.render(g, mx, my, partialTick);
		hoveredX = mx;
		hoveredY = my;
	}

	/**
	 * Renders the tooltip of the hovered row, if any. Call after the list is drawn.
	 */
	public void renderRowTooltip(GuiGraphics g) {
		if (hoveredTooltip != null) {
			g.renderComponentTooltip(Minecraft.getInstance().font, List.of(hoveredTooltip), hoveredX, hoveredY);
		}
	}

	@Override
	public void setSelected(@Nullable Entry entry) {
		super.setSelected(entry);
		if (onSelect != null) onSelect.run();
	}

	@Override
	public int getRowWidth() {
		return Math.min(width - 24, 300);
	}

	@Override
	public int getScrollbarPosition() {
		return x1 - 6;
	}

	public void setData(List<Entry> entries) {
		clearEntries();
		entries.forEach(this::addEntry);
		setSelected(null);
		if (onSelect != null) onSelect.run();
		setScrollAmount(getScrollAmount());
	}

	public static class Entry extends ObjectSelectionList.Entry<Entry> {

		private final Component text;
		@Nullable
		private final ItemStack icon;
		@Nullable
		private final Supplier<ItemStack> iconSupplier;
		@Nullable
		private final Runnable onClick;
		@Nullable
		private final Object data;
		private final boolean header;
		private final boolean grey;
		private final boolean collapsed;
		@Nullable
		private final Component tooltip;

		public Entry(Component text, @Nullable ItemStack icon, @Nullable Runnable onClick) {
			this(text, icon, null, onClick, false, false, null, false, null);
		}

		public Entry(Component text, @Nullable ItemStack icon, @Nullable Runnable onClick, @Nullable Object data) {
			this(text, icon, null, onClick, false, false, data, false, null);
		}

		public Entry(Component text, @Nullable ItemStack icon, @Nullable Runnable onClick, @Nullable Object data, boolean grey) {
			this(text, icon, null, onClick, false, false, data, grey, null);
		}

		public Entry(Component text, @Nullable ItemStack icon, @Nullable Runnable onClick, boolean grey) {
			this(text, icon, null, onClick, false, false, null, grey, null);
		}

		public Entry(Component text, @Nullable ItemStack icon, @Nullable Runnable onClick, boolean grey,
		             @Nullable Component tooltip) {
			this(text, icon, null, onClick, false, false, null, grey, tooltip);
		}

		public Entry(Component text, @Nullable ItemStack icon, @Nullable Runnable onClick, @Nullable Object data,
		             boolean grey, @Nullable Component tooltip) {
			this(text, icon, null, onClick, false, false, data, grey, tooltip);
		}

		public static Entry rotating(Component text, @Nullable Supplier<ItemStack> iconSupplier,
		                             @Nullable Runnable onClick) {
			return new Entry(text, null, iconSupplier, onClick, false, false, null, false, null);
		}

		public Entry(Component text, boolean header) {
			this(text, null, null, null, header, false, null, false, null);
		}

		public Entry(Component text, boolean header, @Nullable Runnable onClick) {
			this(text, null, null, onClick, header, false, null, false, null);
		}

		public Entry(Component text, boolean header, @Nullable Runnable onClick, boolean collapsed) {
			this(text, null, null, onClick, header, collapsed, null, false, null);
		}

		private Entry(Component text, @Nullable ItemStack icon, @Nullable Supplier<ItemStack> iconSupplier,
		              @Nullable Runnable onClick, boolean header, boolean collapsed, @Nullable Object data, boolean grey,
		              @Nullable Component tooltip) {
			this.text = text;
			this.icon = icon;
			this.iconSupplier = iconSupplier;
			this.onClick = onClick;
			this.header = header;
			this.collapsed = collapsed;
			this.data = data;
			this.grey = grey;
			this.tooltip = tooltip;
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
				Component label = text.copy().withStyle(ChatFormatting.WHITE)
						.append(Component.literal(collapsed ? " [+]" : " [-]").withStyle(ChatFormatting.GRAY));
				g.drawString(Minecraft.getInstance().font, label, left + 2, top + 5, 0xAAAAAA);
				return;
			}
			if (hovered) {
				g.fill(left, top - 2, left + rowWidth, top + itemHeight + 2, 0x20FFFFFF);
			}
			int x = left + 2;
			ItemStack useIcon = iconSupplier != null ? iconSupplier.get() : icon;
			if (useIcon != null && !useIcon.isEmpty()) {
				g.renderItem(useIcon, left + 2, top + 1);
				x = left + 22;
			}
			g.drawString(Minecraft.getInstance().font, text, x, top + 5, grey ? 0xAAAAAA : 0xFFFFFF);
			if (hovered && tooltip != null) {
				((EditorList) this.list).hoveredTooltip = tooltip;
			}
		}

		public void activate() {
			if (onClick != null) {
				onClick.run();
			}
		}

		@Override
		public boolean mouseClicked(double mx, double my, int button) {
			if (button == 0) {
				if (header) {
					if (onClick != null) {
						onClick.run();
						return true;
					}
				} else {
					this.list.setSelected(this);
					long now = Util.getMillis();
					boolean dbl = now - ((EditorList) this.list).lastClick < 250;
					((EditorList) this.list).lastClick = now;
					activate();
					if (dbl) {
						Runnable dblClick = ((EditorList) this.list).onDoubleClick;
						if (dblClick != null) dblClick.run();
					}
					return true;
				}
			}
			return super.mouseClicked(mx, my, button);
		}

	}

}
