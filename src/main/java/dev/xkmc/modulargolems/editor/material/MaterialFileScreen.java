package dev.xkmc.modulargolems.editor.material;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.editor.base.EditorFile;
import dev.xkmc.modulargolems.editor.base.EditorList;
import dev.xkmc.modulargolems.editor.base.EditorSession;
import dev.xkmc.modulargolems.editor.base.EditorText;
import dev.xkmc.modulargolems.editor.base.EditorToast;
import dev.xkmc.modulargolems.editor.base.ExitConfirmScreen;
import dev.xkmc.modulargolems.editor.base.PickListScreen;
import dev.xkmc.modulargolems.editor.base.PromptScreen;
import dev.xkmc.modulargolems.editor.util.EditorData;
import dev.xkmc.modulargolems.editor.util.EditorLang;
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

	private final EditorSession session = new EditorSession();
	private final Screen parent;
	private GolemMaterialConfig config;
	private ResourceLocation fileId;

	private EditorList list;
	private final List<ResourceLocation> order = new ArrayList<>();
	private Button saveBtn;

	public MaterialFileScreen(GolemMaterialConfig config, ResourceLocation fileId, Screen parent) {
		super(EditorLang.MATERIALS_FILE.get());
		this.config = config;
		this.fileId = fileId;
		this.parent = parent;
	}

	@Override
	protected void init() {
		list = new EditorList(minecraft, width, height - 90, 30, height - 64);
		addRenderableWidget(list);
		int c = width / 2;
		addRenderableWidget(Button.builder(EditorText.ADD.get(), b -> addType())
				.bounds(c - 145, height - 56, 60, 20).build());
		addRenderableWidget(Button.builder(EditorText.EDIT.get(), b -> editEntry())
				.bounds(c - 80, height - 56, 60, 20).build());
		addRenderableWidget(Button.builder(EditorText.REMOVE.get(), b -> removeEntry())
				.bounds(c - 15, height - 56, 60, 20).build());
		saveBtn = Button.builder(EditorText.SAVE.get(), b -> save())
				.bounds(c - 65, height - 30, 60, 20).build();
		saveBtn.active = session.dirty;
		addRenderableWidget(saveBtn);
		addRenderableWidget(Button.builder(EditorText.BACK.get(), b -> exitFile())
				.bounds(c + 5, height - 30, 60, 20).build());
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
					.append(EditorLang.STATS.get(stats))
					.append(Component.literal("  "))
					.append(EditorLang.MODIFIERS.get(mods))
					.append(Component.literal("]"))
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
			EditorToast.show(EditorText.ADD.get(), EditorText.NO_FILE.get());
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
					session.dirty = true;
					Minecraft.getInstance().setScreen(new MaterialEntryScreen(config, id, MaterialFileScreen.this, session));
				}, this));
	}

	private void editEntry() {
		ResourceLocation id = selected();
		if (id == null) {
			EditorToast.show(EditorText.EDIT.get(), EditorText.NO_FILE.get());
			return;
		}
		Minecraft.getInstance().setScreen(new MaterialEntryScreen(config, id, MaterialFileScreen.this, session));
	}

	private void removeEntry() {
		ResourceLocation id = selected();
		if (id == null) {
			EditorToast.show(EditorText.REMOVE.get(), EditorText.NO_FILE.get());
			return;
		}
		config.stats.remove(id);
		config.modifiers.remove(id);
		config.ingredients.remove(id);
		config.repairIngredients.remove(id);
		config.partLimitation.remove(id);
		session.dirty = true;
		rebuild();
	}

	private void save() {
		Minecraft.getInstance().setScreen(new PromptScreen(EditorText.SAVE.get(), EditorLang.FILE_ID.get(),
				fileId.toString(), EditorData::validateFileId, s -> {
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
			EditorData.save(ModularGolems.MATERIALS, fileId, config);
			EditorData.savedFlag = true;
			session.dirty = false;
			EditorToast.show(EditorText.SAVE.get(), EditorLang.SAVE_DONE.get(fileId));
			EditorToast.show(EditorText.SAVE.get(), EditorLang.SAVE_NOTE.get());
			return true;
		} catch (Exception e) {
			EditorToast.show(EditorLang.SAVE_FAIL.get(e.getMessage()), EditorLang.NOT_IN_WORLD.get());
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
		g.drawCenteredString(font, EditorLang.FILE.get(fileId), width / 2, 10, 0xFFFFFF);
	}

	@Override
	public void onClose() {
		exitFile();
	}

}
