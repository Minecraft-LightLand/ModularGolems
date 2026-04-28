package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemBeaconItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BeaconRenderer {

	public static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");

	public static void renderGolemBeacon(MetalGolemEntity entity, PoseStack pose, MultiBufferSource source, float pTick) {
		if (entity.isAddedToWorld() && entity.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof MetalGolemBeaconItem) {
			var color = DyeColor.values()[entity.getConfigColor()].getTextureDiffuseColors();
			float totalTick = entity.tickCount + pTick;
			float entityScale = entity.getScale();
			float beaconScale = 0.5f * entityScale;
			float radius = 1.2f * entityScale;
			float rotationSpeed = 2.5f;
			for (int i = 0; i < 3; i++) {
				float angleOffset = i * 120f;
				float angle = totalTick * rotationSpeed + angleOffset;
				float beaconX = (float) Math.cos(Math.toRadians(angle)) * radius;
				float beaconZ = (float) Math.sin(Math.toRadians(angle)) * radius;
				float beaconY = entity.getBbHeight() * 0.1f;
				pose.pushPose();
				pose.translate(beaconX, beaconY, beaconZ);
				pose.scale(beaconScale, beaconScale, beaconScale);
				pose.mulPose(Axis.YP.rotationDegrees(-angle));
				ItemStack beaconStack = new ItemStack(Blocks.BEACON);
				Minecraft.getInstance().getItemRenderer().renderStatic(
						beaconStack,
						ItemDisplayContext.FIXED,
						15728880,
						OverlayTexture.NO_OVERLAY,
						pose,
						source,
						entity.level(),
						0
				);
				pose.popPose();
				pose.pushPose();
				pose.translate(beaconX, beaconY, beaconZ);
				float beamScale = 0.25f * entityScale;
				renderBeam(pose, source, totalTick + i * 10, beamScale, 1024, color);
				pose.popPose();
			}
		}
	}

	public static void renderBeam(PoseStack pose, MultiBufferSource source, float pTick, float scale, float length, float[] color) {
		float width1 = 0.2F * scale;
		float width2 = 0.25F * scale;

		float accurateTick = pTick % 40;
		float f2 = Mth.frac(accurateTick * 0.2F - (float) Mth.floor(accurateTick * 0.1F));
		float colorR = color[0];
		float colorG = color[1];
		float colorB = color[2];
		pose.pushPose();
		pose.mulPose(Axis.YP.rotationDegrees(accurateTick * 2.25F - 45.0F));
		float v1 = -1.0F + f2;
		float v2 = length * scale * (0.5F / width1) + v1;
		renderPart(pose, source.getBuffer(RenderType.beaconBeam(BEAM_LOCATION, false)),
				colorR, colorG, colorB, 1.0F,
				0, length, 0.0F, width1, width1, 0.0F, -width1, 0.0F, 0.0F, -width1,
				0.0F, 1.0F, v2, v1);
		pose.popPose();
		v1 = -1.0F + f2;
		v2 = (float) length * scale + v1;
		renderPart(pose, source.getBuffer(RenderType.beaconBeam(BEAM_LOCATION, true)),
				colorR, colorG, colorB, 0.125F,
				0, length, -width2, -width2, width2, -width2, -width2, width2, width2, width2,
				0.0F, 1.0F, v2, v1);
	}

	private static void renderPart(PoseStack pose, VertexConsumer buffer, float r, float g, float b, float a, float start, float end, float p_112164_, float p_112165_, float p_112166_, float p_112167_, float p_112168_, float p_112169_, float p_112170_, float p_112171_, float u1, float u2, float v1, float v2) {
		PoseStack.Pose posestack$pose = pose.last();
		Matrix4f matrix4f = posestack$pose.pose();
		Matrix3f matrix3f = posestack$pose.normal();
		renderQuad(matrix4f, matrix3f, buffer, r, g, b, a, start, end, p_112164_, p_112165_, p_112166_, p_112167_, u1, u2, v1, v2);
		renderQuad(matrix4f, matrix3f, buffer, r, g, b, a, start, end, p_112170_, p_112171_, p_112168_, p_112169_, u1, u2, v1, v2);
		renderQuad(matrix4f, matrix3f, buffer, r, g, b, a, start, end, p_112166_, p_112167_, p_112170_, p_112171_, u1, u2, v1, v2);
		renderQuad(matrix4f, matrix3f, buffer, r, g, b, a, start, end, p_112168_, p_112169_, p_112164_, p_112165_, u1, u2, v1, v2);
	}

	private static void renderQuad(Matrix4f pose, Matrix3f normal, VertexConsumer buffer, float r, float g, float b, float a, float y1, float y2, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
		addVertex(pose, normal, buffer, r, g, b, a, y2, x1, z1, u2, v1);
		addVertex(pose, normal, buffer, r, g, b, a, y1, x1, z1, u2, v2);
		addVertex(pose, normal, buffer, r, g, b, a, y1, x2, z2, u1, v2);
		addVertex(pose, normal, buffer, r, g, b, a, y2, x2, z2, u1, v1);
	}

	private static void addVertex(Matrix4f pose, Matrix3f normal, VertexConsumer buffer, float r, float g, float b, float a, float y, float x, float z, float u, float v) {
		buffer.vertex(pose, x, y, z).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
	}

}