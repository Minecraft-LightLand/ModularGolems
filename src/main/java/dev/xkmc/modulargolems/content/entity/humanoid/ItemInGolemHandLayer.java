package dev.xkmc.modulargolems.content.entity.humanoid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.events.event.GolemRenderItemInHandEvent;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ItemInGolemHandLayer<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends ItemInHandLayer<T, M> {

	private final ItemInHandRenderer itemInHandRenderer;

	public ItemInGolemHandLayer(RenderLayerParent<T, M> parent, ItemInHandRenderer r) {
		super(parent, r);
		this.itemInHandRenderer = r;
	}

	protected void renderArmWithItem(LivingEntity e, ItemStack stack, ItemDisplayContext ctx, HumanoidArm arm, PoseStack pose, MultiBufferSource buffer, int light) {
		if (!stack.isEmpty()) {
			pose.pushPose();
			this.getParentModel().translateToHand(arm, pose);
			pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
			pose.mulPose(Axis.YP.rotationDegrees(180.0F));
			boolean flag = arm == HumanoidArm.LEFT;
			pose.translate((float) (flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
			if (!MinecraftForge.EVENT_BUS.post(new GolemRenderItemInHandEvent(e, stack, ctx, arm, pose, buffer, light, itemInHandRenderer)))
				this.itemInHandRenderer.renderItem(e, stack, ctx, flag, pose, buffer, light);
			pose.popPose();
		}
	}

}
