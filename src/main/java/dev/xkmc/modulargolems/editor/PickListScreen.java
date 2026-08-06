package dev.xkmc.modulargolems.editor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

public class PickListScreen<T> extends Screen {

	private final List<T> candidates;
	private final Function<T, Component> label;
	private final Function<T, ItemStack> icon;
	private final Consumer<T> callback;

	private EditBox search;
	private EditorList list;

	public PickListScreen(Component title, List<T> candidates, Function<T, Component> label,
						  Function<T, ItemStack> icon, Consumer<T> callback) {
		super(title);
		this.candidates = candidates;
		this.label = label;
		this.icon = icon;
		this.callback = callback;
	}

	@Override
	protected void init() {
		search = new EditBox(this.font, width / 2 - 100, 10, 200, 18, EditorLang.SEARCH.get());
		search.setMaxLength(64);
		search.setResponder(s -> refresh());
		search.setFocused(true);
		addRenderableWidget(search);
		setInitialFocus(search);
		list = new EditorList(minecraft, width, height - 44, 34, height - 10);
		addRenderableWidget(list);
		refresh();
	}

	private void refresh() {
		String q = search.getValue().toLowerCase(Locale.ROOT);
		List<EditorList.Entry> entries = new ArrayList<>();
		for (T t : candidates) {
			String name = label.apply(t).getString().toLowerCase(Locale.ROOT);
			if (q.isEmpty() || name.contains(q)) {
				ItemStack ic = icon.apply(t);
				entries.add(new EditorList.Entry(label.apply(t), ic, () -> callback.accept(t)));
			}
		}
		list.setData(entries);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (search != null && search.isFocused() && (keyCode == 257 || keyCode == 335)) {
			EditorList.Entry sel = list.getSelected();
			if (sel == null) {
				var children = list.children();
				if (!children.isEmpty()) {
					sel = children.get(0);
				}
			}
			if (sel != null) {
				sel.activate();
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, this.title, width / 2, 2, 0xFFFFFF);
	}

}
