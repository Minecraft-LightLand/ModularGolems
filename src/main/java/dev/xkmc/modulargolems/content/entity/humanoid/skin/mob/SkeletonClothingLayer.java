package dev.xkmc.modulargolems.content.entity.humanoid.skin.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemModel;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;

public class SkeletonClothingLayer extends RenderLayer<HumanoidGolemRenderState, HumanoidGolemModel> implements IMobCloth {

	private final HumanoidGolemModel model;
	private final Identifier tex;

	public SkeletonClothingLayer(RenderLayerParent<HumanoidGolemRenderState, HumanoidGolemModel> p_174544_, HumanoidGolemModel p_174545_, Identifier tex) {
		super(p_174544_);
		this.model = p_174545_;
		this.tex = tex;
	}

	@Override
	public void renderHead(HumanoidGolemRenderState entity, PoseStack stack, SubmitNodeCollector source, CameraRenderState cam) {
		var rt = RenderTypes.entityCutout(tex);
		source.submitModel(model, entity, stack, rt,
				LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
				-1, null, 0, null);
	}

	public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, HumanoidGolemRenderState state, float yRot, float xRot) {
		coloredCutoutModelCopyLayerRender(this.model, this.tex, poseStack, submitNodeCollector, lightCoords, state, -1, 1);
	}

}