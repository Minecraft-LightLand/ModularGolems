package dev.xkmc.modulargolems.content.item.equipments;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

public interface GolemModelItem {

	Identifier getModelTexture(LivingEntity user);

	Identifier getModelPath();

	default Identifier getEmissiveModelTexture(LivingEntity user) {
		return getModelPath();
	}

	default boolean emissive() {
		return false;
	}

}
