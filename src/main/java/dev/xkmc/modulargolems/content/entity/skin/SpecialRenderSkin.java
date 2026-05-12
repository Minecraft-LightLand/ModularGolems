package dev.xkmc.modulargolems.content.entity.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public interface SpecialRenderSkin {

	void submit(HumanoidGolemRenderState entity, PoseStack stack, SubmitNodeCollector source, CameraRenderState cam);

}
