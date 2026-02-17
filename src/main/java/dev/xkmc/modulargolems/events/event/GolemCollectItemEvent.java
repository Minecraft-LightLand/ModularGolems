package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Items golem will return on disintegration
 */
public class GolemCollectItemEvent extends GolemEvent {

	private final List<ItemStack> list;

	public GolemCollectItemEvent(AbstractGolemEntity<?, ?> golem, List<ItemStack> list) {
		super(golem);
		this.list = list;
	}

	public List<ItemStack> getAll() {
		return list;
	}

	public void add(ItemStack stack) {
		if (stack.isEmpty()) return;
		list.add(stack);
	}

}
