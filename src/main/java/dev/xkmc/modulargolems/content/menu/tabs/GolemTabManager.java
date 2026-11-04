package dev.xkmc.modulargolems.content.menu.tabs;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GolemTabManager<G extends GolemTabGroup<G>> {

	/**
	 * Screen rectangles covered by laid out tabs, so JEI can be told to keep its item list away from
	 * them. Keyed by screen, then by tab side, because one screen may own several managers.
	 */
	private static final Map<Screen, Map<GolemTabType, List<Rect2i>>> EXTRA_AREAS = new WeakHashMap<>();

	protected final List<GolemTabBase<G, ?>> list = new ArrayList<>();

	public final ITabScreen screen;
	public final G token;
	public final GolemTabType type;
	private final int split;

	public int tabPage;
	public GolemTabToken<G, ?> selected;

	/**
	 * Areas occupied by the tabs of the given screen, empty when it has none.
	 */
	public static List<Rect2i> getExtraAreas(Screen screen) {
		Map<GolemTabType, List<Rect2i>> perType = EXTRA_AREAS.get(screen);
		if (perType == null) return List.of();
		List<Rect2i> ans = new ArrayList<>();
		perType.values().forEach(ans::addAll);
		return ans;
	}

	public GolemTabManager(ITabScreen screen, G token, GolemTabType type, int split) {
		this.screen = screen;
		this.token = token;
		this.type = type;
		this.split = split;
	}

	public GolemTabManager(ITabScreen screen, G token) {
		this(screen, token, GolemTabType.RIGHT, -1);
	}

	public void init(Consumer<AbstractWidget> adder, GolemTabToken<G, ?> selected) {
		List<GolemTabToken<G, ?>> token_list = token.getList();
		list.clear();
		this.selected = selected;
		int guiLeft = screen.getGuiLeft();
		int guiTop = screen.getGuiTop();
		int w = screen.getXSize();
		int h = screen.getYSize();
		for (int i = 0; i < token_list.size(); i++) {
			GolemTabToken<G, ?> token = token_list.get(i);
			GolemTabBase<G, ?> tab = token.create(i, this);
			tab.setX(guiLeft + type.getX(w, h, tab.index, split, token_list.size()));
			tab.setY(guiTop + type.getY(w, h, tab.index, split));
			tab.init(guiLeft, guiTop);
			adder.accept(tab);
			list.add(tab);
		}
		updateVisibility();
		EXTRA_AREAS.computeIfAbsent(screen.asScreen(), e -> new EnumMap<>(GolemTabType.class))
				.put(type, list.stream().filter(e -> e.visible)
						.map(e -> new Rect2i(e.getX(), e.getY(), type.width, type.height))
						.collect(Collectors.toList()));
	}

	private void updateVisibility() {
		for (GolemTabBase<G, ?> tab : list) {
			tab.visible = tab.index >= tabPage * type.max && tab.index < (tabPage + 1) * type.max;
			tab.active = tab.visible;
		}
	}

	public Screen getScreen() {
		return screen.asScreen();
	}

	public void onToolTipRender(GuiGraphics stack, int mouseX, int mouseY) {
		for (GolemTabBase<G, ?> tab : list) {
			// hovered only, otherwise a clicked tab keeps its focus and the tooltip sticks around
			if (tab.visible && tab.isHovered()) {
				tab.onTooltip(stack, mouseX, mouseY);
			}
		}
	}

}
