package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.l2tabs.tabs.core.TabGroupData;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;

public class EquipmentGroup extends TabGroupData<EquipmentGroup> {

	public AbstractGolemEntity<?, ?> golem;

	public EquipmentGroup(AbstractGolemEntity<?, ?> golem) {
		super(GolemTabRegistry.EQUIPMENTS);
		this.golem = golem;
	}

}
