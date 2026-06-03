package dev.xkmc.modulargolems.content.item.equipments;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface GolemModelItem {

	ResourceLocation getModelTexture(LivingEntity user);

	ResourceLocation getModelPath();

	default ResourceLocation getEmissiveModelTexture(LivingEntity user) {
		return getModelPath();
	}


	default boolean emissive(LivingEntity user, ItemStack stack) {
		return emissive();
	}

	default boolean emissive() {
		return false;
	}

}
