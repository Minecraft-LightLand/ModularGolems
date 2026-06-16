package dev.xkmc.modulargolems.content.entity.humanoid.skin.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class DrownedOuterLayer extends RenderLayer<HumanoidGolemEntity, HumanoidGolemModel> implements IMobCloth {

	private static final ResourceLocation DROWNED_OUTER_LAYER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/drowned_outer_layer.png");
	private final HumanoidGolemModel model;

	public DrownedOuterLayer(RenderLayerParent<HumanoidGolemEntity, HumanoidGolemModel> parent, HumanoidGolemModel model) {
		super(parent);
		this.model = model;
	}

	@Override
	public void renderHead(PoseStack pose, MultiBufferSource.BufferSource bufferSource, int light, int overlay) {
		model.head.resetPose();
		model.head.render(pose, bufferSource.getBuffer(RenderType.entityCutoutNoCull(DROWNED_OUTER_LAYER_LOCATION)), light, overlay);
	}

	public void render(PoseStack p_116924_, MultiBufferSource p_116925_, int p_116926_, HumanoidGolemEntity p_116927_, float p_116928_, float p_116929_, float p_116930_, float p_116931_, float p_116932_, float p_116933_) {
		coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, DROWNED_OUTER_LAYER_LOCATION, p_116924_, p_116925_, p_116926_, p_116927_, p_116928_, p_116929_, p_116931_, p_116932_, p_116933_, p_116930_, -1);
	}
}