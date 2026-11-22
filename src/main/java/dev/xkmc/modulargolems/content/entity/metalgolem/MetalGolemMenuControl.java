package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.modulargolems.content.entity.common.SweepGolemMenuControl;
import dev.xkmc.modulargolems.content.item.ranged.IShoulderWeapon;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;

import java.util.Optional;

public class MetalGolemMenuControl extends SweepGolemMenuControl<MetalGolemEntity> {

	public MetalGolemMenuControl(EquipmentsMenu menu, MetalGolemEntity golem) {
		super(menu, golem);
	}

	@Override
	public void fillMenu() {
		super.fillMenu();
		addSlot("right_shoulder", golem::getRightShoulder, true, e -> e.getItem() instanceof IShoulderWeapon);
		addSlot("left_shoulder", golem::getLeftShoulder, true, e -> e.getItem() instanceof IShoulderWeapon);
	}

	public Optional<MetalGolemScreenControl> getScreenProvider() {
		return Optional.of(new MetalGolemScreenControl(this));
	}

}
