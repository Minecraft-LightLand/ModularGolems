package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModelState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface GolemModelItem {

	Identifier getModelTexture(MetalGolemModelState user);

	Identifier getModelPath();

	default Identifier getEmissiveModelTexture(MetalGolemModelState user) {
		return getModelPath();
	}

	default boolean emissive(MetalGolemModelState user, ItemStack stack) {
		return emissive();
	}

	default boolean emissive() {
		return false;
	}

}
