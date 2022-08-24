package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MetalGolemRenderer extends AbstractGolemRenderer<MetalGolemEntity, MetalGolemPartType, MetalGolemModel> {

	public MetalGolemRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new MetalGolemModel(ctx.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F, MetalGolemPartType::values);
		this.addLayer(new MetalGolemCrackinessLayer(this));
	}

	protected ResourceLocation getTextureLocationInternal(MetalGolemPartType type, MetalGolemEntity entity) {
		var material = entity.getMaterials().get(type.ordinal());
		String id = material.id().getNamespace();
		String mat = material.id().getPath();
		return new ResourceLocation(id, "textures/entity/metal_golem/" + mat + ".png");
	}

}