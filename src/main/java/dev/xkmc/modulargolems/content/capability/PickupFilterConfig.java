package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

@SerialClass
public class PickupFilterConfig {

	public static final int SIZE = 27;

	@SerialField
	protected final ArrayList<ItemStack> filter = new ArrayList<>();

	@SerialField
	protected boolean blacklist = true;

	@SerialField
	protected boolean matchNBT = false;

	public boolean internalMatch(ItemStack stack) {
		for (ItemStack filter : filter) {
			if (stack.getItem() == filter.getItem()) {
				if (matchNBT) {
					if (ItemStack.isSameItemSameComponents(stack, filter)) {
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
