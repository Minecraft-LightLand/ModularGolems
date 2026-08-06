package dev.xkmc.modulargolems.editor;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.config.GolemPartConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

	public class EditorHomeScreen extends Screen {

	private final Screen parent;
	private boolean part;
	private EditorList list;
	private final List<ResourceLocation> order = new ArrayList<>();
	private Button matBtn, partBtn;

	public EditorHomeScreen(Screen parent) {
		super(EditorLang.TITLE.get());
		this.parent = parent;
	}

	@Override
	protected void init() {
		int c = width / 2;
		matBtn = Button.builder(EditorLang.MATERIALS.get(), b -> setMode(false))
				.bounds(c - 100, 8, 95, 20).build();
		partBtn = Button.builder(EditorLang.PARTS.get(), b -> setMode(true))
				.bounds(c + 5, 8, 95, 20).build();
		addRenderableWidget(matBtn);
		addRenderableWidget(partBtn);
		list = new EditorList(minecraft, width, height - 70, 34, height - 40);
		addRenderableWidget(list);
		addRenderableWidget(Button.builder(EditorLang.NEW.get(), b -> newFile())
				.bounds(c - 130, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.EDIT.get(), b -> editFile())
				.bounds(c - 65, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.BACK.get(), b -> onClose())
				.bounds(c + 5, height - 30, 60, 20).build());
		refreshMode();
		rebuild();
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(parent);
	}

	private void setMode(boolean p) {
		if (part != p) {
			part = p;
			refreshMode();
			rebuild();
		}
	}

	private void refreshMode() {
		matBtn.active = !part;
		partBtn.active = part;
	}

	private void rebuild() {
		order.clear();
		List<EditorList.Entry> entries = new ArrayList<>();
		List<ResourceLocation> ids = new ArrayList<>();
		if (part) {
			for (var cfg : ModularGolems.PARTS.getAll()) {
				ResourceLocation id = cfg.getID();
				if (id != null) ids.add(id);
			}
		} else {
			for (var cfg : ModularGolems.MATERIALS.getAll()) {
				ResourceLocation id = cfg.getID();
				if (id != null) ids.add(id);
			}
		}
		if (ids.isEmpty()) {
			entries.add(new EditorList.Entry(EditorLang.NO_MATERIALS.get(), null, null));
			list.setData(entries);
			return;
		}
		Map<String, List<ResourceLocation>> groups = new TreeMap<>();
		for (ResourceLocation id : ids) {
			groups.computeIfAbsent(id.getNamespace(), k -> new ArrayList<>()).add(id);
		}
		for (var ent : groups.entrySet()) {
			String ns = ent.getKey();
			List<ResourceLocation> files = ent.getValue();
			files.sort(ResourceLocation::compareTo);
			entries.add(new EditorList.Entry(Component.literal(modName(ns)), true));
			if (files.size() == 1) {
				ResourceLocation f = files.get(0);
				order.add(f);
				entries.add(new EditorList.Entry(Component.literal(ns).copy()
						.append(Component.literal("   (" + fileCount(f) + ")"))
						, null, null));
			} else {
				for (ResourceLocation f : files) {
					order.add(f);
					entries.add(new EditorList.Entry(Component.literal(f.getPath()).copy()
							.append(Component.literal("   (" + fileCount(f) + ")"))
							, null, null));
				}
			}
		}
		list.setData(entries);
	}

	private int fileCount(ResourceLocation id) {
		if (part) {
			var cfg = ModularGolems.PARTS.getEntry(id);
			return cfg == null ? 0 : cfg.filters.size() + cfg.magnifiers.size();
		}
		var cfg = ModularGolems.MATERIALS.getEntry(id);
		return cfg == null ? 0 : cfg.getAllMaterials().size();
	}

	private static String modName(String ns) {
		var opt = ModList.get().getModContainerById(ns);
		if (opt.isPresent()) {
			return opt.get().getModInfo().getDisplayName();
		}
		return ns;
	}

	@Nullable
	private ResourceLocation selected() {
		EditorList.Entry sel = list.getSelected();
		if (sel == null) return null;
		int i = list.children().indexOf(sel);
		if (i < 0 || i >= order.size()) return null;
		return order.get(i);
	}

	private void editFile() {
		ResourceLocation id = selected();
		if (id == null) {
			EditorToast.show(EditorLang.EDIT.get(), EditorLang.NO_FILE.get());
			return;
		}
		if (part) {
			GolemPartConfig cfg = ModularGolems.PARTS.getEntry(id);
			if (cfg == null) return;
			Minecraft.getInstance().setScreen(new PartFileScreen(EditorData.copy(ModularGolems.PARTS, cfg), id, this));
		} else {
			GolemMaterialConfig cfg = ModularGolems.MATERIALS.getEntry(id);
			if (cfg == null) return;
			Minecraft.getInstance().setScreen(new MaterialFileScreen(EditorData.copy(ModularGolems.MATERIALS, cfg), id, this));
		}
	}

	private void newFile() {
		String def = part ? "modulargolems:custom_parts" : "modulargolems:custom";
		Minecraft.getInstance().setScreen(new PromptScreen(EditorLang.NEW.get(), EditorLang.FILE_ID.get(), def, EditorData::validateFileId, s -> {
			ResourceLocation id = EditorData.parseId(s);
			if (id == null) return;
			if (part) {
				Minecraft.getInstance().setScreen(new PartFileScreen(new GolemPartConfig(), id, this));
			} else {
				Minecraft.getInstance().setScreen(new MaterialFileScreen(EditorData.newMaterial(), id, this));
			}
		}, this));
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, this.title, width / 2, 2, 0xFFFFFF);
	}

}
