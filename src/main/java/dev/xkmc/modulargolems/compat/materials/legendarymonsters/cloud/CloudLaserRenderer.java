package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.miauczel.legendary_monsters.LegendaryMonsters;
import net.miauczel.legendary_monsters.Particle.custom.LightningBoltData;
import net.miauczel.legendary_monsters.Particle.custom.LightningRender;
import net.miauczel.legendary_monsters.client.ColorPack;
import net.miauczel.legendary_monsters.entity.client.Render.LMRenderTypes;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 天穹激光的渲染，逐段照搬新版 Legendary Monsters 的 {@code EnergyBeamRender2d}：
 * 起手贴图 + 落点贴图（撞到方块时再贴一片平面 quad）＋ 沿光束的两道程序化闪电
 * （粗的白、细的粉），最后一道才是新版 Boss 激光最显眼的部分。
 * <p>
 * 贴图、发光 RenderType（{@code LMRenderTypes}）和闪电几何（{@code LightningBoltData} /
 * {@code LightningRender}）都直接引用 LM 的类，和原子吐息引用 Alex's Caves 是同一套做法。
 */
public class CloudLaserRenderer extends EntityRenderer<CloudLaserEntity> {

	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath(LegendaryMonsters.MOD_ID, "textures/entity/energy_beam.png");
	/** 出场动画时长（tick），对应 LM 的 {@code ControlledAnim(3)}。 */
	private static final int APPEAR_TICKS = 3;

	private static final ColorPack BIG_BEAM_COLOR = new ColorPack(255.0F, 255.0F, 255.0F, 255.0F);
	private static final ColorPack SMALL_BEAM_COLOR = new ColorPack(255.0F, 156.0F, 246.0F, 255.0F);

	private final Map<UUID, LightningRender> lightningRenderMap = new HashMap<>();

	public CloudLaserRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRender(CloudLaserEntity e, Frustum frustum, double x, double y, double z) {
		return true;
	}

	@Override
	public ResourceLocation getTextureLocation(CloudLaserEntity e) {
		return TEXTURE;
	}

	@Override
	public void render(CloudLaserEntity e, float yRot, float delta, PoseStack pose, MultiBufferSource source, int light) {
		double endX = e.prevEndX + (e.endX - e.prevEndX) * delta;
		double endY = e.prevEndY + (e.endY - e.prevEndY) * delta;
		double endZ = e.prevEndZ + (e.endZ - e.prevEndZ) * delta;
		Vec3 pos = e.getPosition(delta);
		int frame = Mth.floor((Math.min(APPEAR_TICKS, e.tickCount + delta) - 1) * 2.0F);
		if (frame < 0) frame = 6;

		VertexConsumer buffer = source.getBuffer(LMRenderTypes.getGlowingEffect(TEXTURE));
		renderStart(frame, pose, buffer, light);
		pose.pushPose();
		pose.translate(endX - pos.x, endY - pos.y, endZ - pos.z);
		renderEnd(frame, e.blockSide, pose, buffer, light);
		pose.popPose();
		renderLightningBeam(e, delta, pose, source);
	}

	/** 新版渲染器的主体：粗白 + 细粉两道闪电填满整条光束（LM 只在 tickCount > 20 之后画）。 */
	private void renderLightningBeam(CloudLaserEntity e, float partialTick, PoseStack pose, MultiBufferSource source) {
		if (e.tickCount <= 20) return;
		Vec3 start = e.getPosition(partialTick);
		Vec3 end = new Vec3(
				e.prevEndX + (e.endX - e.prevEndX) * partialTick,
				e.prevEndY + (e.endY - e.prevEndY) * partialTick,
				e.prevEndZ + (e.endZ - e.prevEndZ) * partialTick);
		pose.pushPose();
		pose.translate(-start.x, -start.y, -start.z);
		LightningBoltData.BoltRenderInfo bigInfo = new LightningBoltData.BoltRenderInfo(1.0F, 0.1F, 0.1F, 0.25F,
				new Vector4f(BIG_BEAM_COLOR.r / 255.0F, BIG_BEAM_COLOR.g / 255.0F, BIG_BEAM_COLOR.b / 255.0F, 0.7F), 0.6F);
		LightningBoltData bigBolt = new LightningBoltData(bigInfo, start, end, 3)
				.size(0.5F).lifespan(1)
				.spawn(LightningBoltData.SpawnFunction.NO_DELAY)
				.fade(LightningBoltData.FadeFunction.NONE);
		LightningBoltData.BoltRenderInfo smallInfo = new LightningBoltData.BoltRenderInfo(0.25F, 0.1F, 0.1F, 0.25F,
				new Vector4f(SMALL_BEAM_COLOR.r / 255.0F, SMALL_BEAM_COLOR.g / 255.0F, SMALL_BEAM_COLOR.b / 255.0F, 0.7F), 0.35F);
		LightningBoltData smallBolt = new LightningBoltData(smallInfo, start, end, 6)
				.size(0.2F).lifespan(1)
				.spawn(LightningBoltData.SpawnFunction.NO_DELAY)
				.fade(LightningBoltData.FadeFunction.NONE);
		LightningRender lr = lightningRenderMap.computeIfAbsent(e.getUUID(), k -> new LightningRender());
		lr.update(this, bigBolt, partialTick);
		lr.update(this, smallBolt, partialTick);
		lr.render(partialTick, pose, source);
		pose.popPose();
		if (e.isRemoved()) lightningRenderMap.remove(e.getUUID());
	}

	private void renderFlatQuad(int frame, PoseStack pose, VertexConsumer buffer, int light) {
		float minU = 0.0625F * (float) frame;
		float minV = 0.0F;
		float maxU = minU + 0.0625F;
		float maxV = minV + 0.5F;
		PoseStack.Pose entry = pose.last();
		drawVertex(entry, buffer, -1.3F, -1.3F, 0.0F, minU, minV, light);
		drawVertex(entry, buffer, -1.3F, 1.3F, 0.0F, minU, maxV, light);
		drawVertex(entry, buffer, 1.3F, 1.3F, 0.0F, maxU, maxV, light);
		drawVertex(entry, buffer, 1.3F, -1.3F, 0.0F, maxU, minV, light);
	}

	private void renderStart(int frame, PoseStack pose, VertexConsumer buffer, int light) {
		pose.pushPose();
		pose.mulPose(entityRenderDispatcher.cameraOrientation());
		renderFlatQuad(frame, pose, buffer, light);
		pose.popPose();
	}

	private void renderEnd(int frame, @Nullable Direction side, PoseStack pose, VertexConsumer buffer, int light) {
		pose.pushPose();
		pose.mulPose(entityRenderDispatcher.cameraOrientation());
		renderFlatQuad(frame, pose, buffer, light);
		pose.popPose();
		if (side != null) {
			pose.pushPose();
			Quaternionf quat = side.getRotation();
			quat.mul(MathUtils.quatFromRotationXYZ(90.0F, 0.0F, 0.0F, true));
			pose.mulPose(quat);
			pose.translate(0.0F, 0.0F, -0.01F);
			renderFlatQuad(frame, pose, buffer, light);
			pose.popPose();
		}
	}

	private void drawVertex(PoseStack.Pose entry, VertexConsumer buffer,
			float x, float y, float z, float u, float v, int light) {
		buffer.addVertex(entry, x, y, z).setColor(1.0F, 1.0F, 1.0F, 1.0F)
				.setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light)
				.setNormal(entry, 0.0F, 1.0F, 0.0F);
	}

}
