package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.weapon.ShoulderAnimData;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IShoulderCannonAnimated extends IShoulderWeapon {

	default @Nullable Identifier getAnimBaseId(MetalGolemEntity user, ItemStack stack, HumanoidArm hand) {
		return getModelForHand(hand);
	}

	@Override
	default List<ShoulderAnimData> getAnimationData(MetalGolemEntity user, ItemStack stack, HumanoidArm hand) {
		var model = getAnimBaseId(user, stack, hand);
		if (model == null) return List.of();
		int starting = user.animState.getStartingAnim();
		int ending = user.animState.getEndingAnim();
		if (starting >= 0 && starting <= 5)
			return List.of(new ShoulderAnimData(model.withSuffix("_start"), 1, starting));
		else if (ending >= 0 && ending <= 5)
			return List.of(new ShoulderAnimData(model.withSuffix("_end"), 1, ending));
		else if (starting > 0) {
			return List.of(new ShoulderAnimData(model.withSuffix("_active"), 1, 0));
		}
		return List.of();
	}

}
