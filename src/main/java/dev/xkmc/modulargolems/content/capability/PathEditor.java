package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.modulargolems.content.item.card.PathRecordCard;
import dev.xkmc.modulargolems.content.menu.ghost.IGhostContainer;
import net.minecraft.world.item.ItemStack;

public record PathEditor(GolemConfigEditor editor) implements IGhostContainer {

	public PathConfig getFilter() {
		return editor().entry().pathConfig;
	}

	public int listSize() {
		return getFilter().path.size();
	}

	public void set(int slot, ItemStack stack) {
		if (!stack.isEmpty() && !(stack.getItem() instanceof PathRecordCard)) return;
		stack = stack.copy();
		if (slot < 0) slot = listSize();
		var filter = getFilter();
		if (slot >= filter.path.size()) {
			if (!stack.isEmpty()) {
				filter.path.add(stack);
			}
		} else {
			if (!stack.isEmpty()) {
				filter.path.set(slot, stack);
			} else {
				filter.path.remove(slot);
			}
		}
	}

	public boolean internalMatch(ItemStack stack) {
		return false;
	}

	@Override
	public int getContainerSize() {
		return PickupFilterConfig.SIZE;
	}

	public ItemStack getItem(int slot) {
		var filter = getFilter();
		return slot >= filter.path.size() ? ItemStack.EMPTY : filter.path.get(slot);
	}

}
