package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Crackiness;

public class MetalGolemRenderState extends HumanoidRenderState implements AbstractGolemRenderState<
		MetalGolemEntity, MetalGolemRenderState, MetalGolemPartType> {

	public Crackiness.Level crackiness;

	public float attackTicksRemaining;
}
