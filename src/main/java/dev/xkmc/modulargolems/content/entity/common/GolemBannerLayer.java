package dev.xkmc.modulargolems.content.entity.common;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GolemBannerLayer<T extends AbstractGolemEntity<?, ?>, M extends EntityModel<T> & IHeadedModel> extends RenderLayer<T, M> {
	private final float scaleX;
	private final float scaleY;
	private final float scaleZ;
	private final ItemInHandRenderer itemInHandRenderer;

	public GolemBannerLayer(RenderLayerParent<T, M> parent, ItemInHandRenderer iihr) {
		this(parent, 1.0F, 1.0F, 1.0F, iihr);
	}

	public GolemBannerLayer(RenderLayerParent<T, M> parent, float sx, float sy, float sz, ItemInHandRenderer iihr) {
		super(parent);
		this.scaleX = sx;
		this.scaleY = sy;
		this.scaleZ = sz;
		this.itemInHandRenderer = iihr;
	}

	public void render(PoseStack pose, MultiBufferSource buffer, int light, T entity, float f1, float f2, float f3, float f4, float f5, float f6) {
		var mc = Minecraft.getInstance();
		var cam = mc.getCameraEntity();
		if (cam != null && entity.isPassengerOfSameVehicle(cam)) return;
		ItemStack stack = getBanner(entity);
		if (!renders(stack)) return;
		pose.pushPose();
		pose.scale(this.scaleX, this.scaleY, this.scaleZ);
		this.getParentModel().getHead().translateAndRotate(pose);
		this.getParentModel().translateToHead(pose);
		this.itemInHandRenderer.renderItem(entity, stack, ItemDisplayContext.HEAD, false, pose, buffer, light);
		pose.popPose();

	}

	public ItemStack getBanner(T entity) {
		return GolemBannerUtil.getBanner(entity);
	}

	public boolean renders(ItemStack stack) {
		return GolemBannerUtil.renders(stack);
	}

}
