package dev.xkmc.modulargolems.content.entity.common;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStackResourceHandler;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SlotWrapper extends ItemStackResourceHandler {

	private final Supplier<ItemStack> getter;
	private final Consumer<ItemStack> setter;

	public SlotWrapper(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
		super();
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected ItemStack getStack() {
		return getter.get();
	}

	@Override
	protected void setStack(ItemStack stack) {
		// We pass insideTransaction = true to disable all non-transactional actions.
		setter.accept(stack);
	}

	@Override
	protected boolean isValid(ItemResource resource) {
		return true;
	}

	@Override
	protected int getCapacity(ItemResource resource) {
		int slotLimit = 64;
		return resource.isEmpty() ? slotLimit : Math.min(slotLimit, resource.getMaxStackSize());
	}

	@Override
	protected void onRootCommit(ItemStack originalState) {
	}

	@Override
	public String toString() {
		return "entity equipment wrapper";
	}

}