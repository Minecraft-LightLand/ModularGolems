package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.modulargolems.content.item.card.TargetFilterCard;
import dev.xkmc.modulargolems.content.menu.ghost.IGhostContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public record TargetFilterLine(GolemConfigEditor editor, ArrayList<ItemStack> list, int offset)
		implements IGhostContainer {

	public TargetFilterConfig getFilter() {
		return editor().entry().targetFilter;
	}

	public int listSize() {
		return list.size();
	}

	public int getContainerSize() {
		return TargetFilterConfig.LINE;
	}

	public void set(int slot, ItemStack stack) {
		if (!(stack.getItem() instanceof TargetFilterCard)) return;
		slot -= offset;
		if (slot >= list.size()) {
			list.add(stack);
		} else {
			list.set(slot, stack);
		}
	}

	public boolean internalMatch(ItemStack stack) {
		return getFilter().internalMatch(list, stack);
	}

	public ItemStack getItem(int slot) {
		slot -= offset;
		return slot >= list.size() ? ItemStack.EMPTY : list.get(slot);
	}

}
