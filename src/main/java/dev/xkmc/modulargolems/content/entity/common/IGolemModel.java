package dev.xkmc.modulargolems.content.entity.common;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface IGolemModel<T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>, M extends EntityModel<T> & IGolemModel<T, P, M>> {

	default M getThis() {
		return Wrappers.cast(this);
	}

	default boolean isTranslucent() {
		return false;
	}

	default RenderType renderType(P part, ResourceLocation texture) {
		if (isTranslucent()) return RenderType.entityTranslucent(texture);
		return getThis().renderType(texture);
	}

	void renderToBufferInternal(P type, PoseStack stack, VertexConsumer consumer, int i, int j, float f1, float f2, float f3, float f4);

	/**
	 * 返回模型根部件。名字不能叫 root()：会与 EntityModel/HierarchicalModel 的 root()
	 * 产生 remap 冲突，正式环境接口方法被映射成 m_142109_，调用方却仍是 root → NoSuchMethodError。
	 */
	@Nullable
	default ModelPart getRootPart() {
		return this instanceof HierarchicalModel<?> model ? model.root() : null;
	}

	ResourceLocation getTextureLocationInternal(ResourceLocation rl);

}