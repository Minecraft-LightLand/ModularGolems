package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
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

	protected void setupRotations(MetalGolemEntity entity, PoseStack stack, float v1, float v2, float v3) {
		super.setupRotations(entity, stack, v1, v2, v3);
		if (!((double) entity.animationSpeed < 0.01D)) {
			float f = 13.0F;
			float f1 = entity.animationPosition - entity.animationSpeed * (1.0F - v3) + 6.0F;
			float f2 = (Math.abs(f1 % f - 6.5F) - 3.25F) / 3.25F;
			stack.mulPose(Vector3f.ZP.rotationDegrees(6.5F * f2));
		}
	}

	protected ResourceLocation getTextureLocationInternal(MetalGolemPartType type, MetalGolemEntity entity) {
		var material = entity.getMaterials().get(type.ordinal());
		String id = material.id().getNamespace();
		String mat = material.id().getPath();
		return new ResourceLocation(id, "textures/entity/metal_golem/" + mat + ".png");
	}

}