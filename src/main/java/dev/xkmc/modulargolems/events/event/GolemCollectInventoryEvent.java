package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.Container;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import java.util.ArrayList;

public class GolemCollectInventoryEvent extends GolemEvent {

	private final ArrayList<IItemHandlerModifiable> list;

	public GolemCollectInventoryEvent(AbstractGolemEntity<?, ?> golem, ArrayList<IItemHandlerModifiable> list) {
		super(golem);
		this.list = list;
	}

	public void add(IItemHandlerModifiable inv) {
		list.add(inv);
	}

	public void add(Container inv) {
		list.add(new InvWrapper(inv));
	}

}
