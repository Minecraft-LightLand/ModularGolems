package dev.xkmc.modulargolems.content.entity.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public interface SpecialRenderSkin {

	void submit(HumanoidGolemRenderState entity, PoseStack stack, SubmitNodeCollector source, CameraRenderState cam);

	@Nullable
	default Identifier texture() {
		return null;
	}

}
