package dev.xkmc.modulargolems.content.entity.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

public record SpecialRenderProfile(boolean slim, @Nullable Identifier texture) implements SpecialRenderSkin {

	@Override
	public void submit(HumanoidGolemRenderState entity, PoseStack stack, SubmitNodeCollector source, CameraRenderState cam) {
		if (slim() && PlayerSkinRenderer.SLIM != null) {
			PlayerSkinRenderer.SLIM.submit(entity, stack, source, cam);
		}
		if (!slim() && PlayerSkinRenderer.REGULAR != null) {
			PlayerSkinRenderer.REGULAR.submit(entity, stack, source, cam);
		}
	}

}
