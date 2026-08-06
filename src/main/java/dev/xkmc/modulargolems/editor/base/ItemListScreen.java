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
import java.util.Set;

public class ItemListScreen<T> extends Screen {

	public interface Handler<T> {

		Component label(T t);

		@Nullable
		ItemStack icon(T t);

	}

	private final Set<T> set;
	private final List<T> candidates;
	private final Handler<T> handler;
	private final Component pickTitle;
	private final Screen parent;
	private final EditorSession session;

	private EditorList list;
	private final List<T> order = new ArrayList<>();

	public ItemListScreen(Component title, Set<T> set, List<T> candidates,
						  Handler<T> handler, Component pickTitle, Screen parent, EditorSession session) {
		super(title);
		this.set = set;
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
		int c = width / 2;
		addRenderableWidget(Button.builder(EditorText.ADD.get(), b -> addItem())
				.bounds(c - 100, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorText.REMOVE.get(), b -> removeItem())
				.bounds(c - 30, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorText.BACK.get(), b -> Minecraft.getInstance().setScreen(parent))
				.bounds(c + 40, height - 30, 60, 20).build());
		rebuild();
	}

	private void rebuild() {
		order.clear();
		List<EditorList.Entry> entries = new ArrayList<>();
		List<T> keys = new ArrayList<>(set);
		keys.sort((a, b) -> handler.label(a).getString().compareToIgnoreCase(handler.label(b).getString()));
		for (T k : keys) {
			order.add(k);
			entries.add(new EditorList.Entry(handler.label(k), handler.icon(k), null));
		}
		list.setData(entries);
	}

	@Nullable
	private T selected() {
		EditorList.Entry sel = list.getSelected();
		if (sel == null) return null;
		int i = list.children().indexOf(sel);
		if (i < 0 || i >= order.size()) return null;
		return order.get(i);
	}

	private void addItem() {
		List<T> remaining = new ArrayList<>();
		for (T t : candidates) {
			if (!set.contains(t)) {
				remaining.add(t);
			}
		}
		if (remaining.isEmpty()) {
			EditorToast.show(EditorText.ADD.get(), EditorText.NO_FILE.get());
			return;
		}
		Minecraft.getInstance().setScreen(new PickListScreen<>(pickTitle, remaining,
				new AddItemHandler<>(this, handler), this));
	}

	private record AddItemHandler<T>(ItemListScreen<T> screen, Handler<T> handler) implements PickListScreen.Handler<T> {

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
			screen.set.add(t);
			screen.session.dirty = true;
			Minecraft.getInstance().setScreen(screen);
		}

	}

	private void removeItem() {
		T item = selected();
		if (item == null) {
			EditorToast.show(EditorText.REMOVE.get(), EditorText.NO_FILE.get());
			return;
		}
		set.remove(item);
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
