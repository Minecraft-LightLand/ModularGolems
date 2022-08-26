package dev.xkmc.modulargolems.content.entity.humanoid;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.*;

public class HumanoidGolemRenderer extends AbstractGolemRenderer<HumanoidGolemEntity, HumaniodGolemPartType, HumanoidGolemModel> {

	public HumanoidGolemRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new HumanoidGolemModel(ctx.bakeLayer(ModelLayers.PLAYER)), 0.5f, HumaniodGolemPartType::values);
		this.addLayer(new HumanoidArmorLayer<>(this,
				new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
				new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
		this.addLayer(new CustomHeadLayer<>(this, ctx.getModelSet(), 1, 1, 1, ctx.getItemInHandRenderer()));
		this.addLayer(new ElytraLayer<>(this, ctx.getModelSet()));
		this.addLayer(new ItemInHandLayer<>(this, ctx.getItemInHandRenderer()));

	}

}
