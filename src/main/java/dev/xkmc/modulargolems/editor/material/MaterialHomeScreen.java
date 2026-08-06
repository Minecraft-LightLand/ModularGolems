package dev.xkmc.modulargolems.editor.material;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.editor.base.EditorHomeScreen;
import dev.xkmc.modulargolems.editor.base.EditorSaveState;
import dev.xkmc.modulargolems.editor.base.EditorTab;
import dev.xkmc.modulargolems.editor.base.EditorText;
import dev.xkmc.modulargolems.editor.base.EditorUtil;
import dev.xkmc.modulargolems.editor.part.PartHomeScreen;
import dev.xkmc.modulargolems.editor.util.GolemEditorLang;
import dev.xkmc.modulargolems.editor.util.GolemEditorUtil;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MaterialHomeScreen extends EditorHomeScreen {

	public MaterialHomeScreen(Screen parent) {
		super(GolemEditorLang.MATERIALS.get(), parent);
	}

	@Override
	protected List<ResourceLocation> listFiles() {
		List<ResourceLocation> ids = new ArrayList<>();
		for (var cfg : ModularGolems.MATERIALS.getAll()) {
			ResourceLocation id = cfg.getID();
			if (id != null) ids.add(id);
		}
		return ids;
	}

	@Override
	protected int fileCount(ResourceLocation id) {
		GolemMaterialConfig cfg = ModularGolems.MATERIALS.getEntry(id);
		return cfg == null ? 0 : cfg.getAllMaterials().size();
	}

	@Override
	protected Component emptyMessage() {
		return GolemEditorLang.NO_MATERIALS.get();
	}

	@Override
	protected String newFileDefault() {
		return "modulargolems:custom";
	}

	@Override
	protected void openNew(ResourceLocation id) {
		Minecraft.getInstance().setScreen(new MaterialFileScreen(GolemEditorUtil.newMaterial(), id, this));
	}

	@Override
	protected void openEdit(ResourceLocation id) {
		GolemMaterialConfig cfg = ModularGolems.MATERIALS.getEntry(id);
		if (cfg == null) return;
		GolemMaterialConfig copy = EditorUtil.copy(ModularGolems.MATERIALS, cfg);
		if (copy == null) return;
		Minecraft.getInstance().setScreen(new MaterialFileScreen(copy, id, this));
	}

	@Override
	protected List<EditorTab> tabs() {
		return List.of(
				new EditorTab(GolemEditorLang.MATERIALS.get(), () -> {
				}),
				new EditorTab(GolemEditorLang.PARTS.get(),
						() -> Minecraft.getInstance().setScreen(new PartHomeScreen(parent, this))));
	}

	@Override
	protected int activeTab() {
		return 0;
	}

	@Override
	protected Component fileIdLabel() {
		return EditorText.FILE_ID.get();
	}

	@Override
	protected Function<String, Component> validateId() {
		return GolemEditorUtil::validateFileId;
	}

	@Override
	protected boolean hasPendingReload() {
		return EditorSaveState.savedFlag;
	}

	@Override
	protected void setReloaded() {
		EditorSaveState.savedFlag = false;
	}

}
