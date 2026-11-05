package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.weapon.ShoulderAnimData;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Slot: 0 for center, >=1 for mainhand, <=-1 for offhand, closer to 0 means closer to center o body
 *
 */
public interface IGenericShoulderWeapon extends IShoulderWeapon {

	@Nullable ResourceLocation getModel(int slot);

	@Override
	default boolean emissive() {
		return false;
	}

	List<ShoulderAnimData> getAnimationData(AbstractGolemEntity<?, ?> user, ItemStack stack, int slot);

	ResourceLocation getModelTexture(AbstractGolemEntity<?, ?> entity, ItemStack stack, int slot);

	default ResourceLocation getEmissiveTexture(AbstractGolemEntity<?, ?> entity, ItemStack stack, int slot) {
		return getModelTexture(entity, stack, slot);
	}

	void onTick(AbstractGolemEntity<?, ?> e, ItemStack stack, int slot);

	@Override
	default @Nullable ResourceLocation getModelForHand(InteractionHand hand) {
		return getModel(hand == InteractionHand.MAIN_HAND ? 1 : -1);
	}

	@Override
	default List<ShoulderAnimData> getAnimationData(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return getAnimationData(user, stack, hand == InteractionHand.MAIN_HAND ? 1 : -1);
	}

	@Override
	default ResourceLocation getModelTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		return getModelTexture(entity, stack, hand == InteractionHand.MAIN_HAND ? 1 : -1);
	}

	@Override
	default void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand) {
		onTick(e, stack, hand == InteractionHand.MAIN_HAND ? 1 : -1);
	}

}
