package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2menustacker.init.MouseCache;
import dev.xkmc.l2tabs.tabs.core.*;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.registry.TableGroup;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;

import java.util.ArrayList;

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
		boolean simple = screen.getMenu() instanceof SimpleAssembleMenu || screen.getMenu() instanceof SimpleDisintegrateMenu || screen.getMenu() instanceof SimpleUpgradeMenu;
		initGroup(tab, GolemTabRegistry.TABLE_TOP, type, cons, simple);
		initGroup(tab, GolemTabRegistry.TABLE_RIGHT, type, cons, simple);
	}

	private static void initGroup(ITabScreen tab, TabGroup<TableGroup> group, TableTabType type, Consumer<AbstractWidget> cons, boolean simple) {
		TableGroup data = new TableGroup(group, simple);
		var tabs = group.getTabs(data);
		if (tabs.isEmpty()) return;
		TabToken<TableGroup, ?> mapped = type.getMapped();
		if (simple && type == TableTabType.DISINTEGRATE) mapped = GolemTabRegistry.TABLE_DISINTEGRATE_SIMPLE.get();
		TabToken<TableGroup, ?> sel = mapped != null && tabs.contains(mapped) ? mapped : null;
		new TabManager<>(tab, data).init(cons, sel);
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
		if (tab.ordinal() > 2) {
			lastOpened = tab;
			time = level.getGameTime();
		}
		MouseCache.cacheMousePos();
		ModularGolems.HANDLER.toServer(new OpenTableMenuToServer(tab));
	}

	@Override
	protected void renderIcon(GuiGraphics g) {
		if (tab == TableTabType.DISINTEGRATE && token == GolemTabRegistry.TABLE_DISINTEGRATE_SIMPLE.get()) {
			token.getType().drawIcon(g, getX(), getY(), getRotatingIcon());
		} else {
			super.renderIcon(g);
		}
	}

	private ItemStack getRotatingIcon() {
		var list = new ArrayList<GolemType<?, ?>>();
		for (var t : GolemTypes.TYPES.get().stream().toList())
			list.add(t);
		if (list.isEmpty()) return stack();
		var mc = Minecraft.getInstance();
		long time = mc.level == null ? 0 : mc.level.getGameTime();
		int index = (int) ((time / 20) % list.size());
		return GolemType.getGolemHolder(list.get(index)).getDefaultInstance();
	}

	private ItemStack stack() {
		return ItemStack.EMPTY;
	}

	public void reinitIfMatched(Screen screen) {
		level = Minecraft.getInstance().level;
		if (level == null) return;
		if (screen instanceof AbstractContainerScreen<?> acs) {
			if (acs.getMenu().getType() == tab.menu) {
				lastOpened = tab;
				time = level.getGameTime();
			}
		}
	}

}
