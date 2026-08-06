package dev.xkmc.modulargolems.editor.util;

import dev.xkmc.modulargolems.editor.base.EditorList;
import dev.xkmc.modulargolems.editor.base.EditorSession;
import dev.xkmc.modulargolems.editor.base.EditorText;
import dev.xkmc.modulargolems.editor.base.EditorToast;
import dev.xkmc.modulargolems.editor.base.PickListScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemListScreen extends Screen {

	private final Set<Item> set;
	private final List<Item> candidates;
	private final Screen parent;
	private final EditorSession session;

	private EditorList list;
	private final List<Item> order = new ArrayList<>();

	public ItemListScreen(Component title, Set<Item> set, List<Item> candidates,
						  Screen parent, EditorSession session) {
		super(title);
		this.set = set;
		this.candidates = candidates;
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
		List<Item> keys = new ArrayList<>(set);
		keys.sort((a, b) -> EditorData.itemName(a).getString().compareToIgnoreCase(EditorData.itemName(b).getString()));
		for (Item k : keys) {
			order.add(k);
			entries.add(new EditorList.Entry(EditorData.itemName(k), new ItemStack(k), null));
		}
		list.setData(entries);
	}

	@Nullable
	private Item selectedItem() {
		EditorList.Entry sel = list.getSelected();
		if (sel == null) return null;
		int i = list.children().indexOf(sel);
		if (i < 0 || i >= order.size()) return null;
		return order.get(i);
	}

	private void addItem() {
		List<Item> remaining = new ArrayList<>();
		for (Item t : candidates) {
			if (!set.contains(t)) {
				remaining.add(t);
			}
		}
		if (remaining.isEmpty()) {
			EditorToast.show(EditorText.ADD.get(), EditorText.NO_FILE.get());
			return;
		}
		Minecraft.getInstance().setScreen(new PickListScreen<>(EditorLang.SELECT_ITEM.get(), remaining,
				EditorData::itemName, ItemStack::new, item -> {
					set.add(item);
					session.dirty = true;
					Minecraft.getInstance().setScreen(ItemListScreen.this);
				}, this));
	}

	private void removeItem() {
		Item item = selectedItem();
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
