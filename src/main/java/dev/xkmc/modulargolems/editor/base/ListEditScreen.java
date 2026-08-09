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
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ListEditScreen<T> extends EditorScreen {

	public interface Handler<T> {

		Component label(T t);

		@Nullable
		ItemStack icon(T t);

		@Nullable
		default Supplier<ItemStack> iconSupplier(T t) {
			return null;
		}

		Component summary(T t);

		void onAdd(Consumer<T> onDone, Screen parent);

		void onEdit(T cur, Consumer<T> onDone, Screen parent);

	}

	private final List<T> data;
	private final Handler<T> handler;
	private final Screen parent;
	private final EditorSession session;

	protected List<T> data() {
		return data;
	}

	protected EditorSession session() {
		return session;
	}

	private EditorList list;
	private Button addBtn;
	private Button editBtn;
	private Button removeBtn;

	public ListEditScreen(Component title, List<T> data, Handler<T> handler, Screen parent, EditorSession session) {
		super(title);
		this.data = data;
		this.handler = handler;
		this.parent = parent;
		this.session = session;
	}

	/**
	 * When non-null, an extra Save button is shown and exit() confirms unsaved changes.
	 */
	@Nullable
	protected Runnable saveAction() {
		return null;
	}

	@Override
	protected void init() {
		list = new EditorList(minecraft, width, height - 70, 30, height - 40);
		addRenderableWidget(list);
		List<Button> row = new ArrayList<>();
		addBtn = Button.builder(EditorText.ADD.get(), b -> addItem()).bounds(0, 0, 60, 20).build();
		row.add(addBtn);
		editBtn = Button.builder(EditorText.EDIT.get(), b -> editItem()).bounds(0, 0, 60, 20).build();
		row.add(editBtn);
		removeBtn = Button.builder(EditorText.REMOVE.get(), b -> removeItem()).bounds(0, 0, 60, 20).build();
		row.add(removeBtn);
		Runnable save = saveAction();
		if (save != null) {
			Button saveBtn = Button.builder(EditorText.SAVE.get(), b -> save.run()).bounds(0, 0, 60, 20).build();
			saveBtn.active = session.dirty;
			row.add(saveBtn);
		}
		row.add(Button.builder(EditorText.BACK.get(), b -> exit()).bounds(0, 0, 60, 20).build());
		row.forEach(this::addRenderableWidget);
		EditorLayout.centerRow(row, width / 2, height - 30, 5);
		editBtn.active = false;
		removeBtn.active = false;
		list.setOnSelect(() -> {
			editBtn.active = selected() != null;
			removeBtn.active = selected() != null;
		});
		list.setOnDoubleClick(this::editItem);
		rebuild();
	}

	protected void exit() {
		Runnable save = saveAction();
		if (session.dirty && save != null) {
			Minecraft.getInstance().setScreen(new ExitConfirmScreen(this, () -> {
				save.run();
				if (!session.dirty) Minecraft.getInstance().setScreen(parent);
			}, () -> Minecraft.getInstance().setScreen(parent)));
		} else {
			Minecraft.getInstance().setScreen(parent);
		}
	}

	private void rebuild() {
		List<EditorList.Entry> entries = new ArrayList<>();
		if (data.isEmpty()) {
			entries.add(new EditorList.Entry(EditorText.EMPTY_FILE.get(), null, null));
		}
		for (T t : data) {
			Component text = handler.summary(t);
			Supplier<ItemStack> supplier = handler.iconSupplier(t);
			entries.add(supplier == null
					? new EditorList.Entry(text, handler.icon(t), null)
					: EditorList.Entry.rotating(text, supplier, null));
		}
		list.setData(entries);
	}

	@Nullable
	private T selected() {
		EditorList.Entry sel = list.getSelected();
		if (sel == null) return null;
		int i = list.children().indexOf(sel);
		if (i < 0 || i >= data.size()) return null;
		return data.get(i);
	}

	private void addItem() {
		handler.onAdd(t -> {
			data.add(t);
			session.dirty = true;
			Minecraft.getInstance().setScreen(ListEditScreen.this);
		}, this);
	}

	private void editItem() {
		int i = list.children().indexOf(list.getSelected());
		if (i < 0 || i >= data.size()) return;
		T cur = data.get(i);
		handler.onEdit(cur, t -> {
			data.set(i, t);
			session.dirty = true;
			Minecraft.getInstance().setScreen(ListEditScreen.this);
		}, this);
	}

	private void removeItem() {
		T item = selected();
		if (item == null) return;
		data.remove(item);
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
		exit();
	}

}
