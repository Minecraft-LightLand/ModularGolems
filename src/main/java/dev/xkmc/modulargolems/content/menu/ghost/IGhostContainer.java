package dev.xkmc.modulargolems.content.menu.ghost;

import net.minecraft.world.item.ItemStack;

public interface IGhostContainer extends ReadOnlyContainer {

	ItemStack getItem(int slot);

	boolean internalMatch(ItemStack stack);

	void set(int slot, ItemStack stack);

	int listSize();

	int getContainerSize();

	@Override
	default boolean isEmpty() {
		return listSize() == 0;
	}

}
