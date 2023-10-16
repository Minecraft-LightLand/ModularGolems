package dev.xkmc.modulargolems.content.menu.ghost;

import net.minecraft.world.item.ItemStack;

public interface IGhostContainer {

	ItemStack getItem(int slot);

	boolean internalMatch(ItemStack stack);

	void set(int slot, ItemStack stack);

	int size();

}
