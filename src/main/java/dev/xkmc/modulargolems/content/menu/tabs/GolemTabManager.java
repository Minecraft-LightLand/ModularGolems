package dev.xkmc.modulargolems.content.menu.tabs;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GolemTabManager<G extends GolemTabGroup<G>> {

	protected final List<GolemTabBase<G, ?>> list = new ArrayList<>();

	public final ITabScreen screen;
	public final G token;

	public int tabPage;
	public GolemTabToken<G, ?> selected;

	public GolemTabManager(ITabScreen screen, G token) {
		this.screen = screen;
		this.token = token;
	}

	public void init(Consumer<AbstractWidget> adder, GolemTabToken<G, ?> selected) {
		List<GolemTabToken<G, ?>> token_list = token.getList();
		list.clear();
		this.selected = selected;
		int guiLeft = screen.getGuiLeft();
		int guiTop = screen.getGuiTop();
		int imgWidth = screen.getXSize();
		for (int i = 0; i < token_list.size(); i++) {
			GolemTabToken<G, ?> token = token_list.get(i);
			GolemTabBase<G, ?> tab = token.create(i, this);
			tab.setX(guiLeft + imgWidth + GolemTabType.RIGHT.getX(tab.index));
			tab.setY(guiTop + GolemTabType.RIGHT.getY(tab.index));
			adder.accept(tab);
			list.add(tab);
		}
		updateVisibility();
	}

	private void updateVisibility() {
		for (GolemTabBase<G, ?> tab : list) {
			tab.visible = tab.index >= tabPage * GolemTabType.MAX_TABS && tab.index < (tabPage + 1) * GolemTabType.MAX_TABS;
			tab.active = tab.visible;
		}
	}

	public Screen getScreen() {
		return screen.asScreen();
	}

	public void onToolTipRender(GuiGraphics stack, int mouseX, int mouseY) {
		for (GolemTabBase<G, ?> tab : list) {
			if (tab.visible && tab.isHoveredOrFocused()) {
				tab.onTooltip(stack, mouseX, mouseY);
			}
		}
	}

}
