package dev.xkmc.modulargolems.content.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import dev.xkmc.modulargolems.content.item.ranged.ConnonPoseUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
		if (!Minecraft.getInstance().options.renderDebug || entity.animState.getStartingAnim() < 5) return;
		var vc = source.getBuffer(RenderType.LINES);
		var angles = ConnonPoseUtil.BEACON.getAngle(entity, hand);
		pose.pushPose();
		pose.translate(0, 1.5f, 0);
		pose.scale(1, -1, -1);
		pose.translate(x * (7f / 16f), 33 / 16f, -3f / 16f);
		pose.mulPose(Axis.YP.rotation(angles[0] + entity.yBodyRot * Mth.DEG_TO_RAD));
		pose.mulPose(Axis.XP.rotation(angles[1]));
		pose.translate(0, 4.5f / 16f, 17 / 16f);
		render(pose, vc, 0, 0, 0, 1);
		pose.popPose();
	}

	public static void render(PoseStack pose, VertexConsumer vc, float x0, float y0, float z0, int len) {
		var mat = pose.last().pose();
		var normal = pose.last().normal();
		vc.vertex(mat, x0 - 1, y0, z0).color(0xFFFF0000).normal(normal, 1, 0, 0).endVertex();
		vc.vertex(mat, x0 + 1, y0, z0).color(0xFFFF0000).normal(normal, 1, 0, 0).endVertex();
		vc.vertex(mat, x0, y0 - 1, z0).color(0xFF00FF00).normal(normal, 0, 1, 0).endVertex();
		vc.vertex(mat, x0, y0 + 1, z0).color(0xFF00FF00).normal(normal, 0, 1, 0).endVertex();
		vc.vertex(mat, x0, y0, z0 - 1).color(0xFF0000FF).normal(normal, 0, 0, 1).endVertex();
		vc.vertex(mat, x0, y0, z0 + len).color(0xFF0000FF).normal(normal, 0, 0, 1).endVertex();
	}


}
