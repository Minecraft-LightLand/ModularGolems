package dev.xkmc.modulargolems.content.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class GolemBannerLayer<
		E extends AbstractGolemEntity<E, P>,
		S extends LivingEntityRenderState & AbstractGolemRenderState<E, S, P>,
		P extends IGolemPart<P>,
		M extends EntityModel<S> & IHeadedModel> extends RenderLayer<S, M> {

	private final float scaleX;
	private final float scaleY;
	private final float scaleZ;

	public GolemBannerLayer(RenderLayerParent<S, M> parent) {
		this(parent, 1.0F, 1.0F, 1.0F);
	}

	public GolemBannerLayer(RenderLayerParent<S, M> parent, float sx, float sy, float sz) {
		super(parent);
		this.scaleX = sx;
		this.scaleY = sy;
		this.scaleZ = sz;
	}

	@Override
	public void submit(PoseStack pose, SubmitNodeCollector col, int light, S state, float yRot, float xRot) {
		var mc = Minecraft.getInstance();
		var cam = mc.getCameraEntity();
		if (cam != null && state.isPassengerOfSameVehicle(cam)) return;
		var stack = state.common().banner();
		pose.pushPose();
		pose.scale(this.scaleX, this.scaleY, this.scaleZ);
		this.getParentModel().getHead().translateAndRotate(pose);
		this.getParentModel().translateToHead(pose);
		stack.submit(pose, col, light, OverlayTexture.NO_OVERLAY, 0);
		pose.popPose();

	}


}