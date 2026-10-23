package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.modulargolems.content.menu.tabs.GolemTabGroup;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;

import java.util.List;

/**
 * Golem type switcher shown on the left side of the assemble screen.
 */
public class GolemTypeGroup extends GolemTabGroup<GolemTypeGroup> {

	public GolemTypeGroup(List<GolemTabToken<GolemTypeGroup, ?>> list) {
		super(list);
	}

}
