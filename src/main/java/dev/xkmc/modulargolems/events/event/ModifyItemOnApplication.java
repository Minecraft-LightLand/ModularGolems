package dev.xkmc.modulargolems.events.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class ModifyItemOnApplication extends Event {

	private final ItemStack golem;
	private ItemStack stack;

	public ModifyItemOnApplication(ItemStack golem, ItemStack stack) {
		this.golem = golem;
		this.stack = stack;
	}

	public ItemStack getGolem() {
		return golem;
	}

	public ItemStack getStack() {
		return stack;
	}

	public void setStack(ItemStack stack) {
		this.stack = stack;
	}

}
