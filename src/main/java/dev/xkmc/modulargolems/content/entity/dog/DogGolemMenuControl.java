package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.item.ranged.IGenericShoulderWeapon;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.Optional;

public class DogGolemMenuControl extends GolemMenuControl<DogGolemEntity> {

	public DogGolemMenuControl(EquipmentsMenu menu, DogGolemEntity golem) {
		super(menu, golem);
	}

	@Override
	public void fillMenu() {
		addSlot("chest", golem::getShoulderSlot, true, e -> e.getItem() instanceof IGenericShoulderWeapon);
		addSlot("legs", () -> golem.getWrapperOfHand(EquipmentSlot.BODY), true,
				e -> isValid(EquipmentSlot.BODY, e));
		addSlot("arrow_1", golem::getArrowSlot, false, e -> true);
		addSlot("right_hand", golem::getBannerSlot, true, e -> e.getItem() instanceof net.minecraft.world.item.BannerItem, 1);
		addSlot("backup", golem::getSkinSlot, true, e -> e.is(dev.xkmc.modulargolems.init.data.MGTagGen.CURIO_SKIN), 1);
		addSlot("feet", golem::getRouteSlot, true, e -> e.is(dev.xkmc.modulargolems.init.data.MGTagGen.CURIO_PATH));
		addSlot("arrow_2", golem::getArrowSlot2, false, e -> true);
	}

	public EquipmentSlot[] getSlotDefinition() {
		return EquipmentsMenu.DOG_SLOTS;
	}

	public Optional<DogGolemScreenControl> getScreenProvider() {
		return Optional.of(new DogGolemScreenControl(this));
	}

}
