package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.item.ItemStack;

public class EquipmentsContainer extends BaseContainerMenu.BaseContainer<EquipmentsMenu> {

	public EquipmentsContainer(EquipmentsMenu menu) {
		super(0, menu);
	}

	private ItemWrapper getWrapper(int index) {
		return parent.golem == null ? ItemWrapper.EMPTY : parent.getWrapperAt(index);
	}

	@Override
	public ItemStack getItem(int index) {
		return getWrapper(index).getItem();
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		getWrapper(index).setItem(stack);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return getWrapper(index).getItem().split(count);
	}

	@Override
	public void setChanged() {
		if (parent.golem instanceof HumanoidGolemEntity humanoid) {
			humanoid.triggerReassess();
		}
	}
}
