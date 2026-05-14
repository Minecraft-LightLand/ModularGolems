package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;

public class BeaconRenderer {

	private static final Identifier BEACON_LOCATION = ModularGolems.loc("textures/equipments/beacon.png");
	public static final Identifier BEAM_LOCATION = Identifier.withDefaultNamespace("textures/entity/beacon_beam.png");

	public static void renderGolemBeacon(MetalGolemRenderState state, PoseStack pose, SubmitNodeCollector col, CameraRenderState cam) {
		var beacon = state.beacon;
		if (beacon == null) return;
		int color = DyeColor.values()[state.common().configColor()].getTextureDiffuseColor();
		float totalTick = state.common().time();
		float entityScale = state.scale;
		float beaconScale = 0.5f * entityScale;
		float radius = 1.2f * entityScale;
		float rotationSpeed = 2.5f;
		for (int i = 0; i < 3; i++) {
			float angleOffset = i * 120f;
			float angle = totalTick * rotationSpeed + angleOffset;
			float beaconX = (float) Math.cos(Math.toRadians(angle)) * radius;
			float beaconZ = (float) Math.sin(Math.toRadians(angle)) * radius;
			float beaconY = state.boundingBoxHeight * 0.1f;
			pose.pushPose();
			pose.translate(beaconX, beaconY, beaconZ);
			pose.scale(beaconScale, beaconScale, beaconScale);
			pose.mulPose(Axis.YP.rotationDegrees(-angle));
			beacon.submit(pose, col, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
			pose.popPose();
			pose.pushPose();
			pose.translate(beaconX, beaconY, beaconZ);
			float beamScale = 0.25f * entityScale;
			submitBeaconBeam(pose, col, beamScale, totalTick + i * 10, 0, 1024, color);
			pose.popPose();
		}

	}

	public static void submitBeaconBeam(
			PoseStack pose, SubmitNodeCollector col, float beamRadiusScale, float animationTime, int beamStart, float height, int color
	) {
		submitBeaconBeam(
				pose, col, BEAM_LOCATION, 1.0F, animationTime, beamStart, height, color, 0.2F * beamRadiusScale, 0.25F * beamRadiusScale
		);
	}

	public static void submitBeaconBeam(
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			Identifier beamLocation,
			float scale,
			float animationTime,
			int beamStart,
			float height,
			int color,
			float solidBeamRadius,
			float beamGlowRadius
	) {
		float beamEnd = beamStart + height;
		poseStack.pushPose();
		poseStack.translate(0.5, 0.0, 0.5);
		float scroll = height < 0 ? animationTime : -animationTime;
		float texVOff = Mth.frac(scroll * 0.2F - Mth.floor(scroll * 0.1F));
		poseStack.pushPose();
		{
			poseStack.mulPose(Axis.YP.rotationDegrees(animationTime * 2.25F - 45.0F));
			float wnx = 0.0F;
			float enz = 0.0F;
			float wsx = -solidBeamRadius;
			float wsz = 0.0F;
			float esx = 0.0F;
			float esz = -solidBeamRadius;
			float uu1 = 0.0F;
			float uu2 = 1.0F;
			float vv2 = -1.0F + texVOff;
			float vv1 = height * scale * (0.5F / solidBeamRadius) + vv2;
			submitNodeCollector.submitCustomGeometry(
					poseStack,
					RenderTypes.beaconBeam(beamLocation, false),
					(pose, buffer) -> renderPart(
							pose, buffer, color, beamStart, beamEnd, 0.0F, solidBeamRadius, solidBeamRadius, 0.0F, wsx, 0.0F, 0.0F, esz, 0.0F, 1.0F, vv1, vv2
					)
			);
		}
		poseStack.popPose();
		float wnx = -beamGlowRadius;
		float wnz = -beamGlowRadius;
		float enz = -beamGlowRadius;
		float wsx = -beamGlowRadius;
		float uu1 = 0.0F;
		float uu2 = 1.0F;
		float vv2 = -1.0F + texVOff;
		float vv1 = height * scale + vv2;
		submitNodeCollector.submitCustomGeometry(
				poseStack,
				RenderTypes.beaconBeam(beamLocation, true),
				(pose, buffer) -> renderPart(
						pose,
						buffer,
						ARGB.color(32, color),
						beamStart,
						beamEnd,
						wnx,
						wnz,
						beamGlowRadius,
						enz,
						wsx,
						beamGlowRadius,
						beamGlowRadius,
						beamGlowRadius,
						0.0F,
						1.0F,
						vv1,
						vv2
				)
		);
		poseStack.popPose();
	}

	private static void renderPart(
			PoseStack.Pose pose,
			VertexConsumer builder,
			int color,
			float beamStart,
			float beamEnd,
			float wnx,
			float wnz,
			float enx,
			float enz,
			float wsx,
			float wsz,
			float esx,
			float esz,
			float uu1,
			float uu2,
			float vv1,
			float vv2
	) {
		renderQuad(pose, builder, color, beamStart, beamEnd, wnx, wnz, enx, enz, uu1, uu2, vv1, vv2);
		renderQuad(pose, builder, color, beamStart, beamEnd, esx, esz, wsx, wsz, uu1, uu2, vv1, vv2);
		renderQuad(pose, builder, color, beamStart, beamEnd, enx, enz, esx, esz, uu1, uu2, vv1, vv2);
		renderQuad(pose, builder, color, beamStart, beamEnd, wsx, wsz, wnx, wnz, uu1, uu2, vv1, vv2);
	}

	private static void renderQuad(
			PoseStack.Pose pose,
			VertexConsumer builder,
			int color,
			float beamStart,
			float beamEnd,
			float wnx,
			float wnz,
			float enx,
			float enz,
			float uu1,
			float uu2,
			float vv1,
			float vv2
	) {
		addVertex(pose, builder, color, beamEnd, wnx, wnz, uu2, vv1);
		addVertex(pose, builder, color, beamStart, wnx, wnz, uu2, vv2);
		addVertex(pose, builder, color, beamStart, enx, enz, uu1, vv2);
		addVertex(pose, builder, color, beamEnd, enx, enz, uu1, vv1);
	}

	private static void addVertex(PoseStack.Pose pose, VertexConsumer builder, int color, float y, float x, float z, float u, float v) {
		builder.addVertex(pose, x, y, z)
				.setColor(color)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(15728880)
				.setNormal(pose, 0.0F, 1.0F, 0.0F);
	}

}
