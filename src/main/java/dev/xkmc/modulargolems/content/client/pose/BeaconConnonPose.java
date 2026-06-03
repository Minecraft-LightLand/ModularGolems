package dev.xkmc.modulargolems.content.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemAimState;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemRenderState;
import dev.xkmc.modulargolems.content.item.ranged.CannonPoseUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import static dev.xkmc.modulargolems.content.item.ranged.CannonPoseUtil.MAX_DEGREE;

public record BeaconConnonPose(
		CannonPoseUtil transform, String id, float x, float y, float z
) implements GolemShoulderPose {

	@Override
	public void setup(MetalGolemAimState entity, MetalGolemModel model, ItemStack stack, HumanoidArm hand) {
		if (entity.animState().getStartingAnim() < 5) return;
		var part = model.root().getChild("body").getChild(id);

		var angles = transform.getAngle(entity, hand);
		var diff = Mth.wrapDegrees(angles[0] * Mth.RAD_TO_DEG + entity.yBodyRot());
		if (diff > MAX_DEGREE) {
			angles[0] = (MAX_DEGREE - entity.yBodyRot()) * Mth.DEG_TO_RAD;
		}
		if (diff < -MAX_DEGREE) {
			angles[0] = (-MAX_DEGREE - entity.yBodyRot()) * Mth.DEG_TO_RAD;
		}
		part.yRot -= angles[0] + entity.yBodyRot() * Mth.DEG_TO_RAD;
		part.xRot += angles[1];

	}

	@Override
	public void submit(MetalGolemRenderState entity, ItemStack stack, HumanoidArm hand, PoseStack pose, SubmitNodeCollector source, int light) {

	}

}
