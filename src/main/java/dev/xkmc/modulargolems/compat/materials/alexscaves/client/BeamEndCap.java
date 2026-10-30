package dev.xkmc.modulargolems.compat.materials.alexscaves.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.fml.ModList;

/**
 * Draws the animated impact cap at the beam endpoint.
 * <p>
 * The cap model is built with Citadel's model API. Citadel ships with Cataclysm rather than with
 * Alex's Caves, so the model class is only touched once Citadel is known to be present, keeping the
 * beam usable without it.
 */
final class BeamEndCap {

	private static final float END_REACH = 1.5F;
	private static final int END_FRAME_COUNT = 3;
	private static final int PACKED_LIGHT = 240;

	private static boolean resolved;
	private static boolean available;
	private static Object model;

	private BeamEndCap() {
	}

	static void render(PoseStack stack, MultiBufferSource source, float width, float length, float age) {
		if (!resolve()) return;
		int frame = (int) age / 2 % END_FRAME_COUNT;
		VertexConsumer consumer = source.getBuffer(AtomicBeamRenderTypes.endCap(frame));
		stack.pushPose();
		stack.translate(0.0F, 0.0F, length - END_REACH);
		stack.mulPose(Axis.ZP.rotationDegrees(45.0F));
		stack.mulPose(Axis.XP.rotationDegrees(90.0F));
		stack.scale(width, width, width);
		AtomicBeamEndModel endModel = (AtomicBeamEndModel) model;
		endModel.resetToDefaultPose();
		endModel.renderToBuffer(stack, consumer, PACKED_LIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		stack.popPose();
	}

	private static boolean resolve() {
		if (!resolved) {
			resolved = true;
			available = ModList.get().isLoaded("citadel");
			if (available) model = new AtomicBeamEndModel();
		}
		return available;
	}

}
