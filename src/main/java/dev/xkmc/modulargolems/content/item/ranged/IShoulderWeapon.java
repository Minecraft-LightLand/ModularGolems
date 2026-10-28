package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.weapon.ShoulderAnimData;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IShoulderWeapon {

	@Nullable ResourceLocation getModelForHand(InteractionHand hand);

	List<ShoulderAnimData> getAnimationData(AbstractGolemEntity<?, ?> user, ItemStack stack, InteractionHand hand);

	ResourceLocation getModelTexture(AbstractGolemEntity<?, ?> entity, ItemStack stack, InteractionHand hand);

	default boolean emissive() {
		return false;
	}

	default ResourceLocation getEmissiveTexture(AbstractGolemEntity<?, ?> entity, ItemStack stack, InteractionHand hand) {
		return getModelTexture(entity, stack, hand);
	}

	void onTick(AbstractGolemEntity<?, ?> e, ItemStack stack, InteractionHand hand);

}
