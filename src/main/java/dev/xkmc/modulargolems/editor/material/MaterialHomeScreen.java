package dev.xkmc.modulargolems.editor.material;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.editor.part.PartHomeScreen;
import dev.xkmc.modulargolems.editor.util.EditorData;
import dev.xkmc.modulargolems.editor.util.EditorHomeScreen;
import dev.xkmc.modulargolems.editor.util.EditorLang;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MaterialHomeScreen extends EditorHomeScreen {

	public MaterialHomeScreen(Screen parent) {
		super(EditorLang.MATERIALS.get(), parent);
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
		return EditorLang.NO_MATERIALS.get();
	}

	@Override
	protected String newFileDefault() {
		return "modulargolems:custom";
	}

	@Override
	protected void openNew(ResourceLocation id) {
		Minecraft.getInstance().setScreen(new MaterialFileScreen(EditorData.newMaterial(), id, this));
	}

	@Override
	protected void openEdit(ResourceLocation id) {
		GolemMaterialConfig cfg = ModularGolems.MATERIALS.getEntry(id);
		if (cfg == null) return;
		Minecraft.getInstance().setScreen(new MaterialFileScreen(EditorData.copy(ModularGolems.MATERIALS, cfg), id, this));
	}

	@Override
	protected Component siblingLabel() {
		return EditorLang.PARTS.get();
	}

	@Override
	protected void openSibling() {
		Minecraft.getInstance().setScreen(new PartHomeScreen(parent, this));
	}

}
