package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class GolemItemUseEvent extends GolemEvent {

	private final ItemStack stack;
	private final InteractionHand hand;

	public GolemItemUseEvent(AbstractGolemEntity<?, ?> golem, ItemStack stack, InteractionHand hand) {
		super(golem);
		this.stack = stack;
		this.hand = hand;
	}

	public ItemStack getStack() {
		return stack;
	}

	public InteractionHand getHand() {
		return hand;
	}

}