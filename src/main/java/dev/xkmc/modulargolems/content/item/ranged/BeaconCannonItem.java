package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.client.weapon.ShoulderAnimData;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.misc.BeaconLaserEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemMiscEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BeaconCannonItem extends ShouldWeaponItem implements IShoulderCannonAnimated, IGenericShoulderWeapon {

	public BeaconCannonItem(Properties properties) {
		super(properties);
	}

	@Override
	public void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand) {
		onTick(e, stack, hand == InteractionHand.MAIN_HAND ? 1 : -1);
	}

	@Override
	public void onTick(AbstractGolemEntity<?, ?> e, ItemStack stack, int slot) {
		var hand = slot < 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
		if (e.tickCount % 60 == (hand == InteractionHand.MAIN_HAND ? 20 : 50) &&
				!e.level().isClientSide() && e.getTarget() != null && e.getTarget().isAlive()) {
			if (CannonPoseUtil.forEntity(e, CannonPoseUtil.BEACON_CANNON).isOutOfRange(e, hand)) return;
			var laser = new BeaconLaserEntity(GolemMiscEntities.LASER.get(), e.level(), e, 10, hand == InteractionHand.MAIN_HAND);
			e.level().addFreshEntity(laser);
			if (!e.isSilent())
				e.level().playSound(null, e.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, 2, 1.5f);
		}
	}

	@Override
	public List<ShoulderAnimData> getAnimationData(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return IShoulderCannonAnimated.super.getAnimationData(user, stack, hand);
	}

	@Override
	public List<ShoulderAnimData> getAnimationData(AbstractGolemEntity<?, ?> user, ItemStack stack, int slot) {
		if (user instanceof MetalGolemEntity metal)
			return getAnimationData(metal, stack, slot < 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
		// the dog carries the cannon flat on its back and plays no deploy animation
		return List.of();
	}

	@Override
	public @Nullable ResourceLocation getModel(int slot) {
		return getModelForHand(slot < 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
	}

	@Override
	public ResourceLocation getModelTexture(AbstractGolemEntity<?, ?> entity, ItemStack stack, int slot) {
		var id = ForgeRegistries.ITEMS.getKey(this);
		assert id != null;
		return id.withPath(e -> "textures/equipments/" + e + ".png");
	}

	@Override
	public boolean emissive() {
		return true;
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
