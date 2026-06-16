package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemModel;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderer;
import net.minecraft.client.model.geom.ModelPart;
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

	public PlayerSkinRenderer(EntityRendererProvider.Context ctx, ModelPart part, boolean slim) {
		super(ctx, part, slim);
	}

	public void setModel(HumanoidGolemModel model) {
		this.model = model;
	}

	@Override
	protected boolean delegated(HumanoidGolemEntity entity) {
		return true;
	}

	@Override
	public void render(HumanoidGolemEntity entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i) {
		stack.pushPose();
		HumanoidGolemRenderer.MODEL_DELEGATE.set(getModel());
		renderImpl(entity, f1, f2, stack, source, i);
		HumanoidGolemRenderer.MODEL_DELEGATE.remove();
		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(HumanoidGolemEntity entity) {
		var skin = ClientSkinDispatch.get(entity);
		if (skin.texture() != null)
			return skin.texture();
		AbstractClientPlayer player = Proxy.getClientPlayer();
		assert player != null;
		return player.getSkin().texture();
	}

}
