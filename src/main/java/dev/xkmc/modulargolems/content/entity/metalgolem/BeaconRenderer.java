package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemBeaconItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DyeColor;

public class BeaconRenderer {

	private static final ResourceLocation BEACON_LOCATION = ModularGolems.loc("textures/equipments/beacon.png");
	private static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");

	public static void renderGolemBeacon(MetalGolemEntity entity, PoseStack pose, MultiBufferSource source, float pTick) {
		if (entity.isAddedToLevel() && entity.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof MetalGolemBeaconItem) {
			int color = DyeColor.values()[entity.getConfigColor()].getTextureDiffuseColor();
			pose.pushPose();
			renderBeacon(pose, source, entity.tickCount + pTick);
			renderBeam(pose, source, entity.tickCount + pTick, 1F, color);
			pose.popPose();
		}
	}

	protected static void renderBeacon(PoseStack pose, MultiBufferSource source, float pTick) {
		float w = 1.5F;
		float h = 0.5f;

		pose.pushPose();
		float accurateTick = pTick % 360;
		pose.mulPose(Axis.YP.rotationDegrees(accurateTick - 45.0F));
		pose.translate(0, -0.49f, 0);

		var buffer = source.getBuffer(RenderType.armorCutoutNoCull(BEACON_LOCATION));
		PoseStack.Pose p = pose.last();
		addVertex(p, buffer, -1, h, -w, -w, 0, 0);
		addVertex(p, buffer, -1, h, -w, w, 0, 1);
		addVertex(p, buffer, -1, h, w, w, 1, 1);
		addVertex(p, buffer, -1, h, w, -w, 1, 0);
		addVertex(p, buffer, -1, -h, -w, -w, 0, 0);
		addVertex(p, buffer, -1, -h, -w, w, 0, 1);
		addVertex(p, buffer, -1, -h, w, w, 1, 1);
		addVertex(p, buffer, -1, -h, w, -w, 1, 0);
		addVertex(p, buffer, -1, -h, w, -w, 0, 0);
		addVertex(p, buffer, -1, h, w, -w, 0, 1 / 3f);
		addVertex(p, buffer, -1, h, w, w, 1, 1 / 3f);
		addVertex(p, buffer, -1, -h, w, w, 1, 0);
		addVertex(p, buffer, -1, -h, -w, -w, 0, 0);
		addVertex(p, buffer, -1, h, -w, -w, 0, 1 / 3f);
		addVertex(p, buffer, -1, h, -w, w, 1, 1 / 3f);
		addVertex(p, buffer, -1, -h, -w, w, 1, 0);
		addVertex(p, buffer, -1, -h, -w, w, 0, 0);
		addVertex(p, buffer, -1, h, -w, w, 0, 1 / 3f);
		addVertex(p, buffer, -1, h, w, w, 1, 1 / 3f);
		addVertex(p, buffer, -1, -h, w, w, 1, 0);
		addVertex(p, buffer, -1, -h, -w, -w, 0, 0);
		addVertex(p, buffer, -1, h, -w, -w, 0, 1 / 3f);
		addVertex(p, buffer, -1, h, w, -w, 1, 1 / 3f);
		addVertex(p, buffer, -1, -h, w, -w, 1, 0);

		pose.popPose();
	}

	protected static void renderBeam(PoseStack pose, MultiBufferSource source, float pTick, float scale, int color) {
		float width1 = 0.2F;
		float width2 = 0.25F;
		int length = 1024;

		float accurateTick = pTick % 40;
		float f2 = Mth.frac(accurateTick * 0.2F - (float) Mth.floor(accurateTick * 0.1F));
		pose.pushPose();
		pose.mulPose(Axis.YP.rotationDegrees(accurateTick * 2.25F - 45.0F));
		float v1 = -1.0F + f2;
		float v2 = (float) length * scale * (0.5F / width1) + v1;
		renderPart(pose, source.getBuffer(RenderType.beaconBeam(BEAM_LOCATION, false)),
				color,
				0, length, 0.0F, width1, width1, 0.0F, -width1, 0.0F, 0.0F, -width1,
				0.0F, 1.0F, v2, v1);
		pose.popPose();
		v1 = -1.0F + f2;
		v2 = (float) length * scale + v1;
		renderPart(pose, source.getBuffer(RenderType.beaconBeam(BEAM_LOCATION, true)),
				color & 0x00FFFFFF | 0x1F000000,
				0, length, -width2, -width2, width2, -width2, -width2, width2, width2, width2,
				0.0F, 1.0F, v2, v1);
	}

	private static void renderPart(PoseStack pose, VertexConsumer buffer, int color, int start, int end, float p_112164_, float p_112165_, float p_112166_, float p_112167_, float p_112168_, float p_112169_, float p_112170_, float p_112171_, float u1, float u2, float v1, float v2) {
		PoseStack.Pose p = pose.last();
		renderQuad(p, buffer, color, start, end, p_112164_, p_112165_, p_112166_, p_112167_, u1, u2, v1, v2);
		renderQuad(p, buffer, color, start, end, p_112170_, p_112171_, p_112168_, p_112169_, u1, u2, v1, v2);
		renderQuad(p, buffer, color, start, end, p_112166_, p_112167_, p_112170_, p_112171_, u1, u2, v1, v2);
		renderQuad(p, buffer, color, start, end, p_112168_, p_112169_, p_112164_, p_112165_, u1, u2, v1, v2);
	}

	private static void renderQuad(PoseStack.Pose pose, VertexConsumer buffer, int color, int y1, int y2, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
		addVertex(pose, buffer, color, y2, x1, z1, u2, v1);
		addVertex(pose, buffer, color, y1, x1, z1, u2, v2);
		addVertex(pose, buffer, color, y1, x2, z2, u1, v2);
		addVertex(pose, buffer, color, y2, x2, z2, u1, v1);
	}

	private static void addVertex(PoseStack.Pose pose, VertexConsumer buffer, int color, float y, float x, float z, float u, float v) {
		buffer.addVertex(pose, x, y, z).setColor(color).setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT)
				.setNormal(pose, 0.0F, 1.0F, 0.0F);
	}

}
