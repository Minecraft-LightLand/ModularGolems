package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2menustacker.init.MouseCache;
import dev.xkmc.l2tabs.tabs.core.*;
import dev.xkmc.modulargolems.content.menu.registry.TableGroup;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class TableTab extends TabBase<TableGroup, TableTab> {

	public static Level level = null;
	public static long time = 0;
	public static TableTabType lastOpened = null;

	public static TabToken.TabFactory<TableGroup, TableTab> from(TableTabType type) {
		return (index, token, manager, title) -> new TableTab(type, index, token, manager, title);
	}

	public static void initScreen(TableTabType type, AbstractContainerScreen<?> screen, Consumer<AbstractWidget> cons) {
		ITabScreen tab = screen instanceof ITabScreen e ? e : new DelegateTabScreen(screen);
		new TabManager<>(tab, new TableGroup()).init(cons, type);
	}

	private final TableTabType tab;

	public TableTab(TableTabType tab, int index, TabToken<TableGroup, TableTab> token, TabManager<TableGroup> manager, Component title) {
		super(index, token, manager, title);
		this.tab = tab;
	}

	@Override
	public void onTabClicked() {
		level = Minecraft.getInstance().level;
		if (level == null) return;
		if (tab.ordinal() > 1) {
			lastOpened = tab;
			time = level.getGameTime();
		}
		MouseCache.cacheMousePos();
		ModularGolems.HANDLER.toServer(new OpenTableMenuToServer(tab));
	}

}
