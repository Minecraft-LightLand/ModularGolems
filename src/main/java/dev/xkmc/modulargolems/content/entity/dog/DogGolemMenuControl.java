package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.item.ranged.IShoulderWeapon;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BannerItem;

import java.util.Optional;

public class DogGolemMenuControl extends GolemMenuControl<DogGolemEntity> {

	public DogGolemMenuControl(EquipmentsMenu menu, DogGolemEntity golem) {
		super(menu, golem);
	}

	/**
	 * The dog golem has no head equipment, so its first slot is repurposed as the banner slot. The
	 * patrol path takes the dedicated route position, leaving the arrow slot free for consumables.
	 */
public void fillMenu() {
		addSlot("right_hand", golem::getBannerSlot, true, e -> e.getItem() instanceof BannerItem, 1);
		addSlot("legs", () -> golem.getWrapperOfHand(EquipmentSlot.CHEST), true,
				e -> isValid(EquipmentSlot.CHEST, e));
		addSlot("backup", golem::getSkinSlot, true, e -> e.is(MGTagGen.CURIO_SKIN), 1);
		addSlot("feet", golem::getRouteSlot, true, e -> e.is(MGTagGen.CURIO_PATH));
		addSlot("arrow_1", golem::getArrowSlot, false, e -> true);
		addSlot("chest", golem::getShoulderSlot, true,
				e -> e.getItem() instanceof IShoulderWeapon);
	}

	public Optional<DogGolemScreenControl> getScreenProvider() {
		return Optional.of(new DogGolemScreenControl(this));
	}

}
