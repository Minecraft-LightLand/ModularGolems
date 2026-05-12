package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderer;
import dev.xkmc.modulargolems.content.entity.render.GolemBannerLayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

public class MetalGolemRenderer extends AbstractGolemRenderer<MetalGolemEntity, MetalGolemRenderState, MetalGolemPartType, MetalGolemModel> {

	protected static void transform(PoseStack stack, ItemDisplayContext transform, @Nullable MetalGolemPartType part) {
		switch (transform) {
			case GUI:
			case FIRST_PERSON_LEFT_HAND:
			case FIRST_PERSON_RIGHT_HAND:
				break;
			case THIRD_PERSON_LEFT_HAND:
			case THIRD_PERSON_RIGHT_HAND: {
				stack.translate(0.25, 0.4, 0.5);
				float size = 0.625f;
				stack.scale(size, size, size);
				break;
			}
			case GROUND: {
				stack.translate(0.25, 0, 0.5);
				float size = 0.625f;
				stack.scale(size, size, size);
				break;
			}
			case NONE:
			case HEAD:
			case FIXED: {
				stack.translate(0.5, 0.5, 0.5);
				float size = 0.45f;
				stack.scale(size, -size, size);
				stack.translate(0, -0.15, 0);
				return;
			}
			default:
				stack.translate(0, 0, 0.5);
				break;
		}
		stack.mulPose(Axis.ZP.rotationDegrees(135));
		stack.mulPose(Axis.YP.rotationDegrees(-155));
		if (part == null) {
			float size = 0.375f;
			stack.scale(size, size, size);
			stack.translate(0, -2.2, 0);
		} else if (part == MetalGolemPartType.BODY) {
			float size = 0.525f;
			stack.scale(size, size, size);
			stack.translate(0, -1, 0);
		} else if (part == MetalGolemPartType.LEG) {
			float size = 0.6f;
			stack.scale(size, size, size);
			stack.translate(0, -2.2, 0);
		} else if (part == MetalGolemPartType.LEFT) {
			float size = 0.55f;
			stack.scale(size, size, size);
			stack.translate(-0.7, -1.7, 0);
		}
	}

	public MetalGolemRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new MetalGolemModel(ctx.bakeLayer(GolemEquipmentModels.METALGOLEM)), 0.7F, MetalGolemPartType::values);
		this.addLayer(new MetalGolemCrackinessLayer(this));
		this.addLayer(new GolemEquipmentRenderer(this, ctx));
		this.addLayer(new GolemBannerLayer<>(this, ctx.getItemInHandRenderer()));
	}

	@Override
	protected void setupRotations(MetalGolemRenderState state, PoseStack poseStack, float bodyRot, float entityScale) {
		super.setupRotations(state, poseStack, bodyRot, entityScale);
		if (!(state.walkAnimationSpeed < 0.01)) {
			float p = 13.0F;
			float wp = state.walkAnimationPos + 6.0F;
			float triangleWave = (Math.abs(wp % 13.0F - 6.5F) - 3.25F) / 3.25F;
			poseStack.mulPose(Axis.ZP.rotationDegrees(6.5F * triangleWave));
		}
	}

	@Override
	public void submit(MetalGolemRenderState state, PoseStack pose, SubmitNodeCollector col, CameraRenderState cam) {
		super.submit(state, pose, col, cam);
		BeaconRenderer.renderGolemBeacon(state, pose, col, cam);
	}

}