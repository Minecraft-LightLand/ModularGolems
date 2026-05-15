package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderer;
import dev.xkmc.modulargolems.content.entity.render.GolemBannerLayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class MetalGolemRenderer extends AbstractGolemRenderer<MetalGolemEntity, MetalGolemRenderState, MetalGolemPartType, MetalGolemModel> {

	public MetalGolemRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new MetalGolemModel(ctx.bakeLayer(GolemEquipmentModels.METALGOLEM)), 0.7F, MetalGolemPartType::values);
		this.addLayer(new MetalGolemCrackinessLayer(this));
		this.addLayer(new GolemEquipmentRenderer(this, ctx));
		this.addLayer(new GolemBannerLayer<>(this));
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
	public MetalGolemRenderState createRenderState() {
		return new MetalGolemRenderState();
	}

	@Override
	public void extractRenderState(MetalGolemEntity entity, MetalGolemRenderState state, float pt) {
		super.extractRenderState(entity, state, pt);
		HumanoidMobRenderer.extractHumanoidRenderState(entity, state, pt, this.itemModelResolver);
		state.update(entity, pt, itemModelResolver);
	}

	@Override
	public void submit(MetalGolemRenderState state, PoseStack pose, SubmitNodeCollector col, CameraRenderState cam) {
		super.submit(state, pose, col, cam);
		BeaconRenderer.renderGolemBeacon(state, pose, col, cam);
	}

}