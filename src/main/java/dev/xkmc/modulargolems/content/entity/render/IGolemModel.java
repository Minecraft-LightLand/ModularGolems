package dev.xkmc.modulargolems.content.entity.render;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public interface IGolemModel<
		E extends AbstractGolemEntity<E, P>,
		S extends LivingEntityRenderState & AbstractGolemRenderState<E, S, P>,
		P extends IGolemPart<P>,
		M extends EntityModel<S> & IGolemModel<E, S, P, M>
		> {

	default M getThis() {
		return Wrappers.cast(this);
	}

	void renderToBufferInternal(P type, Consumer<ModelPart> col);

	Identifier getTextureLocationInternal(Identifier rl);

}