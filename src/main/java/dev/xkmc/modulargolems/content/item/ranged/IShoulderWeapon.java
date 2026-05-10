package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.weapon.ShoulderAnimData;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IShoulderWeapon {

	@Nullable Identifier getModelForHand(InteractionHand hand);

	List<ShoulderAnimData> getAnimationData(MetalGolemEntity user, ItemStack stack, InteractionHand hand);

	Identifier getModelTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand);

	default boolean emissive() {
		return false;
	}

	default Identifier getEmissiveTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		return getModelTexture(entity, stack, hand);
	}

	void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand);

}
