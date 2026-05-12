package dev.xkmc.modulargolems.content.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class GolemDefaultLayer<
		E extends AbstractGolemEntity<E, P>,
		S extends LivingEntityRenderState & AbstractGolemRenderState<E, S, P>,
		P extends IGolemPart<P>,
		M extends EntityModel<S> & IGolemModel<E, S, P, M>
		> extends RenderLayer<S, M> {

	private final AbstractGolemRenderer<E, S, P, M> parent;

	public GolemDefaultLayer(AbstractGolemRenderer<E, S, P, M> parent) {
		super(parent);
		this.parent = parent;
	}

	@Override
	public void submit(
			PoseStack pose, SubmitNodeCollector buffer, int light, S e, float yRot, float xRot
	) {
		parent.renderAllParts(pose, buffer, light, e);
	}

}
