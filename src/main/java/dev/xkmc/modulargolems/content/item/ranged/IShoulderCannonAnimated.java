package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.weapon.ShoulderAnimData;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IShoulderCannonAnimated extends IShoulderWeapon {

	default @Nullable ResourceLocation getAnimBaseId(AbstractGolemEntity<?, ?> user, ItemStack stack, InteractionHand hand) {
		return getModelForHand(hand);
	}

	@Override
	default List<ShoulderAnimData> getAnimationData(AbstractGolemEntity<?, ?> user, ItemStack stack, InteractionHand hand) {
		var model = getAnimBaseId(user, stack, hand);
		if (model == null) return List.of();
		int starting = user.getShoulderAnimStarting();
		int ending = user.getShoulderAnimEnding();
		// the dog carries the barrel on its back, where the bipedal swing up would point it at the sky,
		// so none of its deploy, hold or retract animations are played and the pose simply keeps
		// whatever the aiming produced
		boolean dog = user instanceof DogGolemEntity;
		if (dog) return List.of();
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
