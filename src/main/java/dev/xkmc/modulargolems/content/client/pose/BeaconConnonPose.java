package dev.xkmc.modulargolems.content.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import dev.xkmc.modulargolems.content.item.ranged.ConnonPoseUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public record BeaconConnonPose(String id, float x, float y, float z) implements GolemShoulderPose {

	@Override
	public void setup(MetalGolemEntity entity, MetalGolemModel model, ItemStack stack, InteractionHand hand, float pTick) {
		if (entity.animState.getStartingAnim() < 5) return;
		var part = model.root().getChild("body").getChild(id);

		var angles = ConnonPoseUtil.BEACON.getAngle(entity, hand);
		var diff = Mth.wrapDegrees(angles[0] * Mth.RAD_TO_DEG + entity.yBodyRot);
		if (diff > 30) {
			angles[0] = (30 - entity.yBodyRot) * Mth.DEG_TO_RAD;
		}
		if (diff < -30) {
			angles[0] = (-30 - entity.yBodyRot) * Mth.DEG_TO_RAD;
		}
		part.yRot -= angles[0] + entity.yBodyRot * Mth.DEG_TO_RAD;
		part.xRot += angles[1];

	}

	@Override
	public void render(MetalGolemEntity entity, MetalGolemModel model, ItemStack stack, InteractionHand hand, PoseStack pose, MultiBufferSource source, float pTick) {
	}


}
