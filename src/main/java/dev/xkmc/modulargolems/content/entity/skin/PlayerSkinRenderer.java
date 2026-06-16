package dev.xkmc.modulargolems.content.entity.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemModel;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderState;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class PlayerSkinRenderer extends HumanoidGolemRenderer {

	@Nullable
	public static PlayerSkinRenderer REGULAR, SLIM;

	public PlayerSkinRenderer(EntityRendererProvider.Context ctx, boolean slim) {
		super(ctx, slim);
	}

	public PlayerSkinRenderer(EntityRendererProvider.Context ctx, ModelPart part, boolean slim) {
		super(ctx, part, slim);
	}

	public void setModel(HumanoidGolemModel model) {
		this.model = model;
	}

	@Override
	protected boolean delegated(HumanoidGolemRenderState entity) {
		return true;
	}

	@Override
	public void submit(HumanoidGolemRenderState entity, PoseStack stack, SubmitNodeCollector source, CameraRenderState cam) {
		stack.pushPose();
		HumanoidGolemRenderer.MODEL_DELEGATE.set(getModel());
		submitImpl(entity, stack, source, cam);
		HumanoidGolemRenderer.MODEL_DELEGATE.remove();
		stack.popPose();
	}

	@Override
	public Identifier getTextureLocation(HumanoidGolemRenderState entity) {
		var skin = entity.skinProfile;
		if (skin != null && skin.texture() != null)
			return skin.texture();
		AbstractClientPlayer player = Minecraft.getInstance().player;
		assert player != null;
		return player.getSkin().body().texturePath();
	}

}
