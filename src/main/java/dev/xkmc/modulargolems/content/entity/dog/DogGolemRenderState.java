package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderState;
import dev.xkmc.modulargolems.content.entity.render.CommonGolemRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class DogGolemRenderState extends LivingEntityRenderState implements AbstractGolemRenderState<
		DogGolemEntity, DogGolemRenderState, DogGolemPartType> {


	public CommonGolemRenderState common;

	@Override
	public CommonGolemRenderState common() {
		return common;
	}

}
