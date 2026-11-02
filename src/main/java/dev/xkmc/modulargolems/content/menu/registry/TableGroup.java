package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.modulargolems.content.menu.tabs.GolemTabGroup;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;

import java.util.List;

public class TableGroup extends GolemTabGroup<TableGroup> {

	public TableGroup(List<GolemTabToken<TableGroup, ?>> list) {
		super(list);
	}

	public TableGroup() {
		super(GolemTabRegistry.LIST_TABLE);
	}

}