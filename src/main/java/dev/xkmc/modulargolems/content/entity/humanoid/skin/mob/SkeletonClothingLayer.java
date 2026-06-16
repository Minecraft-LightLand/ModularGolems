package dev.xkmc.modulargolems.content.entity.humanoid.skin.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class SkeletonClothingLayer extends RenderLayer<HumanoidGolemEntity, HumanoidGolemModel> implements IMobCloth {

	private final HumanoidGolemModel model;
	private final ResourceLocation tex;

	public SkeletonClothingLayer(RenderLayerParent<HumanoidGolemEntity, HumanoidGolemModel> p_174544_, HumanoidGolemModel p_174545_, ResourceLocation tex) {
		super(p_174544_);
		this.model = p_174545_;
		this.tex = tex;
	}

	@Override
	public void renderHead(PoseStack pose, MultiBufferSource.BufferSource bufferSource, int light, int overlay) {
		model.head.resetPose();
		model.head.render(pose, bufferSource.getBuffer(RenderType.entityCutoutNoCull(tex)), light, overlay);
	}

	public void render(PoseStack p_117553_, MultiBufferSource p_117554_, int p_117555_, HumanoidGolemEntity p_117556_, float p_117557_, float p_117558_, float p_117559_, float p_117560_, float p_117561_, float p_117562_) {
		coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, tex, p_117553_, p_117554_, p_117555_, p_117556_, p_117557_, p_117558_, p_117560_, p_117561_, p_117562_, p_117559_, -1);
	}

}