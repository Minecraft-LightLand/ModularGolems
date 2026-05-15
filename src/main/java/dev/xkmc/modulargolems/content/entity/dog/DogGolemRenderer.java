package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderer;
import dev.xkmc.modulargolems.content.entity.render.GolemBannerLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DogGolemRenderer extends AbstractGolemRenderer<DogGolemEntity, DogGolemRenderState, DogGolemPartType, DogGolemModel> {

	public DogGolemRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new DogGolemModel(ctx.bakeLayer(GolemEquipmentModels.DOGGOLEM)), 1F, DogGolemPartType::values);
		addLayer(new DogArmorLayer(this, ctx.getModelSet(), ctx.getEquipmentRenderer()));
		addLayer(new GolemBannerLayer<>(this));
	}

	@Override
	public DogGolemRenderState createRenderState() {
		return new DogGolemRenderState();
	}

	@Override
	public void extractRenderState(DogGolemEntity entity, DogGolemRenderState state, float pt) {
		super.extractRenderState(entity, state, pt);
		state.update(entity, pt, itemModelResolver);
	}

}