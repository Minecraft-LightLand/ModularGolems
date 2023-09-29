package dev.xkmc.modulargolems.content.capability;

import net.minecraft.world.item.ItemStack;

public record PickupFilterEditor(GolemConfigEditor editor) {

	public PickupFilterConfig getFilter() {
		return editor().entry().pickupFilter;
	}

	public int size() {
		return getFilter().filter.size();
	}

	public void set(int slot, ItemStack stack) {
		var filter = getFilter();
		if (slot > filter.filter.size()) {
			filter.filter.add(stack);
		} else {
			filter.filter.set(slot, stack);
		}
		editor().sync();
	}

	public boolean internalMatch(ItemStack stack) {
		return getFilter().internalMatch(stack);
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
