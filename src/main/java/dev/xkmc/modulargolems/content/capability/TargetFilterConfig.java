package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

@SerialClass
public class TargetFilterConfig {

	@SerialClass.SerialField
	protected final ArrayList<ItemStack> hostileTo = new ArrayList<>();

	@SerialClass.SerialField
	protected final ArrayList<ItemStack> friendlyTo = new ArrayList<>();

	public boolean internalMatch(ArrayList<ItemStack> list, ItemStack stack) {
		for (ItemStack filter : list) {
			if (stack.getItem() == filter.getItem()) {
				if (ItemStack.isSameItemSameTags(stack, filter)) {
					return true;
				}
			}
		}
		return false;
	}

}
