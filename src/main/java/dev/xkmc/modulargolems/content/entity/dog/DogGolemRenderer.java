package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderer;
import dev.xkmc.modulargolems.content.entity.render.GolemBannerLayer;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DogGolemRenderer extends AbstractGolemRenderer<DogGolemEntity, DogGolemRenderState, DogGolemPartType, DogGolemModel> {

	public DogGolemRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, GolemTypes.TYPE_DOG.get(), new DogGolemModel(ctx.getModelSet()), 1F);
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