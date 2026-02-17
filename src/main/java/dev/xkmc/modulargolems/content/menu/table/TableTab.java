package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.modulargolems.content.menu.registry.TableGroup;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabBase;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class TableTab extends GolemTabBase<TableGroup, TableTab> {

	public static GolemTabToken.TabFactory<TableGroup, TableTab> from(TableTabType type) {
		return (index, token, manager, stack, title) -> new TableTab(type, index, token, manager, stack, title);
	}

	private final TableTabType tab;

	public TableTab(TableTabType tab, int index, GolemTabToken<TableGroup, TableTab> token, GolemTabManager<TableGroup> manager, ItemStack stack, Component title) {
		super(index, token, manager, stack, title);
		this.tab = tab;
	}

	@Override
	public void onTabClicked() {
		ModularGolems.HANDLER.toServer(new OpenTableMenuToServer(tab));
	}

}
