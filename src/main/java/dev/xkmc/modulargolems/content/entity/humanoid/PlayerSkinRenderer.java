package dev.xkmc.modulargolems.content.entity.humanoid;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PlayerSkinRenderer extends HumanoidGolemRenderer {

	public static PlayerSkinRenderer REGULAR;
	public static PlayerSkinRenderer SLIM;

	public PlayerSkinRenderer(EntityRendererProvider.Context ctx, boolean slim) {
		super(ctx, slim);
	}

	@Override
	protected boolean delegated(HumanoidGolemEntity entity) {
		return true;
	}

	@Override
	public void render(HumanoidGolemEntity entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i) {
		stack.pushPose();
		float r = entity.getScale();
		//stack.translate(0, (1 - r) * 1.501, 0);
		stack.scale(r, r, r);
		renderImpl(entity, f1, f2, stack, source, i);
		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(HumanoidGolemEntity entity) {
		var profile = ClientProfileManager.get(entity);
		if (profile == null || profile.texture() == null) {
			AbstractClientPlayer player = Proxy.getClientPlayer();
			assert player != null;
			return player.getSkinTextureLocation();
		}
		return profile.texture();
	}

}
