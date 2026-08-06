package dev.xkmc.modulargolems.editor;

import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ModifierMapScreen extends Screen {

	private final Map<GolemModifier, Integer> map;
	private final List<GolemModifier> candidates;
	private final Screen parent;
	private final EditorSession session;

	private EditorList list;
	private final List<GolemModifier> order = new ArrayList<>();

	public ModifierMapScreen(Component title, Map<GolemModifier, Integer> map, List<GolemModifier> candidates,
							 Screen parent, EditorSession session) {
		super(title);
		this.map = map;
		this.candidates = candidates;
		this.parent = parent;
		this.session = session;
	}

	@Override
	protected void init() {
		list = new EditorList(minecraft, width, height - 70, 30, height - 40);
		addRenderableWidget(list);
		int c = width / 2;
		addRenderableWidget(Button.builder(EditorLang.ADD.get(), b -> addModifier())
				.bounds(c - 130, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.EDIT.get(), b -> editModifier())
				.bounds(c - 65, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.REMOVE.get(), b -> removeModifier())
				.bounds(c, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.BACK.get(), b -> Minecraft.getInstance().setScreen(parent))
				.bounds(c + 65, height - 30, 60, 20).build());
		rebuild();
	}

	private void rebuild() {
		order.clear();
		List<EditorList.Entry> entries = new ArrayList<>();
		List<GolemModifier> keys = new ArrayList<>(map.keySet());
		keys.sort((a, b) -> a.getRegistryName().toString().compareTo(b.getRegistryName().toString()));
		for (GolemModifier k : keys) {
			order.add(k);
			entries.add(new EditorList.Entry(
					label(k).copy().append(Component.literal("   Lv " + map.get(k))), null, null));
		}
		list.setData(entries);
	}

	private static Component label(GolemModifier mod) {
		return mod.getDesc();
	}

	@Nullable
	private GolemModifier selectedKey() {
		EditorList.Entry sel = list.getSelected();
		if (sel == null) return null;
		int i = list.children().indexOf(sel);
		if (i < 0 || i >= order.size()) return null;
		return order.get(i);
	}

	private void addModifier() {
		List<GolemModifier> remaining = new ArrayList<>();
		for (GolemModifier t : candidates) {
			if (!map.containsKey(t)) {
				remaining.add(t);
			}
		}
		if (remaining.isEmpty()) {
			EditorToast.show(EditorLang.ADD.get(), EditorLang.NO_FILE.get());
			return;
		}
		Function<GolemModifier, Component> lab = ModifierMapScreen::label;
		Minecraft.getInstance().setScreen(new PickListScreen<>(EditorLang.PICK_TARGET.get(), remaining, lab, t -> null, t -> {
			promptLevel(t);
		}, this));
	}

	private void promptLevel(GolemModifier key) {
		int cur = map.getOrDefault(key, 1);
		Minecraft.getInstance().setScreen(new PromptScreen(EditorLang.LEVEL.get(), label(key), "" + cur, s -> {
			try {
				int v = Integer.parseInt(s.trim());
				if (v < 1 || v > GolemModifier.MAX_LEVEL) {
					return EditorLang.INVALID_INT.get(s);
				}
				return null;
			} catch (NumberFormatException e) {
				return EditorLang.INVALID_INT.get(s);
			}
		}, s -> {
			map.put(key, Integer.parseInt(s.trim()));
			session.dirty = true;
			Minecraft.getInstance().setScreen(ModifierMapScreen.this);
		}, this));
	}

	private void editModifier() {
		GolemModifier key = selectedKey();
		if (key == null) {
			EditorToast.show(EditorLang.EDIT.get(), EditorLang.NO_FILE.get());
			return;
		}
		promptLevel(key);
	}

	private void removeModifier() {
		GolemModifier key = selectedKey();
		if (key == null) {
			EditorToast.show(EditorLang.REMOVE.get(), EditorLang.NO_FILE.get());
			return;
		}
		map.remove(key);
		session.dirty = true;
		rebuild();
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

}
