package dev.xkmc.modulargolems.content.entity.humanoid.skin.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class StrayClothingLayer extends RenderLayer<HumanoidGolemEntity, HumanoidGolemModel> {

	private static final ResourceLocation STRAY_CLOTHES_LOCATION =  ResourceLocation.withDefaultNamespace("textures/entity/skeleton/stray_overlay.png");
	private final HumanoidGolemModel layerModel;

	public StrayClothingLayer(RenderLayerParent<HumanoidGolemEntity, HumanoidGolemModel> p_174544_, HumanoidGolemModel p_174545_) {
		super(p_174544_);
		this.layerModel = p_174545_;
	}

	public void render(PoseStack p_117553_, MultiBufferSource p_117554_, int p_117555_, HumanoidGolemEntity p_117556_, float p_117557_, float p_117558_, float p_117559_, float p_117560_, float p_117561_, float p_117562_) {
		coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, STRAY_CLOTHES_LOCATION, p_117553_, p_117554_, p_117555_, p_117556_, p_117557_, p_117558_, p_117560_, p_117561_, p_117562_, p_117559_, -1);
	}

}