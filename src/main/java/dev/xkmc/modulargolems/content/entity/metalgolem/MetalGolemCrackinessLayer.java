package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Crackiness;

import java.util.Map;

public class MetalGolemCrackinessLayer extends RenderLayer<MetalGolemRenderState, MetalGolemModel> {
	private static final Map<Crackiness.Level, Identifier> TEXTURES = ImmutableMap.of(
			Crackiness.Level.LOW, Identifier.withDefaultNamespace("textures/entity/iron_golem/iron_golem_crackiness_low.png"),
			Crackiness.Level.MEDIUM, Identifier.withDefaultNamespace("textures/entity/iron_golem/iron_golem_crackiness_medium.png"),
			Crackiness.Level.HIGH, Identifier.withDefaultNamespace("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

	public MetalGolemCrackinessLayer(RenderLayerParent<MetalGolemRenderState, MetalGolemModel> p_117135_) {
		super(p_117135_);
	}

	@Override
	public void submit(PoseStack stack, SubmitNodeCollector col, int light, MetalGolemRenderState state, float yRot, float xRot) {
		if (!state.isInvisible) {
			var crack = state.crackiness;
			if (crack != Crackiness.Level.NONE) {
				Identifier id = TEXTURES.get(crack);
				renderColoredCutoutModel(this.getParentModel(), id, stack, col, light, state, -1, 1);
			}
		}
	}
}