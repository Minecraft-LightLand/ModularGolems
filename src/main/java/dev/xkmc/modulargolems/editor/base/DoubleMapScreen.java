package dev.xkmc.modulargolems.editor.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DoubleMapScreen<T> extends Screen {

	public interface Handler<T> {

		Component label(T t);

		@Nullable
		ItemStack icon(T t);

		boolean percent(T t);

	}

	private final Map<T, Double> map;
	private final List<T> candidates;
	private final Handler<T> handler;
	private final Screen parent;
	private final EditorSession session;

	private EditorList list;
	private final List<T> order = new ArrayList<>();
	private Button addBtn;
	private Button editBtn;
	private Button removeBtn;

	public DoubleMapScreen(Component title, Map<T, Double> map, List<T> candidates,
						   Handler<T> handler, Screen parent, EditorSession session) {
		super(title);
		this.map = map;
		this.candidates = candidates;
		this.handler = handler;
		this.parent = parent;
		this.session = session;
	}

	@Override
	protected void init() {
		list = new EditorList(minecraft, width, height - 70, 30, height - 40);
		addRenderableWidget(list);
		List<Button> row = new ArrayList<>();
		addBtn = Button.builder(EditorText.ADD.get(), b -> addValue()).bounds(0, 0, 60, 20).build();
		row.add(addBtn);
		editBtn = Button.builder(EditorText.EDIT.get(), b -> editValue()).bounds(0, 0, 60, 20).build();
		row.add(editBtn);
		removeBtn = Button.builder(EditorText.REMOVE.get(), b -> removeValue()).bounds(0, 0, 60, 20).build();
		row.add(removeBtn);
		row.add(Button.builder(EditorText.BACK.get(), b -> Minecraft.getInstance().setScreen(parent)).bounds(0, 0, 60, 20).build());
		row.forEach(this::addRenderableWidget);
		EditorLayout.centerRow(row, width / 2, height - 30, 5);
		editBtn.active = false;
		removeBtn.active = false;
		list.setOnSelect(() -> {
			editBtn.active = selectedKey() != null;
			removeBtn.active = selectedKey() != null;
		});
		rebuild();
	}

	private void rebuild() {
		order.clear();
		List<EditorList.Entry> entries = new ArrayList<>();
		List<T> keys = new ArrayList<>(map.keySet());
		keys.sort((a, b) -> handler.label(a).getString().compareToIgnoreCase(handler.label(b).getString()));
		for (T k : keys) {
			order.add(k);
			entries.add(new EditorList.Entry(
					handler.label(k).copy().append(Component.literal("   " + display(k, map.get(k)))), handler.icon(k), null));
		}
		list.setData(entries);
		updateAddBtn();
	}

	private List<T> remaining() {
		List<T> remaining = new ArrayList<>();
		for (T t : candidates) {
			if (!map.containsKey(t)) {
				remaining.add(t);
			}
		}
		return remaining;
	}

	private void updateAddBtn() {
		addBtn.active = !remaining().isEmpty();
	}

	@Nullable
	private T selectedKey() {
		EditorList.Entry sel = list.getSelected();
		if (sel == null) return null;
		int i = list.children().indexOf(sel);
		if (i < 0 || i >= order.size()) return null;
		return order.get(i);
	}

	private void addValue() {
		List<T> remaining = remaining();
		if (remaining.isEmpty()) {
			EditorToast.show(EditorText.ADD.get(), EditorText.NO_FILE.get());
			return;
		}
		Minecraft.getInstance().setScreen(new PickListScreen<>(EditorText.PICK_TARGET.get(), remaining,
				new AddValueHandler<>(this, handler), this));
	}

	private void promptValue(T key) {
		double cur = map.getOrDefault(key, 0.0);
		Minecraft.getInstance().setScreen(new PromptScreen(EditorText.VALUE.get(), handler.label(key), format(cur), s -> {
			try {
				Double.parseDouble(s.trim());
				return null;
			} catch (NumberFormatException e) {
				return EditorText.INVALID_NUMBER.get(s);
			}
		}, s -> {
			map.put(key, Double.parseDouble(s.trim()));
			session.dirty = true;
			Minecraft.getInstance().setScreen(DoubleMapScreen.this);
		}, this));
	}

	private void editValue() {
		T key = selectedKey();
		if (key == null) return;
		promptValue(key);
	}

	private void removeValue() {
		T key = selectedKey();
		if (key == null) return;
		map.remove(key);
		session.dirty = true;
		rebuild();
	}

	public static String format(double v) {
		String s = String.format(Locale.ROOT, "%.6f", v);
		while (s.endsWith("0")) {
			s = s.substring(0, s.length() - 1);
		}
		if (s.endsWith(".")) {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}

	private String display(T key, double v) {
		return handler.percent(key) ? format(v * 100) + "%" : format(v);
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, this.title, width / 2, 10, 0xFFFFFF);
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(parent);
	}

	private record AddValueHandler<T>(DoubleMapScreen<T> screen, Handler<T> handler) implements PickListScreen.Handler<T> {

		@Override
		public Component label(T t) {
			return handler.label(t);
		}

		@Override
		@Nullable
		public ItemStack icon(T t) {
			return handler.icon(t);
		}

		@Override
		public void onSelect(T t) {
			screen.promptValue(t);
		}

	}

}
