package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.world.entity.LivingEntity.getEquipmentSlotForItem;

public class GolemEquipEvent extends HumanoidGolemEvent {

	private final ItemStack stack;

	private EquipmentSlot slot;
	private boolean canEquip;
	private int amount;

	public GolemEquipEvent(HumanoidGolemEntity golem, ItemStack stack) {
		super(golem);
		this.stack = stack;
		slot = getEquipmentSlotForItem(stack);
		canEquip = stack.canEquip(slot, golem);
		amount = 1;
	}

	public void setSlot(EquipmentSlot slot, int amount) {
		this.slot = slot;
		canEquip = true;
		this.amount = amount;
	}

	public boolean canEquip() {
		return canEquip;
	}

	public EquipmentSlot getSlot() {
		return slot;
	}

	public ItemStack getStack() {
		return stack;
	}

	public int getAmount() {
		return amount;
	}
}
