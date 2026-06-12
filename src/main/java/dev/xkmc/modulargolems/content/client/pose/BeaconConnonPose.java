package dev.xkmc.modulargolems.content.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import dev.xkmc.modulargolems.content.item.ranged.CannonPoseUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import static dev.xkmc.modulargolems.content.item.ranged.CannonPoseUtil.MAX_DEGREE;

public record BeaconConnonPose(
		CannonPoseUtil transform, String id, float x, float y, float z
) implements GolemShoulderPose {

	@Deprecated
	public BeaconConnonPose(String id, float x, float y, float z) {
		this(CannonPoseUtil.BEACON, id, x, y, z);
	}

	@Override
	public void setup(MetalGolemEntity entity, MetalGolemModel model, ItemStack stack, InteractionHand hand, float pTick) {
		if (entity.animState.getStartingAnim() < 5) return;
		var part = model.root().getChild("body").getChild(id);

		var angles = transform.getAngle(entity, hand);
		var diff = Mth.wrapDegrees(angles[0] * Mth.RAD_TO_DEG + entity.yBodyRot);
		if (diff > MAX_DEGREE) {
			angles[0] = (MAX_DEGREE - entity.yBodyRot) * Mth.DEG_TO_RAD;
		}
		if (diff < -MAX_DEGREE) {
			angles[0] = (-MAX_DEGREE - entity.yBodyRot) * Mth.DEG_TO_RAD;
		}
		part.yRot -= angles[0] + entity.yBodyRot * Mth.DEG_TO_RAD;
		part.xRot += angles[1];

	}

	@Override
	public void render(MetalGolemEntity entity, MetalGolemModel model, ItemStack stack, InteractionHand hand, PoseStack pose, MultiBufferSource source, int light, float pTick) {
	}

}
