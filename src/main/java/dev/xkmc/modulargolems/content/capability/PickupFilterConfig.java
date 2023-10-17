package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

@SerialClass
public class PickupFilterConfig {

	public static final int SIZE = 27;

	@SerialClass.SerialField
	protected final ArrayList<ItemStack> filter = new ArrayList<>();

	@SerialClass.SerialField
	protected boolean blacklist = true;

	@SerialClass.SerialField
	protected boolean matchNBT = false;

	public boolean internalMatch(ItemStack stack) {
		for (ItemStack filter : filter) {
			if (stack.getItem() == filter.getItem()) {
				if (matchNBT) {
					if (ItemStack.isSameItemSameTags(stack, filter)) {
						return true;
					}
				} else {
					return true;
				}
			}
		}
		return false;
	}

	public boolean allowPickup(ItemStack stack) {
		return internalMatch(stack) != blacklist;
	}

}
