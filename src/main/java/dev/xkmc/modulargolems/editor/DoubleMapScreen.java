package dev.xkmc.modulargolems.editor;

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
import java.util.function.Function;

public class DoubleMapScreen<T> extends Screen {

	private final Map<T, Double> map;
	private final List<T> candidates;
	private final Function<T, Component> label;
	private final Function<T, ItemStack> icon;
	private final net.minecraft.client.gui.screens.Screen parent;

	private EditorList list;
	private final List<T> order = new ArrayList<>();

	public DoubleMapScreen(Component title, Map<T, Double> map, List<T> candidates,
						   Function<T, Component> label, Function<T, ItemStack> icon,
						   net.minecraft.client.gui.screens.Screen parent) {
		super(title);
		this.map = map;
		this.candidates = candidates;
		this.label = label;
		this.icon = icon;
		this.parent = parent;
	}

	@Override
	protected void init() {
		list = new EditorList(minecraft, width, height - 70, 30, height - 40);
		addRenderableWidget(list);
		int c = width / 2;
		addRenderableWidget(Button.builder(EditorLang.ADD.get(), b -> addValue())
				.bounds(c - 130, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.EDIT.get(), b -> editValue())
				.bounds(c - 65, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.REMOVE.get(), b -> removeValue())
				.bounds(c, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.BACK.get(), b -> Minecraft.getInstance().setScreen(parent))
				.bounds(c + 65, height - 30, 60, 20).build());
		rebuild();
	}

	private void rebuild() {
		order.clear();
		List<EditorList.Entry> entries = new ArrayList<>();
		List<T> keys = new ArrayList<>(map.keySet());
		keys.sort((a, b) -> label.apply(a).getString().compareToIgnoreCase(label.apply(b).getString()));
		for (T k : keys) {
			order.add(k);
			entries.add(new EditorList.Entry(
					label.apply(k).copy().append(Component.literal("   " + format(map.get(k)))), icon.apply(k), null));
		}
		list.setData(entries);
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
		List<T> remaining = new ArrayList<>();
		for (T t : candidates) {
			if (!map.containsKey(t)) {
				remaining.add(t);
			}
		}
		if (remaining.isEmpty()) {
			EditorToast.show(EditorLang.ADD.get(), EditorLang.NO_FILE.get());
			return;
		}
		Minecraft.getInstance().setScreen(new PickListScreen<>(EditorLang.PICK_TARGET.get(), remaining, label, icon, t -> {
			promptValue(t);
		}));
	}

	private void promptValue(T key) {
		double cur = map.getOrDefault(key, 0.0);
		Minecraft.getInstance().setScreen(new PromptScreen(EditorLang.VALUE.get(), label.apply(key), format(cur), s -> {
			try {
				Double.parseDouble(s.trim());
				return null;
			} catch (NumberFormatException e) {
				return EditorLang.INVALID_NUMBER.get(s);
			}
		}, s -> {
			map.put(key, Double.parseDouble(s.trim()));
			Minecraft.getInstance().setScreen(DoubleMapScreen.this);
		}));
	}

	private void editValue() {
		T key = selectedKey();
		if (key == null) {
			EditorToast.show(EditorLang.EDIT.get(), EditorLang.NO_FILE.get());
			return;
		}
		promptValue(key);
	}

	private void removeValue() {
		T key = selectedKey();
		if (key == null) {
			EditorToast.show(EditorLang.REMOVE.get(), EditorLang.NO_FILE.get());
			return;
		}
		map.remove(key);
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

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, this.title, width / 2, 10, 0xFFFFFF);
	}

}
