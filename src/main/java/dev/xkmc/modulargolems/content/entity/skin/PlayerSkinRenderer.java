package dev.xkmc.modulargolems.content.entity.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderState;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderer;
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
		var skin = ClientSkinDispatch.get(entity);
		if (skin instanceof SpecialRenderProfile profile && profile.texture() != null)
			return profile.texture();
		AbstractClientPlayer player = Proxy.getClientPlayer();
		assert player != null;
		return player.getSkin().body().texturePath();
	}

}
