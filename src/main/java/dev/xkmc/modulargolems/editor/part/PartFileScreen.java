package dev.xkmc.modulargolems.editor.part;

import dev.xkmc.modulargolems.content.config.GolemPartConfig;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.editor.base.DoubleMapScreen;
import dev.xkmc.modulargolems.editor.base.EditorFile;
import dev.xkmc.modulargolems.editor.base.EditorList;
import dev.xkmc.modulargolems.editor.base.EditorSaveState;
import dev.xkmc.modulargolems.editor.base.EditorSession;
import dev.xkmc.modulargolems.editor.base.EditorText;
import dev.xkmc.modulargolems.editor.base.EditorToast;
import dev.xkmc.modulargolems.editor.base.EditorUtil;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PartFileScreen extends Screen {

	private final EditorSession session = new EditorSession();
	private final Screen parent;
	private GolemPartConfig config;
	private ResourceLocation fileId;

	private EditorList list;
	private final List<Item> partOrder = new ArrayList<>();
	private final List<ResourceLocation> entOrder = new ArrayList<>();
	private Button saveBtn;

	public PartFileScreen(GolemPartConfig config, ResourceLocation fileId, Screen parent) {
		super(GolemEditorLang.PARTS_FILE.get());
		this.config = config;
		this.fileId = fileId;
		this.parent = parent;
	}

	@Override
	protected void init() {
		list = new EditorList(minecraft, width, height - 90, 30, height - 64);
		addRenderableWidget(list);
		int c = width / 2;
		addRenderableWidget(Button.builder(GolemEditorLang.ADD_PART.get(), b -> addPart())
				.bounds(c - 160, height - 56, 80, 20).build());
		addRenderableWidget(Button.builder(GolemEditorLang.ADD_MAGNIFIER.get(), b -> addEntity())
				.bounds(c - 75, height - 56, 80, 20).build());
		addRenderableWidget(Button.builder(EditorText.REMOVE.get(), b -> removeEntry())
				.bounds(c + 10, height - 56, 60, 20).build());
		saveBtn = Button.builder(EditorText.SAVE.get(), b -> save())
				.bounds(c - 65, height - 30, 60, 20).build();
		saveBtn.active = session.dirty;
		addRenderableWidget(saveBtn);
		addRenderableWidget(Button.builder(EditorText.BACK.get(), b -> exitFile())
				.bounds(c + 5, height - 30, 60, 20).build());
		rebuild();
	}

	private void rebuild() {
		partOrder.clear();
		entOrder.clear();
		List<EditorList.Entry> entries = new ArrayList<>();
		List<Item> parts = new ArrayList<>(config.filters.keySet());
		parts.sort((a, b) -> EditorUtil.itemName(a).getString().compareToIgnoreCase(EditorUtil.itemName(b).getString()));
		for (Item part : parts) {
			partOrder.add(part);
			int n = config.filters.get(part).size();
			entries.add(new EditorList.Entry(EditorUtil.itemName(part).copy()
					.append(Component.literal("   ")).append(GolemEditorLang.FILTERS.get(n))
					, new ItemStack(part), () -> editPart(part)));
		}
		List<ResourceLocation> ents = new ArrayList<>(config.magnifiers.keySet());
		ents.sort(ResourceLocation::compareTo);
		for (ResourceLocation id : ents) {
			entOrder.add(id);
			int n = config.magnifiers.get(id).size();
			ItemStack icon = null;
			GolemType<?, ?> t = GolemTypes.TYPES.get().getValue(id);
			var holder = t == null ? null : GolemType.GOLEM_TYPE_TO_ITEM.get(id);
			if (holder != null) icon = new ItemStack(holder);
			entries.add(new EditorList.Entry(Component.literal(id.toString()).copy()
					.append(Component.literal("   ")).append(GolemEditorLang.MAGNIFIERS.get(n))
					, icon, () -> editEntity(id)));
		}
		if (entries.isEmpty()) {
			entries.add(new EditorList.Entry(EditorText.EMPTY_FILE.get(), null, null));
		}
		list.setData(entries);
	}

	private void editPart(Item part) {
		var map = config.filters.computeIfAbsent(part, k -> new java.util.LinkedHashMap<>());
		List<StatFilterType> cand = List.of(StatFilterType.values());
		Minecraft.getInstance().setScreen(new DoubleMapScreen<>(GolemEditorLang.FILTERS.get(map.size()), map, cand,
				GolemEditorUtil::statFilterName, t -> null, t -> false, PartFileScreen.this, session));
	}

	private void editEntity(ResourceLocation id) {
		var map = config.magnifiers.computeIfAbsent(id, k -> new java.util.LinkedHashMap<>());
		Minecraft.getInstance().setScreen(new DoubleMapScreen<>(GolemEditorLang.MAGNIFIERS.get(map.size()), map,
				GolemEditorUtil.listStats(), GolemEditorUtil::statName, t -> null, GolemStatType::percentDisplay, PartFileScreen.this, session));
	}

	private void addPart() {
		List<Item> remaining = new ArrayList<>();
		for (Item t : GolemEditorUtil.listParts()) {
			if (!config.filters.containsKey(t)) {
				remaining.add(t);
			}
		}
		if (remaining.isEmpty()) {
			EditorToast.show(GolemEditorLang.ADD_PART.get(), EditorText.NO_FILE.get());
			return;
		}
		Minecraft.getInstance().setScreen(new PickListScreen<>(GolemEditorLang.SELECT_PART.get(), remaining,
				EditorUtil::itemName, ItemStack::new, part -> {
					config.filters.computeIfAbsent(part, k -> new java.util.LinkedHashMap<>());
					session.dirty = true;
					editPart(part);
				}, this));
	}

	private void addEntity() {
		List<GolemType<?, ?>> remaining = new ArrayList<>();
		for (GolemType<?, ?> t : GolemEditorUtil.listGolemTypes()) {
			if (!config.magnifiers.containsKey(t.getRegistryName())) {
				remaining.add(t);
			}
		}
		if (remaining.isEmpty()) {
			EditorToast.show(GolemEditorLang.ADD_MAGNIFIER.get(), EditorText.NO_FILE.get());
			return;
		}
		Minecraft.getInstance().setScreen(new PickListScreen<>(GolemEditorLang.SELECT_ENTITY.get(), remaining,
				t -> t.getDesc(), t -> {
					var holder = GolemType.GOLEM_TYPE_TO_ITEM.get(t.getRegistryName());
					return holder == null ? null : new ItemStack(holder);
				}, t -> {
					config.magnifiers.computeIfAbsent(t.getRegistryName(), k -> new java.util.LinkedHashMap<>());
					session.dirty = true;
					editEntity(t.getRegistryName());
				}, this));
	}

	private void removeEntry() {
		EditorList.Entry sel = list.getSelected();
		if (sel == null) {
			EditorToast.show(EditorText.REMOVE.get(), EditorText.NO_FILE.get());
			return;
		}
		int i = list.children().indexOf(sel);
		if (i < 0) return;
		if (i < partOrder.size()) {
			config.filters.remove(partOrder.get(i));
		} else if (i < partOrder.size() + entOrder.size()) {
			config.magnifiers.remove(entOrder.get(i - partOrder.size()));
		}
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
						Minecraft.getInstance().setScreen(PartFileScreen.this);
					}
				}, this));
	}

	private boolean doSave() {
		try {
			GolemEditorUtil.save(ModularGolems.PARTS, fileId, config);
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
