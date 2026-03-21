package dev.xkmc.modulargolems.content.client.weapon;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IEntityModelWeapon {

	@Nullable ResourceLocation getModelForHand(InteractionHand hand);

	default boolean shouldPlayAnimation(LivingEntity user, ItemStack stack, InteractionHand hand) {
		return false;
	}

	default float getAnimationSpeed(LivingEntity user, ItemStack stack, InteractionHand hand) {
		return 1;
	}

	default float getAnimationTick(LivingEntity user, ItemStack stack, InteractionHand hand) {
		return 0;
	}

	ResourceLocation getModelTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand);

	default boolean emissive() {
		return false;
	}

	default ResourceLocation getEmissiveTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		return getModelTexture(entity, stack, hand);
	}

	default @Nullable ResourceLocation getPoseId() {
		return getModelForHand(InteractionHand.MAIN_HAND);
	}

}
