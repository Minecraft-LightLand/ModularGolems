package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DogGolemRenderer extends AbstractGolemRenderer<DogGolemEntity, DogGolemPartType, DogGolemModel> {

    public DogGolemRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DogGolemModel(ctx.bakeLayer(ModelLayers.WOLF)), 0.5F, DogGolemPartType::values);
    }

    protected float getBob(DogGolemEntity dog, float pPartialTicks) {
        return dog.getTailAngle();
    }

}