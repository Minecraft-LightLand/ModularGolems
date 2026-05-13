package dev.xkmc.modulargolems.content.item.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.render.IGolemModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public record GolemRenderHandle(
		PoseStack pose, SubmitNodeCollector col,
		int light, int overlay, boolean hasFoil, int outlineCol
) {

	public void render(RenderType type, ModelPart part) {
		col.submitModelPart(part, pose, type, light, overlay, null, false, hasFoil, -1, null, outlineCol);
	}

	public <P extends IGolemPart<P>, M extends EntityModel<?> & IGolemModel<?, ?, P, M>>
	void renderPart(M model, Identifier id, P part) {
		var normal = model.renderType(model.getTextureLocationInternal(id));
		model.renderToBufferInternal(part, p -> render(normal, p));
		var etex = model.getTextureLocationInternal(id.withSuffix("_emissive"));
		if (ModelOverrides.isValid(etex)) {
			var emi = model.renderType(etex);
			model.renderToBufferInternal(part, p -> render(emi, p));
		}
	}

}
