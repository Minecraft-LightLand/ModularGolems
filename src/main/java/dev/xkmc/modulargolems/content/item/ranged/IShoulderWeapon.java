package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IShoulderWeapon {

	@Nullable ResourceLocation getModelForHand(InteractionHand hand);

	@Nullable ResourceLocation getAnimationId(MetalGolemEntity user, ItemStack stack, InteractionHand hand);

	float getAnimationSpeed(MetalGolemEntity user, ItemStack stack, InteractionHand hand);

	float getAnimationTick(MetalGolemEntity user, ItemStack stack, InteractionHand hand);

	ResourceLocation getModelTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand);

	default boolean emissive() {
		return false;
	}

	default ResourceLocation getEmissiveTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		return getModelTexture(entity, stack, hand);
	}

	void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand);

}
