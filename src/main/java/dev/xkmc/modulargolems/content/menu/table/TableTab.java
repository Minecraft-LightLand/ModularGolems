package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.registry.TableGroup;
import dev.xkmc.modulargolems.content.menu.tabs.*;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TableTab extends GolemTabBase<TableGroup, TableTab> {

	public static Level level = null;
	public static long time = 0;
	public static TableTabType lastOpened = null;
	public static boolean lastOpenedSimple = false;

	private static boolean simpleActive = false;

	public static GolemTabToken.TabFactory<TableGroup, TableTab> from(TableTabType type) {
		return (index, token, manager, stack, title) -> new TableTab(type, false, index, token, manager, stack, title);
	}

	public static GolemTabToken.TabFactory<TableGroup, TableTab> fromSimple(TableTabType type) {
		return (index, token, manager, stack, title) -> new TableTab(type, true, index, token, manager, stack, title);
	}

	public static void initScreen(TableTabType type, AbstractContainerScreen<?> screen, Consumer<AbstractWidget> cons) {
		initScreen(type, screen, cons, screen.getMenu() instanceof SimpleAssembleMenu
				|| screen.getMenu() instanceof SimpleDisintegrateMenu
				|| screen.getMenu() instanceof SimpleUpgradeMenu);
	}

	public static void initScreen(TableTabType type, AbstractContainerScreen<?> screen, Consumer<AbstractWidget> cons, boolean simple) {
		simpleActive = simple;
		ITabScreen tab = screen instanceof ITabScreen e ? e : new DelegateTabScreen(screen);
		var topList = simple ? GolemTabRegistry.LIST_TABLE_TOP_SIMPLE : GolemTabRegistry.LIST_TABLE_TOP;
		GolemTabToken<TableGroup, ?> top = type.ordinal() < TableTabType.CRAFT.ordinal()
				? topList.get(type.ordinal()) : null;
		new GolemTabManager<>(tab, new TableGroup(topList), GolemTabType.ABOVE, 3)
				.init(cons, top);
		if (type == TableTabType.ANVIL && simple) return;
		GolemTabToken<TableGroup, ?> right;
		if (simple) {
			right = switch (type) {
				case CRAFT -> GolemTabRegistry.TABLE_CRAFT;
				case STONECUTTER -> GolemTabRegistry.TABLE_STONECUTTER;
				case SMITHING -> GolemTabRegistry.TABLE_SMITHING;
				case GRINDSTONE -> GolemTabRegistry.TABLE_GRINDSTONE;
				default -> null;
			};
		} else {
			right = type.ordinal() >= TableTabType.CRAFT.ordinal()
					? GolemTabRegistry.LIST_TABLE_RIGHT.get(type.ordinal() - TableTabType.CRAFT.ordinal()) : null;
		}
		var rightList = simple ? GolemTabRegistry.LIST_TABLE_SIMPLE_RIGHT : GolemTabRegistry.LIST_TABLE_RIGHT;
		new GolemTabManager<>(tab, new TableGroup(rightList), GolemTabType.RIGHT, -1)
				.init(cons, right);
	}

	private final TableTabType tab;
	private final boolean simple;

	public TableTab(TableTabType tab, boolean simple, int index, GolemTabToken<TableGroup, TableTab> token, GolemTabManager<TableGroup> manager, ItemStack stack, Component title) {
		super(index, token, manager, stack, title);
		this.tab = tab;
		this.simple = simple;
	}

	@Override
	public void onTabClicked() {
		level = Minecraft.getInstance().level;
		if (level == null) return;
		if (tab.ordinal() >= TableTabType.CRAFT.ordinal()) {
			lastOpened = tab;
			lastOpenedSimple = simpleActive;
			time = level.getGameTime();
		}
		ModularGolems.HANDLER.toServer(new OpenTableMenuToServer(tab, simpleActive));
	}

	@Override
	protected void renderIcon(GuiGraphics g) {
		if (tab == TableTabType.DISINTEGRATE && simple)
			manager.type.drawIcon(g, getX(), getY(), index, getRotatingIcon());
		else
			super.renderIcon(g);
	}

	private ItemStack getRotatingIcon() {
		var list = new ArrayList<GolemType<?, ?>>();
		for (var t : GolemTypes.TYPES.get())
			list.add(t);
		if (list.isEmpty()) return stack;
		var mc = Minecraft.getInstance();
		long time = mc.level == null ? 0 : mc.level.getGameTime();
		int index = (int) ((time / 20) % list.size());
		return GolemType.getGolemHolder(list.get(index)).getDefaultInstance();
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
