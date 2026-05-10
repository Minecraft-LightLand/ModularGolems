package dev.xkmc.modulargolems.content.client.weapon;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IEntityModelWeapon {

	@Nullable Identifier getModelForHand(InteractionHand hand);

	default boolean shouldPlayAnimation(LivingEntity user, ItemStack stack, InteractionHand hand) {
		return false;
	}

	default float getAnimationSpeed(LivingEntity user, ItemStack stack, InteractionHand hand) {
		return 1;
	}

	default float getAnimationTick(LivingEntity user, ItemStack stack, InteractionHand hand) {
		return 0;
	}

	Identifier getModelTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand);

	default boolean emissive() {
		return false;
	}

	default Identifier getEmissiveTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		return getModelTexture(entity, stack, hand);
	}

	default @Nullable Identifier getPoseId() {
		return getModelForHand(InteractionHand.MAIN_HAND);
	}

}
