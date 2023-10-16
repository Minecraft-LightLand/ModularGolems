package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.modulargolems.content.menu.ghost.IGhostContainer;
import dev.xkmc.modulargolems.content.menu.ghost.ReadOnlyContainer;
import net.minecraft.world.item.ItemStack;

public record PickupFilterEditor(GolemConfigEditor editor) implements IGhostContainer, ReadOnlyContainer {

	public PickupFilterConfig getFilter() {
		return editor().entry().pickupFilter;
	}

	public int size() {
		return getFilter().filter.size();
	}

	public void set(int slot, ItemStack stack) {
		var filter = getFilter();
		if (slot >= filter.filter.size()) {
			filter.filter.add(stack);
		} else {
			filter.filter.set(slot, stack);
		}
		editor().sync();
	}

	public boolean internalMatch(ItemStack stack) {
		return getFilter().internalMatch(stack);
	}

	@Override
	public int getContainerSize() {
		return size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
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
