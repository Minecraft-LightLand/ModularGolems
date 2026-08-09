package dev.xkmc.modulargolems.editor.base;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ValueMapScreen<K, V> extends EditorScreen {

	public interface Handler<K, V> {

		Component keyLabel(K k);

		@Nullable
		ItemStack keyIcon(K k);

		List<K> allKeys();

		String keyDefault();

		@Nullable
		Function<String, Component> keyValidate();

		Component valueSummary(V v);

		void openValue(Screen parent, @Nullable V current, Consumer<V> onDone);

	}

	@Nullable
	private Map<K, V> map;
	private final Supplier<Map<K, V>> create;
	private final Handler<K, V> handler;
	private final Screen parent;
	private final EditorSession session;

	private EditorList list;
	private final List<K> order = new ArrayList<>();
	private Button addBtn;
	private Button editBtn;
	private Button removeBtn;

	public ValueMapScreen(Component title, @Nullable Map<K, V> map, Supplier<Map<K, V>> create,
	                      Handler<K, V> handler, Screen parent, EditorSession session) {
		super(title);
		this.map = map;
		this.create = create;
		this.handler = handler;
		this.parent = parent;
		this.session = session;
	}

	private Map<K, V> view() {
		return map == null ? Collections.emptyMap() : map;
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
		row.add(Button.builder(EditorText.BACK.get(), b -> onClose()).bounds(0, 0, 60, 20).build());
		row.forEach(this::addRenderableWidget);
		EditorLayout.centerRow(row, width / 2, height - 30, 5);
		editBtn.active = false;
		removeBtn.active = false;
		list.setOnSelect(() -> {
			editBtn.active = selectedKey() != null;
			removeBtn.active = selectedKey() != null;
		});
		list.setOnDoubleClick(this::editValue);
		rebuild();
	}

	private void rebuild() {
		order.clear();
		List<EditorList.Entry> entries = new ArrayList<>();
		List<K> keys = new ArrayList<>(view().keySet());
		keys.sort((a, b) -> handler.keyLabel(a).getString().compareToIgnoreCase(handler.keyLabel(b).getString()));
		for (K k : keys) {
			order.add(k);
			entries.add(new EditorList.Entry(
					handler.keyLabel(k).copy().append(Component.literal("   ")).append(handler.valueSummary(view().get(k)).copy().withStyle(ChatFormatting.GRAY)),
					handler.keyIcon(k), null));
		}
		list.setData(entries);
		updateAddBtn();
	}

	private List<K> remaining() {
		List<K> remaining = new ArrayList<>();
		for (K t : handler.allKeys()) {
			if (!view().containsKey(t)) {
				remaining.add(t);
			}
		}
		return remaining;
	}

	private void updateAddBtn() {
		addBtn.active = !handler.allKeys().isEmpty() || handler.keyValidate() != null;
	}

	@Nullable
	private K selectedKey() {
		EditorList.Entry sel = list.getSelected();
		if (sel == null) return null;
		int i = list.children().indexOf(sel);
		if (i < 0 || i >= order.size()) return null;
		return order.get(i);
	}

	private void addValue() {
		List<K> remaining = remaining();
		if (remaining.isEmpty() && handler.keyValidate() == null) {
			EditorToast.show(EditorText.ADD.get(), EditorText.NO_FILE.get());
			return;
		}
		if (!remaining.isEmpty()) {
			Minecraft.getInstance().setScreen(new PickListScreen<>(EditorText.PICK_TARGET.get(), remaining,
					new AddValueHandler<>(this, handler), this));
		} else {
			Minecraft.getInstance().setScreen(new PromptScreen(EditorText.PICK_TARGET.get(), EditorText.FILE_ID.get(),
					handler.keyDefault(), this::validateKey, this::applyTypedKey, this));
		}
	}

	@Nullable
	private Component validateKey(String s) {
		Function<String, Component> validate = handler.keyValidate();
		Component err = validate == null ? null : validate.apply(s);
		if (err != null) return err;
		ResourceLocation rl = EditorFile.parseId(s);
		if (rl == null) return EditorText.INVALID_ID.get(s);
		if (view().containsKey(rl)) return EditorText.INVALID_ID.get(s);
		return null;
	}

	private void applyTypedKey(String s) {
		ResourceLocation rl = EditorFile.parseId(s);
		if (rl == null) return;
		applyAdd(rl);
	}

	@SuppressWarnings("unchecked")
	private <T> T castKey(Object k) {
		return (T) k;
	}

	private void applyAdd(Object key) {
		handler.openValue(this, null, v -> {
			if (map == null) map = create.get();
			map.put(castKey(key), v);
			session.dirty = true;
			Minecraft.getInstance().setScreen(ValueMapScreen.this);
		});
	}

	private void editValue() {
		K key = selectedKey();
		if (key == null) return;
		handler.openValue(this, view().get(key), v -> {
			if (map == null) map = create.get();
			map.put(key, v);
			session.dirty = true;
			Minecraft.getInstance().setScreen(ValueMapScreen.this);
		});
	}

	private void removeValue() {
		K key = selectedKey();
		if (key == null) return;
		if (map != null) map.remove(key);
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

	private record AddValueHandler<K, V>(ValueMapScreen<K, V> screen,
	                                     Handler<K, V> handler) implements PickListScreen.Handler<K> {

		@Override
		public Component label(K t) {
			return handler.keyLabel(t);
		}

		@Override
		@Nullable
		public ItemStack icon(K t) {
			return handler.keyIcon(t);
		}

		@Override
		public void onSelect(K t) {
			handler.openValue(screen, null, v -> {
				if (screen.map == null) screen.map = screen.create.get();
				screen.map.put(t, v);
				screen.session.dirty = true;
				Minecraft.getInstance().setScreen(screen);
			});
		}

	}

}
