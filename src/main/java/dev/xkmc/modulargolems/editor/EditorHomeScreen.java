package dev.xkmc.modulargolems.editor;

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

public abstract class EditorHomeScreen extends Screen {

	protected final Screen parent;
	private EditorList list;

	protected EditorHomeScreen(Component title, Screen parent) {
		super(title);
		this.parent = parent;
	}

	@Override
	protected void init() {
		int c = width / 2;
		addRenderableWidget(Button.builder(siblingLabel(), b -> openSibling())
				.bounds(width - 100, 4, 90, 20).build());
		list = new EditorList(minecraft, width, height - 70, 34, height - 40);
		addRenderableWidget(list);
		addRenderableWidget(Button.builder(EditorLang.NEW.get(), b -> newFile())
				.bounds(c - 95, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.EDIT.get(), b -> editFile())
				.bounds(c - 30, height - 30, 60, 20).build());
		addRenderableWidget(Button.builder(EditorLang.BACK.get(), b -> onClose())
				.bounds(c + 35, height - 30, 60, 20).build());
		rebuild();
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(parent);
	}

	private void rebuild() {
		List<EditorList.Entry> entries = new ArrayList<>();
		List<ResourceLocation> ids = listFiles();
		if (ids.isEmpty()) {
			entries.add(new EditorList.Entry(emptyMessage(), null, null));
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
				entries.add(new EditorList.Entry(Component.literal(ns).copy()
						.append(Component.literal("   (" + fileCount(f) + ")"))
						, null, null, f));
			} else {
				for (ResourceLocation f : files) {
					entries.add(new EditorList.Entry(Component.literal(f.getPath()).copy()
							.append(Component.literal("   (" + fileCount(f) + ")"))
							, null, null, f));
				}
			}
		}
		list.setData(entries);
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
		Object data = sel.getData();
		return data instanceof ResourceLocation id ? id : null;
	}

	private void newFile() {
		Minecraft.getInstance().setScreen(new PromptScreen(EditorLang.NEW.get(), EditorLang.FILE_ID.get(),
				newFileDefault(), EditorData::validateFileId, s -> {
					ResourceLocation id = EditorData.parseId(s);
					if (id == null) return;
					openNew(id);
				}, this));
	}

	private void editFile() {
		ResourceLocation id = selected();
		if (id == null) {
			EditorToast.show(EditorLang.EDIT.get(), EditorLang.NO_FILE.get());
			return;
		}
		openEdit(id);
	}

	protected abstract List<ResourceLocation> listFiles();

	protected abstract int fileCount(ResourceLocation id);

	protected abstract Component emptyMessage();

	protected abstract String newFileDefault();

	protected abstract void openNew(ResourceLocation id);

	protected abstract void openEdit(ResourceLocation id);

	protected abstract Component siblingLabel();

	protected abstract void openSibling();

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		g.drawCenteredString(font, this.title, width / 2, 2, 0xFFFFFF);
	}

}
