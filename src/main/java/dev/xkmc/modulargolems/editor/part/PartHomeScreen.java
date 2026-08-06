package dev.xkmc.modulargolems.editor.part;

import dev.xkmc.modulargolems.content.config.GolemPartConfig;
import dev.xkmc.modulargolems.editor.base.EditorHomeScreen;
import dev.xkmc.modulargolems.editor.base.EditorSaveState;
import dev.xkmc.modulargolems.editor.base.EditorUtil;
import dev.xkmc.modulargolems.editor.material.MaterialHomeScreen;
import dev.xkmc.modulargolems.editor.util.EditorLang;
import dev.xkmc.modulargolems.editor.util.GolemEditorUtil;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PartHomeScreen extends EditorHomeScreen {

	private final MaterialHomeScreen materialHome;

	public PartHomeScreen(Screen parent, MaterialHomeScreen materialHome) {
		super(EditorLang.PARTS.get(), parent);
		this.materialHome = materialHome;
	}

	@Override
	protected List<ResourceLocation> listFiles() {
		List<ResourceLocation> ids = new ArrayList<>();
		for (var cfg : ModularGolems.PARTS.getAll()) {
			ResourceLocation id = cfg.getID();
			if (id != null) ids.add(id);
		}
		return ids;
	}

	@Override
	protected int fileCount(ResourceLocation id) {
		GolemPartConfig cfg = ModularGolems.PARTS.getEntry(id);
		return cfg == null ? 0 : cfg.filters.size() + cfg.magnifiers.size();
	}

	@Override
	protected Component emptyMessage() {
		return EditorLang.EMPTY_FILE.get();
	}

	@Override
	protected String newFileDefault() {
		return "modulargolems:custom_parts";
	}

	@Override
	protected void openNew(ResourceLocation id) {
		Minecraft.getInstance().setScreen(new PartFileScreen(new GolemPartConfig(), id, this));
	}

	@Override
	protected void openEdit(ResourceLocation id) {
		GolemPartConfig cfg = ModularGolems.PARTS.getEntry(id);
		if (cfg == null) return;
		Minecraft.getInstance().setScreen(new PartFileScreen(EditorUtil.copy(ModularGolems.PARTS, cfg), id, this));
	}

	@Override
	protected Component siblingLabel() {
		return EditorLang.MATERIALS.get();
	}

	@Override
	protected void openSibling() {
		Minecraft.getInstance().setScreen(materialHome);
	}

	@Override
	protected Component fileIdLabel() {
		return EditorLang.FILE_ID.get();
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
