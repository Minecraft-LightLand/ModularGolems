package dev.xkmc.modulargolems.content.entity.render;

import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record CommonGolemRenderState(
		Component name,
		ItemStack skin,
		List<GolemMaterial> materials,
		int id,
		boolean aggressive,
		int getVehicleId) {

	public static CommonGolemRenderState of(AbstractGolemEntity<?, ?> e) {
		var skin = ItemStack.EMPTY;
		if (CurioCompatRegistry.get() != null)
			skin = CurioCompatRegistry.get().getSkin(e);
		int vid = -1;
		var veh = e.getVehicle();
		if (veh != null) vid = veh.getId();
		return new CommonGolemRenderState(e.getDisplayName(), skin, e.getMaterials(), e.getId(), e.isAggressive(), vid);
	}

}
