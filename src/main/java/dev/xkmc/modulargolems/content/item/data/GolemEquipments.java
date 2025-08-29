package dev.xkmc.modulargolems.content.item.data;

import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;

public record GolemEquipments(LinkedHashMap<String, ItemStack> equipments) {

	public GolemEquipments() {
		this(new LinkedHashMap<>());
	}

	public GolemEquipments copy() {
		return new GolemEquipments(new LinkedHashMap<>(equipments));
	}

}
