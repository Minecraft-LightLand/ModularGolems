package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.l2tabs.tabs.core.TabGroup;
import dev.xkmc.l2tabs.tabs.core.TabGroupData;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.modulargolems.content.menu.table.TableTabType;

public class TableGroup extends TabGroupData<TableGroup> {

	private final boolean simple;

	public TableGroup(TabGroup<TableGroup> group) {
		this(group, false);
	}

	public TableGroup(TabGroup<TableGroup> group, boolean simple) {
		super(group);
		this.simple = simple;
	}

	@Override
	public <X extends TabGroupData<X>> boolean shouldHideTab(TabToken<X, ?> e) {
		if (simple) {
			if ((Object) e == TableTabType.DISINTEGRATE.getMapped()) return true;
		} else {
			if ((Object) e == GolemTabRegistry.TABLE_DISINTEGRATE_SIMPLE.get()) return true;
		}
		if (simple && (Object) e == TableTabType.ANVIL.getMapped()) return true;
		return super.shouldHideTab(e);
	}

	@Override
	public int split() {
		return 100;
	}

	@Override
	public boolean center() {
		return true;
	}

}
