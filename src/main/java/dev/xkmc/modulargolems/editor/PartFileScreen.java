package dev.xkmc.modulargolems.editor;

import dev.xkmc.modulargolems.content.config.GolemPartConfig;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
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

	private final GolemPartConfig config;
	private ResourceLocation fileId;

	private EditorList list;
	private final List<Item> partOrder = new ArrayList<>();
	private final List<ResourceLocation> entOrder = new ArrayList<>();

	public PartFileScreen(GolemPartConfig config, ResourceLocation fileId) {
		super(EditorLang.PARTS_FILE.get());
		this.config = config;
		this.fileId = fileId;
	}

	@Override
	protected void init() {
		list = new EditorList(minecraft, width, height - 70, 30, height - 40);
		addRenderableWidget(list);
		int c = width / 2;
		addRenderableWidget(Button.builder(EditorLang.ADD_PART.get(), b -> addPart())
				.bounds(c - 160, height - 30, 80, 20).build());
		addRenderableWidget(Button.builder(EditorLang.ADD_MAGNIFIER.get(), b -> addEntity())
				.bounds(c - 75, height - 30, 80, 20).build());
		addRenderableWidget(Button.builder(EditorLang.REMOVE.get(), b -> removeEntry())
				.bounds(c + 10, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.SAVE.get(), b -> save())
				.bounds(c + 75, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.BACK.get(), b -> onClose())
				.bounds(c + 140, height - 30, 60, 20).build());
		rebuild();
	}

	private void rebuild() {
		partOrder.clear();
		entOrder.clear();
		List<EditorList.Entry> entries = new ArrayList<>();
		List<Item> parts = new ArrayList<>(config.filters.keySet());
		parts.sort((a, b) -> EditorData.itemName(a).getString().compareToIgnoreCase(EditorData.itemName(b).getString()));
		for (Item part : parts) {
			partOrder.add(part);
			int n = config.filters.get(part).size();
			entries.add(new EditorList.Entry(EditorData.itemName(part).copy()
					.append(Component.literal("   " + EditorLang.FILTERS.get(n).getString()))
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
					.append(Component.literal("   " + EditorLang.MAGNIFIERS.get(n).getString()))
					, icon, () -> editEntity(id)));
		}
		if (entries.isEmpty()) {
			entries.add(new EditorList.Entry(EditorLang.EMPTY_FILE.get(), null, null));
		}
		list.setData(entries);
	}

	private void editPart(Item part) {
		var map = config.filters.computeIfAbsent(part, k -> new java.util.LinkedHashMap<>());
		List<StatFilterType> cand = List.of(StatFilterType.values());
		Minecraft.getInstance().setScreen(new DoubleMapScreen<>(EditorLang.FILTERS.get(map.size()), map, cand,
				t -> Component.literal(t.name()), t -> null, t -> false, PartFileScreen.this));
	}

	private void editEntity(ResourceLocation id) {
		var map = config.magnifiers.computeIfAbsent(id, k -> new java.util.LinkedHashMap<>());
		Minecraft.getInstance().setScreen(new DoubleMapScreen<>(EditorLang.MAGNIFIERS.get(map.size()), map,
				EditorData.listStats(), EditorData::statName, t -> null, GolemStatType::percentDisplay, PartFileScreen.this));
	}

	private void addPart() {
		List<Item> remaining = new ArrayList<>();
		for (Item t : EditorData.listParts()) {
			if (!config.filters.containsKey(t)) {
				remaining.add(t);
			}
		}
		if (remaining.isEmpty()) {
			EditorToast.show(EditorLang.ADD_PART.get(), EditorLang.NO_FILE.get());
			return;
		}
		Minecraft.getInstance().setScreen(new PickListScreen<>(EditorLang.SELECT_PART.get(), remaining,
				EditorData::itemName, ItemStack::new, part -> {
					config.filters.computeIfAbsent(part, k -> new java.util.LinkedHashMap<>());
					editPart(part);
				}));
	}

	private void addEntity() {
		List<GolemType<?, ?>> remaining = new ArrayList<>();
		for (GolemType<?, ?> t : EditorData.listGolemTypes()) {
			if (!config.magnifiers.containsKey(t.getRegistryName())) {
				remaining.add(t);
			}
		}
		if (remaining.isEmpty()) {
			EditorToast.show(EditorLang.ADD_MAGNIFIER.get(), EditorLang.NO_FILE.get());
			return;
		}
		Minecraft.getInstance().setScreen(new PickListScreen<>(EditorLang.SELECT_ENTITY.get(), remaining,
				t -> t.getDesc(), t -> {
					var holder = GolemType.GOLEM_TYPE_TO_ITEM.get(t.getRegistryName());
					return holder == null ? null : new ItemStack(holder);
				}, t -> {
					config.magnifiers.computeIfAbsent(t.getRegistryName(), k -> new java.util.LinkedHashMap<>());
					editEntity(t.getRegistryName());
				}));
	}

	private void removeEntry() {
		EditorList.Entry sel = list.getSelected();
		if (sel == null) {
			EditorToast.show(EditorLang.REMOVE.get(), EditorLang.NO_FILE.get());
			return;
		}
		int i = list.children().indexOf(sel);
		if (i < 0) return;
		if (i < partOrder.size()) {
			config.filters.remove(partOrder.get(i));
		} else if (i < partOrder.size() + entOrder.size()) {
			config.magnifiers.remove(entOrder.get(i - partOrder.size()));
		}
		rebuild();
	}

	private void save() {
		Minecraft.getInstance().setScreen(new PromptScreen(EditorLang.SAVE.get(), EditorLang.FILE_ID.get(),
				fileId.toString(), EditorData::validateFileId, s -> {
					ResourceLocation id = EditorData.parseId(s);
					if (id == null) return;
					fileId = id;
					try {
						java.nio.file.Path file = EditorData.save(ModularGolems.PARTS, id, config);
						EditorToast.show(EditorLang.SAVE.get(), EditorLang.SAVE_DONE.get(file));
						EditorToast.show(EditorLang.SAVE.get(), EditorLang.SAVE_NOTE.get());
						Minecraft.getInstance().setScreen(new EditorHomeScreen());
					} catch (Exception e) {
						EditorToast.show(EditorLang.SAVE_FAIL.get(e.getMessage()), EditorLang.NOT_IN_WORLD.get());
						Minecraft.getInstance().setScreen(PartFileScreen.this);
					}
				}));
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, EditorLang.FILE.get(fileId), width / 2, 10, 0xFFFFFF);
	}

}
