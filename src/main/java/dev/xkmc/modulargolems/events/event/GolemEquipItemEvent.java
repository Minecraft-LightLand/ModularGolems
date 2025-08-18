package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class GolemEquipItemEvent extends GolemEvent {

	private final ItemStack stack;

	private EquipmentSlot[] slot = new EquipmentSlot[0];
	private boolean canEquip = false;
	private int amount;

	public GolemEquipItemEvent(AbstractGolemEntity<?, ?> golem, ItemStack stack) {
		super(golem);
		this.stack = stack;
	}

	public void setSlot(int amount, EquipmentSlot... slot) {
		this.slot = slot;
		canEquip = true;
		this.amount = amount;
	}

	public boolean canEquip() {
		return canEquip;
	}

	public EquipmentSlot[] getSlot() {
		return slot;
	}

	public ItemStack getStack() {
		return stack;
	}

	public int getAmount() {
		return amount;
	}
}
