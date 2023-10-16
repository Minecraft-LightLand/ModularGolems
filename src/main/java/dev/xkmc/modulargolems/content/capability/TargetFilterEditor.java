package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.modulargolems.content.menu.ghost.IGhostContainer;
import dev.xkmc.modulargolems.content.menu.ghost.ReadOnlyContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public record TargetFilterEditor(GolemConfigEditor editor, ArrayList<ItemStack> list) implements IGhostContainer, ReadOnlyContainer {

	public TargetFilterConfig getFilter() {
		return editor().entry().targetFilter;
	}

	public int size() {
		return list.size();
	}

	public void set(int slot, ItemStack stack) {
		if (slot >= list.size()) {
			list.add(stack);
		} else {
			list.set(slot, stack);
		}
		editor().sync();
	}

	public boolean internalMatch(ItemStack stack) {
		return getFilter().internalMatch(list, stack);
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
		return slot >= list.size() ? ItemStack.EMPTY : list.get(slot);
	}

}
