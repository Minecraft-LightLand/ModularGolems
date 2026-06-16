package dev.xkmc.modulargolems.content.entity.humanoid.skin.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public interface IMobCloth {

	void renderHead(PoseStack pose, MultiBufferSource.BufferSource bufferSource, int light, int overlay);

}
