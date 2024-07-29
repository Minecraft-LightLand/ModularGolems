package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Crackiness;

import java.util.Map;

public class MetalGolemCrackinessLayer extends RenderLayer<MetalGolemEntity, MetalGolemModel> {
	private static final Map<Crackiness.Level, ResourceLocation> TEXTURES = ImmutableMap.of(
			Crackiness.Level.LOW, ResourceLocation.withDefaultNamespace("textures/entity/iron_golem/iron_golem_crackiness_low.png"),
			Crackiness.Level.MEDIUM, ResourceLocation.withDefaultNamespace("textures/entity/iron_golem/iron_golem_crackiness_medium.png"),
			Crackiness.Level.HIGH, ResourceLocation.withDefaultNamespace("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

	public MetalGolemCrackinessLayer(RenderLayerParent<MetalGolemEntity, MetalGolemModel> p_117135_) {
		super(p_117135_);
	}

	public void render(PoseStack stack, MultiBufferSource source, int i, MetalGolemEntity entity, float f1, float f2, float f3, float f4, float f5, float f7) {
		if (!entity.isInvisible()) {
			var crack = entity.getCrackiness();
			if (crack != Crackiness.Level.NONE) {
				ResourceLocation resourcelocation = TEXTURES.get(crack);
				renderColoredCutoutModel(this.getParentModel(), resourcelocation, stack, source, i, entity, -1);
			}
		}
	}
}