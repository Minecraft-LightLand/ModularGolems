package dev.xkmc.modulargolems.content.menu;

import dev.xkmc.l2library.base.menu.BaseContainerMenu;
import net.minecraft.world.item.ItemStack;

public class EquipmentsContainer extends BaseContainerMenu.BaseContainer<EquipmentsMenu> {

	public EquipmentsContainer(EquipmentsMenu menu) {
		super(0, menu);
	}

	@Override
	public ItemStack getItem(int index) {
		if (parent.golem == null) return ItemStack.EMPTY;
		return parent.golem.getItemBySlot(EquipmentsMenu.SLOTS[index]);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (parent.golem == null) return;
		parent.golem.setItemSlot(EquipmentsMenu.SLOTS[index], stack);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		if (parent.golem == null) return ItemStack.EMPTY;
		return parent.golem.getItemBySlot(EquipmentsMenu.SLOTS[index]).split(count);
	}

}
