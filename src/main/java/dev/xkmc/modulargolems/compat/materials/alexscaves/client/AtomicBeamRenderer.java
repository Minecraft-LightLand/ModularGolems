package dev.xkmc.modulargolems.compat.materials.alexscaves.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.impl.GolemBeamSource;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Renders the atomic breath beam body, replicating Alex's Caves' Tremorzilla beam.
 * <p>
 * The beam is hand built cylinder geometry, drawn as a four sided inner pass followed by an eight
 * sided outer pass that fades out towards the impact point, plus the animated end cap model.
 * Alex's Caves' beam textures are referenced by namespace instead of being copied.
 */
public final class AtomicBeamRenderer {

	private static final float OUTER_WIDTH_BONUS = 0.25F;
	private static final float SHAKE_AMPLITUDE = 0.075F;
	private static final float SHAKE_FREQUENCY = 4.0F;
	private static final float MIN_LENGTH = 1.0E-4F;
	private static final int PACKED_LIGHT = 240;

	private AtomicBeamRenderer() {
	}

	/**
	 * Renders the beam of a golem which is currently emitting one. Must run inside the entity render
	 * pose, so that the beam inherits the golem's body yaw and scale.
	 */
	public static void render(GolemBeamSource golem, PoseStack stack, MultiBufferSource source, float partialTicks) {
		if (!golem.isBeamActive()) return;
		float progress = golem.getBeamProgress(partialTicks);
		if (progress <= 0.0F) return;
		Vec3 end = golem.getBeamEnd(partialTicks);
		if (end == null) return;
		Vec3 mouth = golem.getBeamMouth(partialTicks);
		Vec3 direction = end.subtract(mouth);
		float length = (float) direction.length();
		if (length < MIN_LENGTH) return;
		float scale = scaleOf(golem);
		if (scale <= 0.0F) return;
		direction = direction.normalize();
		float xRot = (float) Math.acos(direction.y);
		float yRot = (float) Math.atan2(direction.z, direction.x);
		float age = tickCount(golem) + partialTicks;
		float width = progress * 1.5F;
		float shakeX = (float) Math.sin(age * SHAKE_FREQUENCY) * SHAKE_AMPLITUDE;
		float shakeY = (float) Math.sin(age * SHAKE_FREQUENCY + 1.2F) * SHAKE_AMPLITUDE;
		float shakeZ = (float) Math.sin(age * SHAKE_FREQUENCY + 2.4F) * SHAKE_AMPLITUDE;
		// the pose is already scaled by the golem scale, so world sized geometry has to be unscaled
		width /= scale;
		length /= scale;
		Vec3 mouthOffset = mouth.subtract(positionOf(golem));
		stack.pushPose();
		stack.translate(mouthOffset.x + shakeX, mouthOffset.y + shakeY, mouthOffset.z + shakeZ);
		stack.mulPose(Axis.YP.rotationDegrees((Mth.PI / 2F - yRot) * Mth.RAD_TO_DEG));
		stack.mulPose(Axis.XP.rotationDegrees((-(Mth.PI / 2F) + xRot) * Mth.RAD_TO_DEG));
		stack.mulPose(Axis.ZP.rotationDegrees(45.0F));
		// Two passes like TremorzillaRenderer: an opaque inner core and a wide outer shell that fades
		// out towards the impact point. Each is drawn a second time into Citadel's irradiated target,
		// which is what gives the beam its bloom; without Citadel only the plain passes run.
		renderBeamPass(stack, source, width, length, age, true, false);
		renderBeamPass(stack, source, width, length, age, false, false);
		if (AtomicBeamRenderTypes.glowAvailable()) {
			AtomicBeamRenderTypes.queueGlowEffect();
			renderBeamPass(stack, source, width, length, age, true, true);
			AtomicBeamRenderTypes.queueGlowEffect();
			renderBeamPass(stack, source, width, length, age, false, true);
		}
		BeamEndCap.render(stack, source, width, length, age);
		stack.popPose();
	}

	private static void renderBeamPass(PoseStack stack, MultiBufferSource source, float width, float length, float age, boolean inner, boolean glowPass) {
		int vertices;
		float speed;
		float startAlpha;
		float endAlpha;
		RenderType type;
		if (inner) {
			vertices = 4;
			type = glowPass ? AtomicBeamRenderTypes.INNER_GLOW : AtomicBeamRenderTypes.INNER;
			speed = 0.5F;
			startAlpha = 0.5F;
			endAlpha = 0.5F;
		} else {
			vertices = 8;
			type = glowPass ? AtomicBeamRenderTypes.OUTER_GLOW : AtomicBeamRenderTypes.OUTER;
			width += OUTER_WIDTH_BONUS;
			speed = 1.0F;
			startAlpha = 1.0F;
			endAlpha = 0.0F;
		}
		VertexConsumer consumer = source.getBuffer(type);
		Matrix4f matrix = stack.last().pose();
		Matrix3f normalMatrix = stack.last().normal();
		float v = age * -0.25F * speed;
		float vEnd = v + length * (inner ? 0.5F : 0.15F);
		float prevX = -width;
		float prevY = 0.0F;
		float prevU = 0.0F;
		for (int j = 0; j <= vertices; j++) {
			float x = Mth.cos((float) Math.PI + (float) j * ((float) Math.PI * 2F) / (float) vertices) * width;
			float y = Mth.sin((float) Math.PI + (float) j * ((float) Math.PI * 2F) / (float) vertices) * width;
			float nextU = (float) j + 1.0F;
			consumer.vertex(matrix, prevX * 0.55F, prevY * 0.55F, 0.0F).color(1.0F, 1.0F, 1.0F, startAlpha).uv(prevU, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(PACKED_LIGHT).normal(normalMatrix, 0.0F, -1.0F, 0.0F).endVertex();
			consumer.vertex(matrix, prevX, prevY, length).color(1.0F, 1.0F, 1.0F, endAlpha).uv(prevU, vEnd).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(PACKED_LIGHT).normal(normalMatrix, 0.0F, -1.0F, 0.0F).endVertex();
			consumer.vertex(matrix, x, y, length).color(1.0F, 1.0F, 1.0F, endAlpha).uv(nextU, vEnd).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(PACKED_LIGHT).normal(normalMatrix, 0.0F, -1.0F, 0.0F).endVertex();
			consumer.vertex(matrix, x * 0.55F, y * 0.55F, 0.0F).color(1.0F, 1.0F, 1.0F, startAlpha).uv(nextU, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(PACKED_LIGHT).normal(normalMatrix, 0.0F, -1.0F, 0.0F).endVertex();
			prevX = x;
			prevY = y;
			prevU = nextU;
		}
	}

	private static float scaleOf(GolemBeamSource golem) {
		return golem instanceof AbstractGolemEntity<?, ?> entity ? entity.getScale() : 1.0F;
	}

	private static int tickCount(GolemBeamSource golem) {
		return golem instanceof Entity entity ? entity.tickCount : 0;
	}

	private static Vec3 positionOf(GolemBeamSource golem) {
		return golem instanceof Entity entity ? entity.position() : Vec3.ZERO;
	}

}
