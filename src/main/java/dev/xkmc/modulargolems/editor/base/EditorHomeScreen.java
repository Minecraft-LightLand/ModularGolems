package dev.xkmc.modulargolems.editor.base;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

public abstract class EditorHomeScreen extends EditorScreen {

	protected final Screen parent;
	private EditorList list;
	@Nullable
	private Button reloadBtn;
	private Button editBtn;
	private EditBox search;
	private final Set<String> collapsed = new HashSet<>();

	protected EditorHomeScreen(Component title, Screen parent) {
		super(title);
		this.parent = parent;
	}

	/**
	 * Extra buttons placed before New/Edit on the bottom row.
	 */
	protected List<Button> extraButtons() {
		return new ArrayList<>();
	}

	/**
	 * Whether this tab shows a search box filtering the file list.
	 */
	protected boolean hasSearch() {
		return false;
	}

	/**
	 * Whether this tab shows a Reload button for pending datapack changes.
	 */
	protected boolean hasReload() {
		return true;
	}

	/**
	 * Whether this tab shows a New button.
	 */
	protected boolean hasNew() {
		return true;
	}

	@Override
	protected void init() {
		int listTop = 34;
		if (hasSearch()) {
			listTop = 58;
		}
		list = new EditorList(minecraft, width, height - 40 - listTop, listTop, height - 40);
		//list.setRenderTopAndBottom(false);
		addRenderableWidget(list);
		if (hasSearch()) {
			search = new EditBox(font, width / 2 - 100, 36, 200, 18, EditorText.SEARCH.get());
			search.setMaxLength(64);
			search.setResponder(s -> rebuild());
			addRenderableWidget(search);
			setInitialFocus(search);
		}
		initTabs();
		List<Button> row = new ArrayList<>(extraButtons());
		if (hasNew()) {
			Button newBtn = Button.builder(EditorText.NEW.get(), b -> newFile()).bounds(0, 0, 60, 20).build();
			newBtn.active = canCreate();
			row.add(newBtn);
		}
		editBtn = Button.builder(EditorText.EDIT.get(), b -> editFile()).bounds(0, 0, 60, 20).build();
		row.add(editBtn);
		if (hasReload()) {
			reloadBtn = Button.builder(EditorText.RELOAD.get(), b -> reloadNow(false)).bounds(0, 0, 60, 20).build();
			row.add(reloadBtn);
		}
		row.add(Button.builder(EditorText.BACK.get(), b -> exit()).bounds(0, 0, 60, 20).build());
		row.forEach(this::addRenderableWidget);
		EditorLayout.centerRow(row, width / 2, height - 30, 5);
		if (reloadBtn != null) reloadBtn.active = hasPendingReload();
		editBtn.active = false;
		list.setOnSelect(() -> editBtn.active = selected() != null);
		list.setOnDoubleClick(this::editFile);
		rebuild();
	}

	private void initTabs() {
		List<EditorTab> tabs = tabs();
		int active = activeTab();
		int gap = 6;
		int h = 24;
		int total = 0;
		List<Integer> widths = new ArrayList<>();
		for (EditorTab t : tabs) {
			int w = Math.max(60, font.width(t.label()) + 24);
			widths.add(w);
			total += w;
		}
		total += gap * Math.max(0, tabs.size() - 1);
		int x = (width - total) / 2;
		for (int i = 0; i < tabs.size(); i++) {
			int idx = i;
			EditorTab tab = tabs.get(i);
			TabButton btn = new TabButton(x, 10, widths.get(i), h, tab.label(), i == active,
					b -> openTab(idx));
			if (tab.tooltip() != null) btn.setTooltip(Tooltip.create(tab.tooltip()));
			addRenderableWidget(btn);
			x += widths.get(i) + gap;
		}
	}

	private void openTab(int idx) {
		if (idx == activeTab()) return;
		List<EditorTab> tabs = tabs();
		if (idx < 0 || idx >= tabs.size()) return;
		tabs.get(idx).onSelect().run();
	}

	@Override
	public void onClose() {
		exit();
	}

	private void exit() {
		if (hasPendingReload()) {
			Minecraft.getInstance().setScreen(new ReloadConfirmScreen(this, () -> reloadNow(true), () ->
					Minecraft.getInstance().setScreen(parent)));
		} else {
			Minecraft.getInstance().setScreen(parent);
		}
	}

	private void reloadNow(boolean exit) {
		setReloaded();
		IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
		if (server != null) {
			server.execute(() -> server.reloadResources(server.getPackRepository().getSelectedIds()));
			EditorToast.show(EditorText.RELOAD.get(), EditorText.RELOAD_DONE.get());
		}
		Minecraft.getInstance().setScreen(exit ? parent : this);
	}

	/**
	 * Whether a file row is drawn disabled (light gray). Overridden by subclasses.
	 */
	protected boolean isDisabled(ResourceLocation id) {
		return false;
	}

	/**
	 * Tooltip shown when hovering a file row, or null for none. Overridden by subclasses.
	 */
	@Nullable
	protected Component fileTooltip(ResourceLocation id) {
		return null;
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
		String query = searchText().toLowerCase(Locale.ROOT);
		for (var ent : groups.entrySet()) {
			String ns = ent.getKey();
			List<ResourceLocation> files = ent.getValue();
			files.sort(ResourceLocation::compareTo);
			if (!query.isEmpty()) {
				files.removeIf(f -> !(ns + " " + f.getPath() + " " + fileLabel(f).getString())
						.toLowerCase(Locale.ROOT).contains(query));
			}
			boolean isCollapsed = collapsed.contains(ns);
			boolean matches = !query.isEmpty() && (ns + " " + groupName(ns)).toLowerCase(Locale.ROOT).contains(query);
			boolean showFiles = !isCollapsed || matches;
			if (files.isEmpty() && !matches) continue;
			entries.add(new EditorList.Entry(Component.literal(groupName(ns)), true,
					() -> toggleCollapsed(ns), isCollapsed));
			if (showFiles) {
				for (ResourceLocation f : files) {
					Component label = fileLabel(f).copy().append(rowSuffix(f));
					boolean disabled = isDisabled(f);
					if (disabled) {
						label = label.copy().withStyle(ChatFormatting.RED, ChatFormatting.STRIKETHROUGH);
					}
					Component tip = fileTooltip(f);
					entries.add(tip == null
							? new EditorList.Entry(label, null, null, f, disabled)
							: new EditorList.Entry(label, null, null, f, disabled, tip));
				}
			}
		}
		list.setData(entries);
	}

	private void toggleCollapsed(String ns) {
		if (!collapsed.add(ns)) {
			collapsed.remove(ns);
		}
		rebuild();
	}

	private String searchText() {
		return search == null ? "" : search.getValue();
	}

	/**
	 * Display name of a group header in the file list. Defaults to the mod display name of the
	 * namespace; subclasses can override to show translated categories.
	 */
	protected String groupName(String ns) {
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
		Minecraft.getInstance().setScreen(new PromptScreen(EditorText.NEW.get(), fileIdLabel(),
				newFileDefault(), validateId(), s -> {
			ResourceLocation id = EditorFile.parseId(s);
			if (id == null) return;
			openNew(id);
		}, this));
	}

	private void editFile() {
		ResourceLocation id = selected();
		if (id == null) return;
		openEdit(id);
	}

	protected abstract List<ResourceLocation> listFiles();

	protected Component fileLabel(ResourceLocation id) {
		return Component.literal(id.getPath());
	}

	/**
	 * Text appended to a file row label. Defaults to the entry count; subclasses can replace it.
	 */
	protected Component rowSuffix(ResourceLocation id) {
		return Component.literal("   (" + fileCount(id) + ")");
	}

	protected boolean canCreate() {
		return true;
	}

	protected abstract int fileCount(ResourceLocation id);

	protected abstract Component emptyMessage();

	protected abstract String newFileDefault();

	protected abstract void openNew(ResourceLocation id);

	protected abstract void openEdit(ResourceLocation id);

	protected abstract List<EditorTab> tabs();

	protected abstract int activeTab();

	protected abstract Component fileIdLabel();

	protected abstract Function<String, Component> validateId();

	protected abstract boolean hasPendingReload();

	protected abstract void setReloaded();

	@Override
	public void render(GuiGraphics g, int mx, int my, float pTick) {
		super.renderBackground(g);
		super.render(g, mx, my, pTick);
		if (list != null) list.renderRowTooltip(g);
	}

}
