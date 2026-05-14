package dev.xkmc.modulargolems.content.client.weapon;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IEntityModelWeapon {

	@Nullable Identifier getModelForHand(HumanoidArm hand);

	default boolean shouldPlayAnimation(MetalGolemEntity user, ItemStack stack, HumanoidArm hand) {
		return false;
	}

	default float getAnimationSpeed(MetalGolemEntity user, ItemStack stack, HumanoidArm hand) {
		return 1;
	}

	default float getAnimationTick(MetalGolemEntity user, ItemStack stack, HumanoidArm hand) {
		return 0;
	}

	Identifier getModelTexture(MetalGolemEntity entity, ItemStack stack, HumanoidArm hand);

	default boolean emissive() {
		return false;
	}

	default Identifier getEmissiveTexture(MetalGolemEntity entity, ItemStack stack, HumanoidArm hand) {
		return getModelTexture(entity, stack, hand);
	}

	default @Nullable Identifier getPoseId() {
		return getModelForHand(HumanoidArm.RIGHT);
	}

}
