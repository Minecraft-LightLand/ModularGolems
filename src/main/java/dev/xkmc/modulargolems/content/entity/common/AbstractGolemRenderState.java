package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.modulargolems.content.core.IGolemPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public interface AbstractGolemRenderState<
		E extends AbstractGolemEntity<E, P>,
		S extends LivingEntityRenderState & AbstractGolemRenderState<E, S, P>,
		P extends IGolemPart<P>
		> {
}
