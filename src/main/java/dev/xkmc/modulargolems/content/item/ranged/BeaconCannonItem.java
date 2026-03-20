package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.misc.BeaconLaserEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemMiscEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BeaconCannonItem extends ShouldWeaponItem implements IShoulderCannonAnimated {

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
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(MGLangData.BEACON_CANNON.get());
		super.appendHoverText(stack, level, list, flag);
	}
}
