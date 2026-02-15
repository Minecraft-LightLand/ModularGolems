package dev.xkmc.modulargolems.content.menu.table;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class UpgradeSlot extends SlotItemHandler {

	private final GolemUpgradeItemHandler handler;
	private final int index;

	public UpgradeSlot(GolemUpgradeItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		this.handler = itemHandler;
		this.index = index;
	}

	private ItemStack lastReturnedItem = ItemStack.EMPTY;

	@Override
	public @NotNull ItemStack getItem() {
		var newItem = super.getItem();
		if (!lastReturnedItem.equals(newItem))
			lastReturnedItem = newItem;
		return lastReturnedItem;
	}

	public void setChanged() {
		handler.setStackInSlot(index, lastReturnedItem);
	}

}
