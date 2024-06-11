package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public record SpecialRenderProfile(boolean slim, @Nullable ResourceLocation texture) implements SpecialRenderSkin {

	@Override
	public void render(HumanoidGolemEntity entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i) {
		if (slim() && PlayerSkinRenderer.SLIM != null) {
			PlayerSkinRenderer.SLIM.render(entity, f1, f2, stack, source, i);
		}
		if (!slim() && PlayerSkinRenderer.REGULAR != null) {
			PlayerSkinRenderer.REGULAR.render(entity, f1, f2, stack, source, i);
		}
	}

}
