package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.modulargolems.content.menu.ghost.IGhostContainer;
import net.minecraft.world.item.ItemStack;

public record PickupFilterEditor(GolemConfigEditor editor) implements IGhostContainer {

	public PickupFilterConfig getFilter() {
		return editor().entry().pickupFilter;
	}

	public int listSize() {
		return getFilter().filter.size();
	}

	public void set(int slot, ItemStack stack) {
		if (slot < 0) slot = listSize();
		var filter = getFilter();
		if (slot >= filter.filter.size()) {
			if (!stack.isEmpty()) {
				filter.filter.add(stack);
			}
		} else {
			if (!stack.isEmpty()) {
				filter.filter.set(slot, stack);
			} else {
				filter.filter.remove(slot);
			}
		}
	}

	public boolean internalMatch(ItemStack stack) {
		return getFilter().internalMatch(stack);
	}

	@Override
	public int getContainerSize() {
		return PickupFilterConfig.SIZE;
	}

	public ItemStack getItem(int slot) {
		var filter = getFilter();
		return slot >= filter.filter.size() ? ItemStack.EMPTY : filter.filter.get(slot);
	}

	public boolean isBlacklist() {
		return getFilter().blacklist;
	}

	public boolean isTagMatch() {
		return getFilter().matchNBT;
	}

	public void toggleTag() {
		var filter = getFilter();
		filter.matchNBT = !filter.matchNBT;
		editor().sync();
	}

	public void toggleFilter() {
		var filter = getFilter();
		filter.blacklist = !filter.blacklist;
		editor().sync();
	}
}
