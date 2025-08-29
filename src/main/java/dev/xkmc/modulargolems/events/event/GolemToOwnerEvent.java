package dev.xkmc.modulargolems.events.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class GolemToOwnerEvent extends Event implements ICancellableEvent {

	private final LivingEntity owner;
	private final ItemStack stack;

	public GolemToOwnerEvent(LivingEntity owner, ItemStack stack) {
		this.owner = owner;
		this.stack = stack;
	}

	public LivingEntity getOwner() {
		return owner;
	}

	public ItemStack getStack() {
		return stack;
	}

}
