package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.weapon.ShoulderAnimData;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IDogBackWeapon {

	static IDogBackWeapon of(IGenericShoulderWeapon item) {
		return new Wrapper(item);
	}

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

	record Wrapper(IGenericShoulderWeapon item) implements IDogBackWeapon {

		@Override
		public @Nullable ResourceLocation getModel() {
			return item.getModel(0);
		}

		@Override
		public List<ShoulderAnimData> getAnimationData(DogGolemEntity user, ItemStack stack) {
			return item.getAnimationData(user, stack, 0);
		}

		@Override
		public ResourceLocation getModelTexture(DogGolemEntity entity, ItemStack stack) {
			return item.getModelTexture(entity, stack, 0);
		}

		@Override
		public void onTick(DogGolemEntity e, ItemStack stack) {
			item.onTick(e, stack, 0);
		}

	}

}
