package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BeaconCannonItem extends ShouldWeaponItem {

	public BeaconCannonItem(Properties properties) {
		super(properties);
	}

	@Override
	public void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand) {

	}

	@Override
	public ResourceLocation getModelForHand(InteractionHand hand) {
		return hand == InteractionHand.MAIN_HAND ? GolemModelPaths.BEACON_RIGHT : GolemModelPaths.BEACON_LEFT;
	}

	@Override
	public @Nullable ResourceLocation getAnimationId(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		var model = getModelForHand(hand);
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
	public float getAnimationSpeed(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return 1;
	}

	@Override
	public float getAnimationTick(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		int starting = user.animState.getStartingAnim();
		int ending = user.animState.getEndingAnim();
		if (starting >= 0 && starting <= 5)
			return starting;
		if (ending >= 0 && ending <= 5)
			return ending;
		return 0;
	}

}
