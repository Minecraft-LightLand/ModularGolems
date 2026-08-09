package dev.xkmc.modulargolems.editor.base;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class FormScreen<T> extends EditorScreen {

	public record FormSpec<T>(List<FormField> fields, Function<List<String>, T> build) {

	}

	public record FormField(Component label, String initial, @Nullable Function<String, Component> validate,
	                        boolean bool, @Nullable List<Component> tooltip) {

		public static FormField text(Component label, String initial, @Nullable Function<String, Component> validate) {
			return new FormField(label, initial, validate, false, null);
		}

		public static FormField text(Component label, String initial, @Nullable Function<String, Component> validate,
		                             Component... tooltip) {
			return new FormField(label, initial, validate, false, tooltip.length == 0 ? null : List.of(tooltip));
		}

		public static FormField bool(Component label, boolean initial) {
			return new FormField(label, "" + initial, null, true, null);
		}

		public static FormField bool(Component label, boolean initial, Component... tooltip) {
			return new FormField(label, "" + initial, null, true, tooltip.length == 0 ? null : List.of(tooltip));
		}

	}

	private static final int ROW_H = 26;
	private static final int BOX_W = 120;
	private static final int CONTENT_TOP = 24;

	private final FormSpec<T> spec;
	private final Consumer<T> onDone;
	private final Screen parent;
	private final boolean saveOnClose;

	private final boolean[] boolValues;
	private final List<EditBox> boxes = new ArrayList<>();
	private final List<Button> boolBtns = new ArrayList<>();
	private final List<Integer> boxToField = new ArrayList<>();
	private final List<Integer> boolToField = new ArrayList<>();
	@Nullable
	private Component error;
	@Nullable
	private FormList list;

	public FormScreen(Component title, FormSpec<T> spec, Consumer<T> onDone, Screen parent) {
		this(title, spec, onDone, parent, false);
	}

	public FormScreen(Component title, FormSpec<T> spec, Consumer<T> onDone, Screen parent, boolean saveOnClose) {
		super(title);
		this.spec = spec;
		this.onDone = onDone;
		this.parent = parent;
		this.saveOnClose = saveOnClose;
		this.boolValues = new boolean[spec.fields().stream().mapToInt(e -> e.bool() ? 1 : 0).sum()];
	}

	@Override
	protected void init() {
		int bi = 0;
		for (int i = 0; i < spec.fields().size(); i++) {
			FormField field = spec.fields().get(i);
			if (field.bool()) {
				int idx = bi++;
				boolToField.add(i);
				boolValues[idx] = field.initial().equals("true");
				boolBtns.add(Button.builder(boolLabel(idx), b -> {
				}).bounds(0, 0, BOX_W, 20).build());
			} else {
				EditBox box = new EditBox(this.font, 0, 0, BOX_W, 20, field.label());
				box.setMaxLength(64);
				box.setValue(field.initial());
				box.setResponder(s -> error = null);
				boxes.add(box);
				boxToField.add(i);
			}
		}
		list = new FormList(minecraft, width, buttonY() - 10 - CONTENT_TOP, CONTENT_TOP, buttonY() - 10, ROW_H);
		list.setRenderSelection(false);
		for (int i = 0; i < spec.fields().size(); i++) {
			list.addRow(new FormEntry(i));
		}
		addRenderableWidget(list);
		addRenderableWidget(Button.builder(EditorText.CANCEL.get(), b -> Minecraft.getInstance().setScreen(parent))
				.bounds(width / 2 - 110, buttonY(), 100, 20).build());
		addRenderableWidget(Button.builder(EditorText.CONFIRM.get(), b -> submit())
				.bounds(width / 2 + 10, buttonY(), 100, 20).build());
		if (!boxes.isEmpty()) boxes.get(0).setFocused(true);
	}

	private int labelX() {
		return width / 2 - 160;
	}

	private int boxX() {
		return width / 2 + 40;
	}

	/**
	 * Max width of a field label before it would overlap the edit box. Translated labels can be
	 * longer than the English option names.
	 */
	private int maxLabelWidth() {
		return boxX() - labelX() - 6;
	}

	/**
	 * The label to draw: truncated with an ellipsis when it does not fit next to the edit box.
	 */
	private Component fitLabel(FormField field) {
		Component label = field.label();
		if (font.width(label) <= maxLabelWidth()) return label;
		String cut = font.plainSubstrByWidth(label.getString(), Math.max(0, maxLabelWidth() - 3));
		return Component.literal(cut.isEmpty() ? "..." : cut + "...").withStyle(label.getStyle());
	}

	private int buttonY() {
		return height - 30;
	}

	private Component boolLabel(int idx) {
		boolean v = boolValues[idx];
		return Component.literal(Boolean.toString(v)).withStyle(v ? ChatFormatting.GREEN : ChatFormatting.RED);
	}

	private void toggleBool(int idx) {
		boolValues[idx] = !boolValues[idx];
		boolBtns.get(idx).setMessage(boolLabel(idx));
		error = null;
	}

	private void submit() {
		List<String> values = new ArrayList<>();
		int bi = 0;
		for (int i = 0; i < spec.fields().size(); i++) {
			FormField field = spec.fields().get(i);
			if (field.bool()) {
				values.add("" + boolValues[bi++]);
			} else {
				String val = boxes.get(boxToField.indexOf(i)).getValue();
				if (field.validate() != null) {
					Component err = field.validate().apply(val);
					if (err != null) {
						error = err;
						return;
					}
				}
				values.add(val);
			}
		}
		onDone.accept(spec.build().apply(values));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 257 || keyCode == 335) {
			submit();
			return true;
		}
		for (EditBox box : boxes) {
			if (box.isFocused() && box.keyPressed(keyCode, scanCode, modifiers)) return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		for (EditBox box : boxes) {
			if (box.isFocused() && box.charTyped(codePoint, modifiers)) return true;
		}
		return super.charTyped(codePoint, modifiers);
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, this.title, width / 2, 8, 0xFFFFFF);
		if (list != null) {
			List<Component> tip = hoveredTip(mx, my);
			if (tip != null && !tip.isEmpty()) {
				g.renderComponentTooltip(font, tip, mx, my);
			}
		}
		if (error != null) {
			g.drawCenteredString(font, error, width / 2, buttonY() - 12, 0xFF5555);
		}
	}

	/**
	 * Tooltip of the field row under the mouse, if any. Only the label text (not the editbox/bool
	 * button) triggers the tooltip.
	 */
	@Nullable
	private List<Component> hoveredTip(int mx, int my) {
		if (list == null) return null;
		FormEntry hovered = list.hoveredEntry();
		if (hovered == null) return null;
		int idx = list.children().indexOf(hovered);
		if (idx < 0) return null;
		FormField field = spec.fields().get(idx);
		if (field.tooltip() == null) return null;
		int top = list.rowTop(idx);
		Component label = fitLabel(field);
		int right = labelX() + font.width(label);
		if (my >= top && my < top + ROW_H && mx >= labelX() && mx <= right) {
			return field.tooltip();
		}
		return null;
	}

	/**
	 * Whether any field value differs from its initial value.
	 */
	private boolean changed() {
		int bi = 0;
		for (int i = 0; i < spec.fields().size(); i++) {
			FormField field = spec.fields().get(i);
			if (field.bool()) {
				if (boolValues[bi++] != Boolean.parseBoolean(field.initial())) return true;
			} else if (!boxes.get(boxToField.indexOf(i)).getValue().equals(field.initial())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onClose() {
		if (saveOnClose && changed()) {
			submit();
		} else {
			Minecraft.getInstance().setScreen(parent);
		}
	}

	/**
	 * List panel that renders the form rows. Each row draws its label and value widget inside the
	 * list viewport, so both are clipped to the content band when scrolled.
	 */
	private class FormList extends ObjectSelectionList<FormEntry> {

		FormList(Minecraft mc, int width, int height, int y0, int y1, int itemHeight) {
			super(mc, width, height, y0, y1, itemHeight);
		}

		@Override
		public int getRowWidth() {
			return width;
		}

		@Override
		protected int getScrollbarPosition() {
			return width - 6;
		}

		void addRow(FormEntry entry) {
			addEntry(entry);
		}

		int rowTop(int index) {
			return getRowTop(index);
		}

		@Nullable
		FormEntry hoveredEntry() {
			return getHovered();
		}

	}

	private class FormEntry extends ObjectSelectionList.Entry<FormEntry> {

		private final int field;

		FormEntry(int field) {
			this.field = field;
		}

		private int index() {
			return list.children().indexOf(this);
		}

		@Override
		public void render(GuiGraphics g, int index, int top, int left, int rowWidth, int itemHeight,
		                   int mx, int my, boolean hovered, float partialTick) {
			if (hovered) {
				g.fill(left, top - 2, left + rowWidth, top + itemHeight + 2, 0x20FFFFFF);
			}
			FormField f = spec.fields().get(field);
			g.drawString(font, fitLabel(f), labelX(), top + 5, 0xAAAAAA);
			int by = top + 3;
			if (f.bool()) {
				Button btn = boolBtns.get(boolToField.indexOf(field));
				btn.setX(boxX());
				btn.setY(by);
				btn.render(g, mx, my, partialTick);
			} else {
				EditBox box = boxes.get(boxToField.indexOf(field));
				box.setX(boxX());
				box.setY(by);
				box.render(g, mx, my, partialTick);
			}
		}

		@Override
		public boolean mouseClicked(double mx, double my, int button) {
			if (button != 0) return false;
			FormField f = spec.fields().get(field);
			int top = FormScreen.this.list.rowTop(index());
			boolean inBox = mx >= boxX() && mx <= boxX() + BOX_W && my >= top && my < top + ROW_H;
			for (EditBox box : boxes) box.setFocused(false);
			if (f.bool()) {
				if (inBox) toggleBool(boolToField.indexOf(field));
			} else if (inBox) {
				boxes.get(boxToField.indexOf(field)).setFocused(true);
			}
			return true;
		}

		@Override
		public Component getNarration() {
			return spec.fields().get(field).label();
		}

	}

}
