package dev.xkmc.modulargolems.content.entity.humanoid;

import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderState;
import dev.xkmc.modulargolems.content.entity.render.CommonGolemRenderState;
import dev.xkmc.modulargolems.content.entity.skin.SpecialRenderProfile;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.jspecify.annotations.Nullable;

public class HumanoidGolemRenderState extends HumanoidRenderState implements AbstractGolemRenderState<
		HumanoidGolemEntity, HumanoidGolemRenderState, HumanoidGolemPartType> {

	public @Nullable SpecialRenderProfile skinProfile;

	public CommonGolemRenderState common;

	@Override
	public CommonGolemRenderState common() {
		return common;
	}

}
