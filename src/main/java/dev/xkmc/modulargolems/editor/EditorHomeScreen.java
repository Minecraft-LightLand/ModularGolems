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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EditorHomeScreen extends Screen {

	private boolean part;
	private EditorList list;
	private final List<ResourceLocation> order = new ArrayList<>();
	private Button matBtn, partBtn;

	public EditorHomeScreen() {
		super(EditorLang.TITLE.get());
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
		if (part) {
			for (var cfg : ModularGolems.PARTS.getAll()) {
				ResourceLocation id = cfg.getID();
				if (id == null) continue;
				order.add(id);
				entries.add(new EditorList.Entry(Component.literal(id.toString()).copy()
						.append(Component.literal("   (" + (cfg.filters.size() + cfg.magnifiers.size()) + ")"))
						, null, null));
			}
			if (entries.isEmpty()) {
				entries.add(new EditorList.Entry(EditorLang.NO_MATERIALS.get(), null, null));
			}
		} else {
			for (var cfg : ModularGolems.MATERIALS.getAll()) {
				ResourceLocation id = cfg.getID();
				if (id == null) continue;
				order.add(id);
				entries.add(new EditorList.Entry(Component.literal(id.toString()).copy()
						.append(Component.literal("   (" + cfg.getAllMaterials().size() + ")"))
						, null, null));
			}
			if (entries.isEmpty()) {
				entries.add(new EditorList.Entry(EditorLang.NO_MATERIALS.get(), null, null));
			}
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

	private void editFile() {
		ResourceLocation id = selected();
		if (id == null) {
			EditorToast.show(EditorLang.EDIT.get(), EditorLang.NO_FILE.get());
			return;
		}
		if (part) {
			GolemPartConfig cfg = ModularGolems.PARTS.getEntry(id);
			if (cfg == null) return;
			Minecraft.getInstance().setScreen(new PartFileScreen(EditorData.copy(ModularGolems.PARTS, cfg), id));
		} else {
			GolemMaterialConfig cfg = ModularGolems.MATERIALS.getEntry(id);
			if (cfg == null) return;
			Minecraft.getInstance().setScreen(new MaterialFileScreen(EditorData.copy(ModularGolems.MATERIALS, cfg), id));
		}
	}

	private void newFile() {
		String def = part ? "modulargolems:custom_parts" : "modulargolems:custom";
		Minecraft.getInstance().setScreen(new PromptScreen(EditorLang.NEW.get(), EditorLang.FILE_ID.get(), def, EditorData::validateFileId, s -> {
			ResourceLocation id = EditorData.parseId(s);
			if (id == null) return;
			if (part) {
				Minecraft.getInstance().setScreen(new PartFileScreen(new GolemPartConfig(), id));
			} else {
				Minecraft.getInstance().setScreen(new MaterialFileScreen(EditorData.newMaterial(), id));
			}
		}));
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, this.title, width / 2, 2, 0xFFFFFF);
	}

}
