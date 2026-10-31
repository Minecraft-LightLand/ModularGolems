package dev.xkmc.modulargolems.content.entity.common;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class RiptideEffectLayer<
		T extends AbstractGolemEntity<T, P>,
		P extends IGolemPart<P>,
		M extends EntityModel<T> & IGolemModel<T, P, M>
		> extends RenderLayer<T, M> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/trident_riptide.png");

	private final ModelPart box;

	public RiptideEffectLayer(AbstractGolemRenderer<T, P, M> parent, EntityRendererProvider.Context ctx) {
		super(parent);
		this.box = ctx.getModelSet().bakeLayer(ModelLayers.PLAYER_SPIN_ATTACK).getChild("box");
	}

	/**
	 * 特效大小随体型缩放，以人形傀儡（bbHeight 1.8，与原版玩家一致）为基准 1.0。
	 */
	private float bodyScale(T e) {
		return e.getBbHeight() / 1.8F;
	}

	@Override
	public void render(
			PoseStack pose, MultiBufferSource buffer, int light, T e,
			float legAngle, float legSwing, float bob, float pTick, float yRot, float xRot
	) {
		if (!e.isAutoSpinAttack()) return;
		VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		if (e instanceof DogGolemEntity) {
			// 犬形：原版 3 层 box 是为「放平的人形」设计的（沿本地 Y 堆叠、绕本地 Y 转）。
			// 犬形身体水平、绕前进轴 roll，box 的环绕轴与之差 90°，先做 90° 校正对齐前进轴。
			for (int i = 0; i < 3; i++) {
				pose.pushPose();
				// 特效层原点是脚底，先把 box 抬到身体的旋转中心（与 AbstractGolemRenderer 犬形段的 0.5 + 0.7 对应）。
				pose.translate(0.0F, e.getBbHeight() * 0.9677778F, 0.0F);
				// 身体 roll 已给 box 贡献 +75°/tick；这里叠加 -55，使特效净转速 = +20°/tick（向左，与其他傀儡同向、同速）。
				// 以后调净速/方向改这里的 -55：目标净速 R 就填 -(75) + R 的反向补偿，即 填 (R - 75)。
				pose.mulPose(Axis.ZP.rotationDegrees((e.tickCount + bob) * -55.0F));
				pose.mulPose(Axis.XP.rotationDegrees(90.0F));
				float k = bodyScale(e);
				float s = 0.75F * i * k;
				pose.scale(s, s, s);
				pose.translate(0.0F, (-3.6F + 2.25F * i) * k, 0.0F);
				box.render(pose, vc, light, OverlayTexture.NO_OVERLAY);
				pose.popPose();
			}
			return;
		}
		// 人形与大型体型差异大，层偏移分开写（单位格，不乘体型系数）。
		boolean large = e.getBbHeight() > 2.0F;
		for (int i = 0; i < 3; i++) {
			pose.pushPose();
			pose.mulPose(Axis.YP.rotationDegrees(pTick * -(45 + i * 5)));
			float k = bodyScale(e);
			float s = 0.75F * i * k;
			pose.scale(s, s, s);
			if (large) {
				// 大型：起始偏移 + 间距
				pose.translate(0.0F, -1.0F + 0.85F * i, 0.0F);
			} else {
				// 人形：起始偏移 + 间距
				pose.translate(0.0F, -0.55F + 0.75F * i, 0.0F);
			}
			box.render(pose, vc, light, OverlayTexture.NO_OVERLAY);
			pose.popPose();
		}
	}

}
