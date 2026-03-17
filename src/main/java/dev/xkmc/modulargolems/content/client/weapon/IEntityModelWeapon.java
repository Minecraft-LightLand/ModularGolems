package dev.xkmc.modulargolems.content.client.weapon;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IEntityModelWeapon {

	@Nullable ResourceLocation getModelForHand(InteractionHand hand);

	boolean shouldPlayAnimation(LivingEntity user, ItemStack stack, InteractionHand hand);

	float getAnimationSpeed(LivingEntity user, ItemStack stack, InteractionHand hand);

	float getAnimationTick(LivingEntity user, ItemStack stack, InteractionHand hand);

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
