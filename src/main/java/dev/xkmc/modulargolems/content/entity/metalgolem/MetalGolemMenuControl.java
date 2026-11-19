package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.modulargolems.content.entity.common.SweepGolemMenuControl;
import dev.xkmc.modulargolems.content.item.ranged.IShoulderWeapon;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class MetalGolemMenuControl extends SweepGolemMenuControl<MetalGolemEntity> {

	public MetalGolemMenuControl(EquipmentsMenu menu, MetalGolemEntity golem) {
		super(menu, golem);
	}

	public void fillMenu() {
		super.fillMenu();
		menu.addSlot("right_shoulder", e -> e.getItem() instanceof IShoulderWeapon);
		menu.addSlot("left_shoulder", e -> e.getItem() instanceof IShoulderWeapon);
	}

	public boolean isValid(EquipmentSlot slot, ItemStack stack) {
		var valids = getSlotForItem(stack);
		if (valids.contains(EquipmentSlot.MAINHAND)) {
			if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
				return true;
		}
		return valids.contains(slot);
	}

	@Override
	public void handleQuickMove(ItemStack stack) {
		super.handleQuickMove(stack);
		if (stack.getItem() instanceof IShoulderWeapon) {
			menu.moveItemStackTo(stack, 36 + 8, 37 + 9, false);
		}
	}

	public Optional<MetalGolemScreenControl> getScreenProvider() {
		return Optional.of(new MetalGolemScreenControl(this));
	}

}
