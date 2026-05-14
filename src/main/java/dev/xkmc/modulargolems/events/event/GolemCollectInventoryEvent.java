package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.Container;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

import java.util.List;

public class GolemCollectInventoryEvent extends GolemEvent {

	private final List<ResourceHandler<ItemResource>> list;

	public GolemCollectInventoryEvent(AbstractGolemEntity<?, ?> golem, List<ResourceHandler<ItemResource>> list) {
		super(golem);
		this.list = list;
	}

	public void add(ResourceHandler<ItemResource> inv) {
		list.add(inv);
	}

	public void add(Container inv) {
		list.add(VanillaContainerWrapper.of(inv));
	}

}
