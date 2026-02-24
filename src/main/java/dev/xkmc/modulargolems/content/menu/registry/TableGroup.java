package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.l2tabs.tabs.core.TabGroupData;

public class TableGroup extends TabGroupData<TableGroup> {

	public TableGroup() {
		super(GolemTabRegistry.TABLE);
	}

	@Override
	public int split() {
		return 2;
	}

}
