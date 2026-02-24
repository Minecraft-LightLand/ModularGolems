package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.registry.TableGroup;
import dev.xkmc.modulargolems.content.menu.tabs.*;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class TableTab extends GolemTabBase<TableGroup, TableTab> {

	public static Level level = null;
	public static long time = 0;
	public static TableTabType lastOpened = null;

	public static GolemTabToken.TabFactory<TableGroup, TableTab> from(TableTabType type) {
		return (index, token, manager, stack, title) -> new TableTab(type, index, token, manager, stack, title);
	}

	public static void initScreen(TableTabType type, AbstractContainerScreen<?> screen, Consumer<AbstractWidget> cons) {
		ITabScreen tab = screen instanceof ITabScreen e ? e : new DelegateTabScreen(screen);
		new GolemTabManager<>(tab, new TableGroup(), GolemTabType.ABOVE, 2)
				.init(cons, GolemTabRegistry.LIST_TABLE.get(type.ordinal()));
	}

	private final TableTabType tab;

	public TableTab(TableTabType tab, int index, GolemTabToken<TableGroup, TableTab> token, GolemTabManager<TableGroup> manager, ItemStack stack, Component title) {
		super(index, token, manager, stack, title);
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
		ModularGolems.HANDLER.toServer(new OpenTableMenuToServer(tab));
	}

}
