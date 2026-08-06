package dev.xkmc.modulargolems.editor;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemType;
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
import java.util.function.Function;

public class MaterialFileScreen extends Screen {

	private final GolemMaterialConfig config;
	private ResourceLocation fileId;

	private EditorList list;
	private final List<ResourceLocation> order = new ArrayList<>();

	public MaterialFileScreen(GolemMaterialConfig config, ResourceLocation fileId) {
		super(EditorLang.MATERIALS_FILE.get());
		this.config = config;
		this.fileId = fileId;
	}

	@Override
	protected void init() {
		list = new EditorList(minecraft, width, height - 70, 30, height - 40);
		addRenderableWidget(list);
		int c = width / 2;
		addRenderableWidget(Button.builder(EditorLang.ADD.get(), b -> addType())
				.bounds(c - 155, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.EDIT.get(), b -> editEntry())
				.bounds(c - 90, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.REMOVE.get(), b -> removeEntry())
				.bounds(c - 25, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.SAVE.get(), b -> save())
				.bounds(c + 40, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.BACK.get(), b -> onClose())
				.bounds(c + 105, height - 30, 60, 20).build());
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
					.append(Component.literal("   [" + EditorLang.STATS.get(stats).getString()
							+ "  " + EditorLang.MODIFIERS.get(mods).getString() + "]"))
					, null, null));
		}
		if (entries.isEmpty()) {
			entries.add(new EditorList.Entry(EditorLang.EMPTY_FILE.get(), null, null));
		}
		list.setData(entries);
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
			EditorToast.show(EditorLang.ADD.get(), EditorLang.NO_FILE.get());
			return;
		}
		Function<GolemType<?, ?>, ItemStack> icon = t -> {
			var holder = GolemType.GOLEM_TYPE_TO_ITEM.get(t.getRegistryName());
			return holder == null ? null : new ItemStack(holder);
		};
		Minecraft.getInstance().setScreen(new PickListScreen<>(EditorLang.SELECT_TYPE.get(), remaining,
				t -> t.getDesc(), icon, t -> {
					ResourceLocation id = t.getRegistryName();
					config.stats.computeIfAbsent(id, k -> new java.util.LinkedHashMap<>());
					config.modifiers.computeIfAbsent(id, k -> new java.util.LinkedHashMap<>());
					config.ingredients.computeIfAbsent(id, k -> net.minecraft.world.item.crafting.Ingredient.EMPTY);
					Minecraft.getInstance().setScreen(new MaterialEntryScreen(config, id, MaterialFileScreen.this));
				}));
	}

	private void editEntry() {
		ResourceLocation id = selected();
		if (id == null) {
			EditorToast.show(EditorLang.EDIT.get(), EditorLang.NO_FILE.get());
			return;
		}
		Minecraft.getInstance().setScreen(new MaterialEntryScreen(config, id, MaterialFileScreen.this));
	}

	private void removeEntry() {
		ResourceLocation id = selected();
		if (id == null) {
			EditorToast.show(EditorLang.REMOVE.get(), EditorLang.NO_FILE.get());
			return;
		}
		config.stats.remove(id);
		config.modifiers.remove(id);
		config.ingredients.remove(id);
		config.repairIngredients.remove(id);
		config.partLimitation.remove(id);
		rebuild();
	}

	private void save() {
		Minecraft.getInstance().setScreen(new PromptScreen(EditorLang.SAVE.get(), EditorLang.FILE_ID.get(),
				fileId.toString(), EditorData::validateFileId, s -> {
					ResourceLocation id = EditorData.parseId(s);
					if (id == null) return;
					fileId = id;
					try {
						java.nio.file.Path file = EditorData.save(ModularGolems.MATERIALS, id, config);
						EditorToast.show(EditorLang.SAVE.get(), EditorLang.SAVE_DONE.get(file));
						EditorToast.show(EditorLang.SAVE.get(), EditorLang.SAVE_NOTE.get());
						Minecraft.getInstance().setScreen(new EditorHomeScreen());
					} catch (Exception e) {
						EditorToast.show(EditorLang.SAVE_FAIL.get(e.getMessage()), EditorLang.NOT_IN_WORLD.get());
						Minecraft.getInstance().setScreen(MaterialFileScreen.this);
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
