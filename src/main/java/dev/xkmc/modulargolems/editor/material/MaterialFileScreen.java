package dev.xkmc.modulargolems.editor.material;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.editor.base.EditorFile;
import dev.xkmc.modulargolems.editor.base.EditorList;
import dev.xkmc.modulargolems.editor.base.EditorLayout;
import dev.xkmc.modulargolems.editor.base.EditorSaveState;
import dev.xkmc.modulargolems.editor.base.EditorSession;
import dev.xkmc.modulargolems.editor.base.EditorScreen;
import dev.xkmc.modulargolems.editor.base.EditorText;
import dev.xkmc.modulargolems.editor.base.EditorToast;
import dev.xkmc.modulargolems.editor.base.ExitConfirmScreen;
import dev.xkmc.modulargolems.editor.base.PickListScreen;
import dev.xkmc.modulargolems.editor.base.PromptScreen;
import dev.xkmc.modulargolems.editor.util.GolemEditorLang;
import dev.xkmc.modulargolems.editor.util.GolemEditorUtil;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class MaterialFileScreen extends EditorScreen {

	private final EditorSession session = new EditorSession();
	private final Screen parent;
	private GolemMaterialConfig config;
	private ResourceLocation fileId;

	private EditorList list;
	private final List<ResourceLocation> order = new ArrayList<>();
	private Button addBtn;
	private Button saveBtn;
	private Button editBtn;
	private Button removeBtn;

	public MaterialFileScreen(GolemMaterialConfig config, ResourceLocation fileId, Screen parent) {
		super(GolemEditorLang.MATERIALS_FILE.get());
		this.config = config;
		this.fileId = fileId;
		this.parent = parent;
	}

	@Override
	protected void init() {
		list = new EditorList(minecraft, width, height - 70, 30, height - 40);
		addRenderableWidget(list);
		List<Button> row = new ArrayList<>();
		addBtn = Button.builder(EditorText.ADD.get(), b -> addType()).bounds(0, 0, 60, 20).build();
		row.add(addBtn);
		editBtn = Button.builder(EditorText.EDIT.get(), b -> editEntry()).bounds(0, 0, 60, 20).build();
		row.add(editBtn);
		removeBtn = Button.builder(EditorText.REMOVE.get(), b -> removeEntry()).bounds(0, 0, 60, 20).build();
		row.add(removeBtn);
		saveBtn = Button.builder(EditorText.SAVE.get(), b -> save()).bounds(0, 0, 60, 20).build();
		saveBtn.active = session.dirty;
		row.add(saveBtn);
		row.add(Button.builder(EditorText.BACK.get(), b -> exitFile()).bounds(0, 0, 60, 20).build());
		row.forEach(this::addRenderableWidget);
		EditorLayout.centerRow(row, width / 2, height - 30, 5);
		editBtn.active = false;
		removeBtn.active = false;
		list.setOnSelect(() -> {
			editBtn.active = selected() != null;
			removeBtn.active = selected() != null;
		});
		rebuild();
	}

	private List<ResourceLocation> allKeys() {
		TreeSet<ResourceLocation> set = new TreeSet<>();
		set.addAll(config.stats.keySet());
		set.addAll(config.modifiers.keySet());
		set.addAll(config.ingredients.keySet());
		set.addAll(config.repairIngredients.keySet());
		set.addAll(config.partLimitation.keySet());
		return new ArrayList<>(set);
	}

	private void rebuild() {
		order.clear();
		List<EditorList.Entry> entries = new ArrayList<>();
		for (ResourceLocation id : allKeys()) {
			order.add(id);
			int stats = config.stats.getOrDefault(id, new java.util.HashMap<>()).size();
			int mods = config.modifiers.getOrDefault(id, new java.util.HashMap<>()).size();
			entries.add(new EditorList.Entry(Component.literal(id.toString()).copy()
					.append(Component.literal("   ["))
					.append(GolemEditorLang.STATS.get(stats))
					.append(Component.literal("  "))
					.append(GolemEditorLang.MODIFIERS.get(mods))
					.append(Component.literal("]"))
					, null, null));
		}
		if (entries.isEmpty()) {
			entries.add(new EditorList.Entry(EditorText.EMPTY_FILE.get(), null, null));
		}
		list.setData(entries);
		updateAddBtn();
	}

	private void updateAddBtn() {
		addBtn.active = GolemTypes.TYPES.get().getValues().stream()
				.anyMatch(t -> !config.stats.containsKey(t.getRegistryName()));
	}

	@Nullable
	private ResourceLocation selected() {
		EditorList.Entry sel = list.getSelected();
		if (sel == null) return null;
		int i = list.children().indexOf(sel);
		if (i < 0 || i >= order.size()) return null;
		return order.get(i);
	}

	private void addType() {
		List<GolemType<?, ?>> remaining = new ArrayList<>();
		for (GolemType<?, ?> t : GolemTypes.TYPES.get().getValues()) {
			if (!config.stats.containsKey(t.getRegistryName())) {
				remaining.add(t);
			}
		}
		if (remaining.isEmpty()) {
			EditorToast.show(EditorText.ADD.get(), EditorText.NO_FILE.get());
			return;
		}
		Minecraft.getInstance().setScreen(new PickListScreen<>(GolemEditorLang.SELECT_TYPE.get(), remaining,
				new AddTypeHandler(this), this));
	}

	private record AddTypeHandler(MaterialFileScreen screen) implements PickListScreen.Handler<GolemType<?, ?>> {

		@Override
		public Component label(GolemType<?, ?> t) {
			return t.getDesc();
		}

		@Override
		@Nullable
		public ItemStack icon(GolemType<?, ?> t) {
			var holder = GolemType.GOLEM_TYPE_TO_ITEM.get(t.getRegistryName());
			return holder == null ? null : new ItemStack(holder);
		}

		@Override
		public void onSelect(GolemType<?, ?> t) {
			ResourceLocation id = t.getRegistryName();
			Minecraft.getInstance().setScreen(new MaterialEntryScreen(screen.config, id, screen, screen.session));
		}

	}

	private void editEntry() {
		ResourceLocation id = selected();
		if (id == null) return;
		Minecraft.getInstance().setScreen(new MaterialEntryScreen(config, id, MaterialFileScreen.this, session));
	}

	private void removeEntry() {
		ResourceLocation id = selected();
		if (id == null) return;
		config.stats.remove(id);
		config.modifiers.remove(id);
		config.ingredients.remove(id);
		config.repairIngredients.remove(id);
		config.partLimitation.remove(id);
		session.dirty = true;
		rebuild();
	}

	private void save() {
		Minecraft.getInstance().setScreen(new PromptScreen(EditorText.SAVE.get(), EditorText.FILE_ID.get(),
				fileId.toString(), GolemEditorUtil::validateFileId, s -> {
					ResourceLocation id = EditorFile.parseId(s);
					if (id == null) return;
					fileId = id;
					if (doSave()) {
						Minecraft.getInstance().setScreen(MaterialFileScreen.this);
					}
				}, this));
	}

	private boolean doSave() {
		try {
			GolemEditorUtil.save(ModularGolems.MATERIALS, fileId, config);
			EditorSaveState.savedFlag = true;
			session.dirty = false;
			EditorToast.show(EditorText.SAVE.get(), EditorText.SAVE_DONE.get(fileId));
			EditorToast.show(EditorText.SAVE.get(), EditorText.SAVE_NOTE.get());
			return true;
		} catch (Exception e) {
			EditorToast.show(EditorText.SAVE_FAIL.get(e.getMessage()), EditorText.NOT_IN_WORLD.get());
			return false;
		}
	}

	private void exitFile() {
		if (session.dirty) {
			Minecraft.getInstance().setScreen(new ExitConfirmScreen(this, () -> {
				if (doSave()) {
					Minecraft.getInstance().setScreen(parent);
				}
			}, () -> Minecraft.getInstance().setScreen(parent)));
		} else {
			Minecraft.getInstance().setScreen(parent);
		}
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, EditorText.FILE.get(fileId), width / 2, 10, 0xFFFFFF);
	}

	@Override
	public void onClose() {
		exitFile();
	}

}
