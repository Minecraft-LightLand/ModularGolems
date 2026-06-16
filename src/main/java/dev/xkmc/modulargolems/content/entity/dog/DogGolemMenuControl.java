package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.modulargolems.content.core.MenuControl;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.Optional;

public class DogGolemMenuControl extends MenuControl<DogGolemEntity> {

	public DogGolemMenuControl(EquipmentsMenu menu, DogGolemEntity golem) {
		super(menu, golem);
	}

	public void fillMenu() {
		menu.addSlot("chest", e -> isValid(EquipmentSlot.HEAD, e));
		menu.addSlot("legs", e -> isValid(EquipmentSlot.CHEST, e));
	}

	public EquipmentSlot[] getSlotDefinition() {
		return EquipmentsMenu.DOG_SLOTS;
	}

	public Optional<DogGolemScreenControl> getScreenProvider() {
		return Optional.of(new DogGolemScreenControl(this));
	}

}
