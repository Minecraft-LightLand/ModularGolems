package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BannerItem;

public abstract class SweepGolemMenuControl<T extends SweepGolemEntity<T, ?>> extends GolemMenuControl<T> {

	public SweepGolemMenuControl(EquipmentsMenu menu, T golem) {
		super(menu, golem);
	}

	public void fillMenu() {
		addSlot("right_hand", () -> golem.getWrapperOfHand(EquipmentSlot.MAINHAND), true,
				(i, e) -> isValid(EquipmentSlot.MAINHAND, e));
		addSlot("left_hand", () -> golem.getWrapperOfHand(EquipmentSlot.OFFHAND), true,
				(i, e) -> isValid(EquipmentSlot.OFFHAND, e));
		addSlot("head", () -> golem.getWrapperOfHand(EquipmentSlot.HEAD), true,
				e -> isValid(EquipmentSlot.HEAD, e));
		addSlot("chest", () -> golem.getWrapperOfHand(EquipmentSlot.CHEST), true,
				e -> isValid(EquipmentSlot.CHEST, e));
		addSlot("legs", () -> golem.getWrapperOfHand(EquipmentSlot.LEGS), true,
				e -> isValid(EquipmentSlot.LEGS, e));
		addSlot("feet", () -> golem.getWrapperOfHand(EquipmentSlot.FEET), true,
				e -> isValid(EquipmentSlot.FEET, e));
		addSlot("backup", golem::getBackupHand, true,
				e -> isValid(EquipmentSlot.MAINHAND, e) || isValid(EquipmentSlot.OFFHAND, e));
		addSlot("arrow_1", golem::getArrowSlot, false, e -> true);
		addSlot("arrow_2", golem::getArrowSlot2, false, e -> true);
		addSlot("banner", golem::getBannerSlot, true, e -> e.getItem() instanceof BannerItem, 1);
		addSlot("skin", golem::getSkinSlot, true, e -> e.is(MGTagGen.CURIO_SKIN), 1);
		addSlot("route", golem::getRouteSlot, true, e -> e.is(MGTagGen.CURIO_PATH));
	}

}
