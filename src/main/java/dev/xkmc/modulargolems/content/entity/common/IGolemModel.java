package dev.xkmc.modulargolems.content.entity.common;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public interface IGolemModel<T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>, M extends EntityModel<T> & IGolemModel<T, P, M>> {

	default M getThis() {
		return Wrappers.cast(this);
	}

	default RenderType renderType(P part, ResourceLocation texture) {
		return getThis().renderType(texture);
	}

	void renderToBufferInternal(P type, PoseStack stack, VertexConsumer consumer, int i, int j, float f1, float f2, float f3, float f4);

	ResourceLocation getTextureLocationInternal(ResourceLocation rl);

}