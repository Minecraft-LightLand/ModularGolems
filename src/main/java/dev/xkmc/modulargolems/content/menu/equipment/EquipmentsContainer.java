package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import dev.xkmc.mob_weapon_api.api.ai.ItemWrapper;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.world.item.ItemStack;

public class EquipmentsContainer extends BaseContainerMenu.BaseContainer<EquipmentsMenu> {

	public EquipmentsContainer(EquipmentsMenu menu) {
		super(0, menu);
	}

	private ItemWrapper getWrapper(int index) {
		if (parent.golem == null || index < 0) return ItemWrapper.EMPTY;
		if (index < 6) {
			return parent.golem.getWrapperOfHand(EquipmentsMenu.SLOTS[index]);
		}
		if (parent.golem instanceof SweepGolemEntity<?,?> e) {
			if (index == 6)
				return e.getBackupHand();
			if (index == 7)
				return e.getArrowSlot();
		}
		if (parent.golem instanceof MetalGolemEntity metal) {
			if (index == 8)
				return metal.getRightShoulder();
			if (index == 9)
				return metal.getLeftShoulder();
		}
		return ItemWrapper.EMPTY;
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
