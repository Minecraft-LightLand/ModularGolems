package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.modulargolems.content.core.MenuControl;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;

public abstract class SweepGolemMenuControl<T extends SweepGolemEntity<T, ?>> extends MenuControl<T> {

	public SweepGolemMenuControl(EquipmentsMenu menu, T golem) {
		super(menu, golem);
	}

	public void fillMenu() {
		menu.addSlot("right_hand", (i, e) -> isValid(EquipmentSlot.MAINHAND, e));
		menu.addSlot("left_hand", (i, e) -> isValid(EquipmentSlot.OFFHAND, e));
		menu.addSlot("head", e -> isValid(EquipmentSlot.HEAD, e));
		menu.addSlot("chest", e -> isValid(EquipmentSlot.CHEST, e));
		menu.addSlot("legs", e -> isValid(EquipmentSlot.LEGS, e));
		menu.addSlot("feet", e -> isValid(EquipmentSlot.FEET, e));
		menu.addSlot("backup", e -> isValid(EquipmentSlot.MAINHAND, e) || isValid(EquipmentSlot.OFFHAND, e));
		menu.addSlot("arrow", e -> true);
	}

	public void handleQuickMove(ItemStack stack) {
		if (stack.getItem() instanceof ArrowItem) {
			menu.moveItemStackTo(stack, 36 + 7, 37 + 7, false);
		}
	}

	public EquipmentSlot[] getSlotDefinition() {
		return EquipmentsMenu.SLOTS;
	}

}
