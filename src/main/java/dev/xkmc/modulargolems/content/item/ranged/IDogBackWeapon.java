package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.weapon.ShoulderAnimData;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IDogBackWeapon {

	@Nullable ResourceLocation getModel();

	List<ShoulderAnimData> getAnimationData(DogGolemEntity user, ItemStack stack);

	ResourceLocation getModelTexture(DogGolemEntity entity, ItemStack stack);

	default boolean emissive() {
		return false;
	}

	default ResourceLocation getEmissiveTexture(DogGolemEntity entity, ItemStack stack) {
		return getModelTexture(entity, stack);
	}

	void onTick(DogGolemEntity e, ItemStack stack);

}
