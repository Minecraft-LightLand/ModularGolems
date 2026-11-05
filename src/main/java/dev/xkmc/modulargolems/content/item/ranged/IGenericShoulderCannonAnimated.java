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
 * Generic counterpart of {@link IShoulderCannonAnimated} for shoulder weapons that work on any
 * golem type rather than only the metal golem.
 */
public interface IGenericShoulderCannonAnimated extends IShoulderCannonAnimated, IGenericShoulderWeapon {

	default @Nullable ResourceLocation getAnimBaseId(AbstractGolemEntity<?, ?> user, ItemStack stack, int slot) {
		return getModel(slot);
	}

	@Override
	default @Nullable ResourceLocation getAnimBaseId(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return getAnimBaseId(user, stack, hand == InteractionHand.MAIN_HAND ? 1 : -1);
	}

	@Override
	default List<ShoulderAnimData> getAnimationData(AbstractGolemEntity<?, ?> user, ItemStack stack, int slot) {
		// a weapon carried on the center mount stays deployed and has no deploy animation
		if (slot == 0) return List.of();
		var model = getAnimBaseId(user, stack, slot);
		if (model == null) return List.of();
		return IGenericShoulderWeapon.cannonAnimationData(user, model);
	}

	@Override
	default List<ShoulderAnimData> getAnimationData(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return getAnimationData(user, stack, hand == InteractionHand.MAIN_HAND ? 1 : -1);
	}

}
