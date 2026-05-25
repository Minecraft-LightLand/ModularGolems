package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.modulargolems.content.client.pose.GolemShoulderPose;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record MetalGolemModelItemState(
		List<List<String>> paths,
		List<Pair<KeyframeAnimation, Float>> anims,
		@Nullable ShoulderData shoulder
) {

	public static MetalGolemModelItemState ofArmor(List<List<String>> paths) {
		return new MetalGolemModelItemState(paths, List.of(), null);
	}

	public static MetalGolemModelItemState ofShoulder(
			List<List<String>> paths,
			List<Pair<KeyframeAnimation, Float>> anims,
			ItemStack stack, HumanoidArm hand, Identifier id,
			MetalGolemAimState aim) {
		return new MetalGolemModelItemState(paths, anims, new ShoulderData(stack, hand, id, aim));
	}

	public static MetalGolemModelItemState ofWeapon(List<List<String>> paths, @Nullable Pair<KeyframeAnimation, Float> animData) {
		return new MetalGolemModelItemState(paths, animData == null ? List.of() : List.of(animData), null);
	}

	public void setupAnim(MetalGolemModel model) {
		model.root().getAllParts().forEach(e -> e.skipDraw = true);
		for (List<String> ls : paths) {
			ModelPart part = model.root();
			for (String s : ls) {
				part = part.getChild(s);
			}
			part.getAllParts().forEach(e -> e.skipDraw = false);
		}
		for (var pair : anims) {
			var state = new AnimationState();
			state.startIfStopped(0);
			pair.getFirst().apply(state, pair.getSecond());
		}
		if (shoulder != null) shoulder.setupAnim(model);
	}

	record ShoulderData(
			ItemStack stack, HumanoidArm hand, Identifier id, MetalGolemAimState aim
	) {

		public void setupAnim(MetalGolemModel model) {
			var sp = GolemShoulderPose.MAP.get(id);
			if (sp != null) {
				sp.setup(aim, model, stack, hand);
			}
		}

	}

}
