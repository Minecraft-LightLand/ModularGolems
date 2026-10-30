package dev.xkmc.modulargolems.compat.materials.eeeab;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

/**
 * Holder for the beam's render type, kept in a {@link RenderType} subclass only to reach the shard
 * constants: they are {@code protected} on {@code RenderStateShard}, so a plain renderer cannot see
 * them. Built once here instead of per beam per frame, since the texture is fixed and
 * {@link RenderType#create} re-runs the whole shard setup on every call.
 */
final class MGGuardianLaserRenderTypes extends RenderType {

	private MGGuardianLaserRenderTypes() {
		super("modulargolems_guardian_laser_types", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, () -> {
		}, () -> {
		});
	}

	static final RenderType BEAM = beam(new ResourceLocation("eeeabsmobs", "textures/entity/effect/guardian_laser.png"));

	private static RenderType beam(ResourceLocation texture) {
		return RenderType.create("modulargolems_glow_effect", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
				RenderType.CompositeState.builder()
						.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
						.setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
						.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
						.setCullState(NO_CULL)
						.setOverlayState(OVERLAY)
						.setWriteMaskState(COLOR_WRITE)
						.createCompositeState(false));
	}

}

/**
 * Port of EEEAB's {@code RenderAbsBeam} / {@code RenderGuardianLaser}, so the golem's beam reads the
 * same as the relic's: a quad at the muzzle, four quads crossed around the beam axis so it looks solid
 * from every angle, a quad at the impact point and one laid flat on the surface it stopped at.
 * <p>
 * The render state mirrors {@code ModRenderType.getGlowingEffect} shard for shard. Note that
 * {@code RENDERTYPE_BEACON_BEAM_SHADER} is vanilla's, not Citadel's, so this needs no cross-mod
 * reference at all. Do not substitute {@code getStrongGlowingEffect} (the emissive shader plus
 * {@code ADDITIVE_TRANSPARENCY}): it is deprecated upstream and blows the beam out, because four
 * additively blended quads stack their brightness instead of saturating.
 */
public class MGGuardianLaserRenderer extends EntityRenderer<MGGuardianLaserEntity> {

	private static final float TEXTURE_WIDTH = 256;
	private static final float TEXTURE_HEIGHT = 32;
	private static final ResourceLocation TEXTURE =
			new ResourceLocation("eeeabsmobs", "textures/entity/effect/guardian_laser.png");

	private float quadRadius;
	private float beamRadius;

	public MGGuardianLaserRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(MGGuardianLaserEntity entity) {
		return TEXTURE;
	}

	@Override
	public void render(MGGuardianLaserEntity laser, float entityYaw, float delta, PoseStack pose, MultiBufferSource source, int packedLight) {
		if (!laser.isAccumulating()) return;
		double collideX = laser.prevCollidePosX + (laser.collidePosX - laser.prevCollidePosX) * delta;
		double collideY = laser.prevCollidePosY + (laser.collidePosY - laser.prevCollidePosY) * delta;
		double collideZ = laser.prevCollidePosZ + (laser.collidePosZ - laser.prevCollidePosZ) * delta;
		double posX = laser.xo + (laser.getX() - laser.xo) * delta;
		double posY = laser.yo + (laser.getY() - laser.yo) * delta;
		double posZ = laser.zo + (laser.getZ() - laser.zo) * delta;
		float yaw = laser.preYaw + (laser.yaw - laser.preYaw) * delta;
		float pitch = laser.prePitch + (laser.pitch - laser.prePitch) * delta;
		float length = (float) Math.sqrt(Math.pow(collideX - posX, 2) + Math.pow(collideY - posY, 2) + Math.pow(collideZ - posZ, 2));

		// the relic's beam swells while it is still growing, then settles at the authored size
		if (laser.isGrowing()) {
			quadRadius = MGGuardianLaserEntity.QUAD_RADIUS * MGGuardianLaserEntity.ACCUMULATE_QUAD_SCALE;
			beamRadius = MGGuardianLaserEntity.BEAM_RADIUS * MGGuardianLaserEntity.ACCUMULATE_BEAM_SCALE;
		} else {
			quadRadius = MGGuardianLaserEntity.QUAD_RADIUS;
			beamRadius = MGGuardianLaserEntity.BEAM_RADIUS;
		}

		int frame = laser.animFrame(delta);
		VertexConsumer builder = source.getBuffer(MGGuardianLaserRenderTypes.BEAM);
		renderStart(frame, pose, builder, packedLight);
		renderBeam(length, 180F / (float) Math.PI * yaw, 180F / (float) Math.PI * pitch, frame, pose, builder, packedLight);
		pose.pushPose();
		pose.translate(collideX - posX, collideY - posY, collideZ - posZ);
		renderEnd(frame, laser.blockSide, pose, builder, packedLight);
		pose.popPose();
	}

	private void renderFlatQuad(int frame, PoseStack pose, VertexConsumer builder, int packedLight, boolean inGround) {
		float minU = 16F / TEXTURE_WIDTH * frame;
		float minV = 0;
		float maxU = minU + 16F / TEXTURE_WIDTH;
		float maxV = minV + 16F / TEXTURE_HEIGHT;
		// the guardian laser overrides the base class here and pulses the impact quad instead of using
		// a flat +0.2, which is most of what sells the beam as scorching rather than painted on
		float size = this.quadRadius + (inGround ? frame % 4 * 0.2F : 0);
		drawVertex(size, pose, builder, packedLight, size, -size, minU, minV, maxV, maxU);
	}

	private void renderStart(int frame, PoseStack pose, VertexConsumer builder, int packedLight) {
		pose.pushPose();
		Quaternionf camera = this.entityRenderDispatcher.cameraOrientation();
		pose.mulPose(camera);
		renderFlatQuad(frame, pose, builder, packedLight, false);
		pose.popPose();
	}

	private void renderEnd(int frame, Direction side, PoseStack pose, VertexConsumer builder, int packedLight) {
		pose.pushPose();
		Quaternionf camera = this.entityRenderDispatcher.cameraOrientation();
		pose.mulPose(camera);
		renderFlatQuad(frame, pose, builder, packedLight, false);
		pose.popPose();
		if (side == null) return;
		pose.pushPose();
		Quaternionf rotation = side.getRotation();
		rotation.mul(Axis.XP.rotationDegrees(90F));
		pose.mulPose(rotation);
		pose.translate(0, 0, -0.01f);
		renderFlatQuad(frame, pose, builder, packedLight, true);
		pose.popPose();
	}

	private void renderBeam(float length, float yaw, float pitch, int frame, PoseStack pose, VertexConsumer builder, int packedLight) {
		pose.pushPose();
		pose.mulPose(Axis.XP.rotationDegrees(90F));
		pose.mulPose(Axis.ZP.rotationDegrees(yaw - 90F));
		pose.mulPose(Axis.XP.rotationDegrees(-pitch));
		// four quads spaced a quarter turn apart around the beam axis: that is what keeps the beam
		// looking solid instead of like a flat billboard when viewed edge on
		for (int i = 0; i < 4; i++) {
			pose.pushPose();
			float cameraXRot = Minecraft.getInstance().gameRenderer.getMainCamera().getXRot();
			float angle = switch (i) {
				case 0 -> cameraXRot + 90F;
				case 1 -> -cameraXRot - 90F;
				case 2 -> -cameraXRot + 180F;
				default -> -cameraXRot - 180F;
			};
			pose.mulPose(Axis.YP.rotationDegrees(angle));
			drawBeam(length, frame, pose, builder, packedLight);
			pose.popPose();
		}
		pose.popPose();
	}

	private void drawBeam(float length, int frame, PoseStack pose, VertexConsumer builder, int packedLight) {
		float minU = 0;
		float minV = 16 / TEXTURE_HEIGHT + 1 / TEXTURE_HEIGHT * frame;
		float maxU = minU + 20 / TEXTURE_WIDTH;
		float maxV = minV + 1 / TEXTURE_HEIGHT;
		drawVertex(length, pose, builder, packedLight, this.beamRadius, 0, minU, minV, maxV, maxU);
	}

	private void drawVertex(float length, PoseStack pose, VertexConsumer builder, int packedLight, float size, float offset, float minU, float minV, float maxV, float maxU) {
		PoseStack.Pose entry = pose.last();
		Matrix4f matrix = entry.pose();
		Matrix3f normal = entry.normal();
		drawVertex(matrix, normal, builder, -size, offset, 0, minU, minV, packedLight);
		drawVertex(matrix, normal, builder, -size, length, 0, minU, maxV, packedLight);
		drawVertex(matrix, normal, builder, size, length, 0, maxU, maxV, packedLight);
		drawVertex(matrix, normal, builder, size, offset, 0, maxU, minV, packedLight);
	}

	private void drawVertex(Matrix4f matrix, Matrix3f normal, VertexConsumer builder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, int packedLight) {
		builder.vertex(matrix, offsetX, offsetY, offsetZ).color(1F, 1F, 1F, 1F).uv(textureX, textureY)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
	}

	@Override
	public boolean shouldRender(MGGuardianLaserEntity entity, net.minecraft.client.renderer.culling.Frustum frustum, double x, double y, double z) {
		return true;
	}

}