package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.modulargolems.content.menu.tabs.GolemTabGroup;

import java.util.UUID;

public class EquipmentGroup extends GolemTabGroup<EquipmentGroup> {

	public UUID golem;

	public EquipmentGroup(UUID uuid) {
		super(GolemTabRegistry.LIST_EQUIPMENT);
		this.golem = uuid;
	}

}
