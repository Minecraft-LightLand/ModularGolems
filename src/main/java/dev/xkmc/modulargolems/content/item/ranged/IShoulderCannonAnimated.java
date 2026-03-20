package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IShoulderCannonAnimated extends IShoulderWeapon {

	default @Nullable ResourceLocation getAnimBaseId(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return getModelForHand(hand);
	}

	@Override
	default @Nullable ResourceLocation getAnimationId(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		var model = getModelForHand(hand);
		if (model == null) return null;
		int starting = user.animState.getStartingAnim();
		int ending = user.animState.getEndingAnim();
		if (starting >= 0 && starting <= 5)
			return model.withSuffix("_start");
		if (ending >= 0 && ending <= 5)
			return model.withSuffix("_end");
		if (starting > 0) {
			return model.withSuffix("_active");
		}
		return null;
	}

	@Override
	default float getAnimationSpeed(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return 1;
	}

	@Override
	default float getAnimationTick(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		int starting = user.animState.getStartingAnim();
		int ending = user.animState.getEndingAnim();
		if (starting >= 0 && starting <= 5)
			return starting;
		if (ending >= 0 && ending <= 5)
			return ending;
		return 0;
	}

}
