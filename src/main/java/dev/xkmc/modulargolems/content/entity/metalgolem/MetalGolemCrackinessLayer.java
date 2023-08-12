package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class MetalGolemCrackinessLayer extends RenderLayer<MetalGolemEntity, MetalGolemModel<LivingEntity>> {
	private static final Map<IronGolem.Crackiness, ResourceLocation> TEXTURES = ImmutableMap.of(
			IronGolem.Crackiness.LOW, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"),
			IronGolem.Crackiness.MEDIUM, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"),
			IronGolem.Crackiness.HIGH, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

	public MetalGolemCrackinessLayer(RenderLayerParent<MetalGolemEntity, MetalGolemModel<LivingEntity>> p_117135_) {
		super(p_117135_);
	}

	public void render(PoseStack stack, MultiBufferSource source, int i, MetalGolemEntity entity, float f1, float f2, float f3, float f4, float f5, float f7) {
		if (!entity.isInvisible()) {
			IronGolem.Crackiness crack = entity.getCrackiness();
			if (crack != IronGolem.Crackiness.NONE) {
				ResourceLocation resourcelocation = TEXTURES.get(crack);
				renderColoredCutoutModel(this.getParentModel(), resourcelocation, stack, source, i, entity, 1.0F, 1.0F, 1.0F);
			}
		}
	}
}