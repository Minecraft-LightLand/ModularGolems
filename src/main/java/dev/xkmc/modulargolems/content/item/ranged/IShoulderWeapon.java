package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.weapon.ShoulderAnimData;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IShoulderWeapon {

	@Nullable ResourceLocation getModelForHand(InteractionHand hand);

	/**
	 * 需要看另一侧槽位才能决定模型的装备覆写这个（如肩鞍：单边用单边模型，双边用整体模型）。
	 */
	@Nullable
	default ResourceLocation getModelForHand(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		return getModelForHand(hand);
	}

	List<ShoulderAnimData> getAnimationData(MetalGolemEntity user, ItemStack stack, InteractionHand hand);

	ResourceLocation getModelTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand);

	default boolean emissive() {
		return false;
	}

	default ResourceLocation getEmissiveTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		return getModelTexture(entity, stack, hand);
	}

	void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand);

}
