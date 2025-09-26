package dev.xkmc.modulargolems.content.entity.humanoid;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record SlotWrapper(Supplier<ItemStack> getter, Consumer<ItemStack> setter) implements IItemHandlerModifiable {

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		setter.accept(stack);
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return getter.get();
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		var old = getter.get();
		if (old.isEmpty()) {
			if (!simulate)
				setter.accept(stack.copy());
			return ItemStack.EMPTY;
		}
		if (ItemStack.isSameItemSameTags(old, stack)) {
			int insert = Math.min(stack.getMaxStackSize(), stack.getCount()) - old.getCount();
			if (insert <= 0) return stack;
			var ans = stack.copy();
			ans.shrink(insert);
			if (!simulate) {
				old.grow(insert);
				setter.accept(old);
			}
			return ans;
		}
		return stack;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		var stack = getter.get();
		var ans = stack.copy();
		ans.setCount(Math.min(stack.getCount(), amount));
		if (!simulate) {
			if (stack.getCount() <= amount)
				setter.accept(ItemStack.EMPTY);
			else {
				stack.shrink(amount);
				setter.accept(stack);
			}
		}
		return ans;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 0;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return true;
	}

}
