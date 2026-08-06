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
import java.util.Map;

public class Obj2IntMapScreen<M> extends EditorScreen {

	public interface Handler<M> {

		Component label(M m);

		int maxLevel(M m);

	}

	private final Map<M, Integer> map;
	private final List<M> candidates;
	private final Handler<M> handler;
	private final Component pickTitle;
	private final Screen parent;
	private final EditorSession session;

	private EditorList list;
	private final List<M> order = new ArrayList<>();
	private Button addBtn;
	private Button editBtn;
	private Button removeBtn;

	public Obj2IntMapScreen(Component title, Map<M, Integer> map, List<M> candidates,
							 Handler<M> handler, Component pickTitle,
							 Screen parent, EditorSession session) {
		super(title);
		this.map = map;
		this.candidates = candidates;
		this.handler = handler;
		this.pickTitle = pickTitle;
		this.parent = parent;
		this.session = session;
	}

	@Override
	protected void init() {
		list = new EditorList(minecraft, width, height - 70, 30, height - 40);
		addRenderableWidget(list);
		List<Button> row = new ArrayList<>();
		addBtn = Button.builder(EditorText.ADD.get(), b -> addModifier()).bounds(0, 0, 60, 20).build();
		row.add(addBtn);
		editBtn = Button.builder(EditorText.EDIT.get(), b -> editModifier()).bounds(0, 0, 60, 20).build();
		row.add(editBtn);
		removeBtn = Button.builder(EditorText.REMOVE.get(), b -> removeModifier()).bounds(0, 0, 60, 20).build();
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
		List<M> keys = new ArrayList<>(map.keySet());
		keys.sort((a, b) -> handler.label(a).getString().compareToIgnoreCase(handler.label(b).getString()));
		for (M k : keys) {
			order.add(k);
			entries.add(new EditorList.Entry(
					handler.label(k).copy().append(Component.literal("   ")).append(EditorText.LEVEL_FULL.get(map.get(k), handler.maxLevel(k))), null, null));
		}
		list.setData(entries);
		updateAddBtn();
	}

	private List<M> remaining() {
		List<M> remaining = new ArrayList<>();
		for (M t : candidates) {
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
	private M selectedKey() {
		EditorList.Entry sel = list.getSelected();
		if (sel == null) return null;
		int i = list.children().indexOf(sel);
		if (i < 0 || i >= order.size()) return null;
		return order.get(i);
	}

	private void addModifier() {
		List<M> remaining = remaining();
		if (remaining.isEmpty()) {
			EditorToast.show(EditorText.ADD.get(), EditorText.NO_FILE.get());
			return;
		}
		Minecraft.getInstance().setScreen(new PickListScreen<>(pickTitle, remaining,
				new AddModifierHandler<>(this, handler), this));
	}

	private void promptLevel(M key) {
		int max = handler.maxLevel(key);
		int cur = map.getOrDefault(key, 1);
		Minecraft.getInstance().setScreen(new PromptScreen(EditorText.LEVEL_RANGE.get(max), handler.label(key), "" + cur, s -> {
			try {
				int v = Integer.parseInt(s.trim());
				if (v < 1 || v > max) {
					return EditorText.INVALID_INT.get(max, s);
				}
				return null;
			} catch (NumberFormatException e) {
				return EditorText.INVALID_INT.get(max, s);
			}
		}, s -> {
			map.put(key, Integer.parseInt(s.trim()));
			session.dirty = true;
			Minecraft.getInstance().setScreen(Obj2IntMapScreen.this);
		}, this));
	}

	private void editModifier() {
		M key = selectedKey();
		if (key == null) return;
		promptLevel(key);
	}

	private void removeModifier() {
		M key = selectedKey();
		if (key == null) return;
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

	private record AddModifierHandler<M>(Obj2IntMapScreen<M> screen, Handler<M> handler) implements PickListScreen.Handler<M> {

		@Override
		public Component label(M m) {
			return handler.label(m).copy().append(Component.literal("  ")).append(EditorText.MAX_SHORT.get(handler.maxLevel(m)));
		}

		@Override
		@Nullable
		public ItemStack icon(M m) {
			return null;
		}

		@Override
		public void onSelect(M m) {
			screen.promptLevel(m);
		}

	}

}
