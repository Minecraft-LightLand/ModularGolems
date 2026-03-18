package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.misc.BeaconLaserEntity;
import dev.xkmc.modulargolems.init.registrate.GolemMiscEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BeaconCannonItem extends ShouldWeaponItem {

	public BeaconCannonItem(Properties properties) {
		super(properties);
	}

	@Override
	public void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand) {
		if (e.tickCount % 60 == (hand == InteractionHand.MAIN_HAND ? 20 : 50) &&
				!e.level().isClientSide() && e.getTarget() != null && e.getTarget().isAlive()) {
			var rot = ConnonPoseUtil.BEACON.getAngle(e, hand);
			var diff = Mth.wrapDegrees(rot[0] * Mth.RAD_TO_DEG + e.yBodyRot);
			if (Math.abs(diff) > 30) return;
			var laser = new BeaconLaserEntity(GolemMiscEntities.LASER.get(), e.level(), e, 10, hand == InteractionHand.MAIN_HAND);
			e.level().addFreshEntity(laser);
			e.level().playSound(null, e.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL,2,1.5f);
		}
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
