package dev.xkmc.modulargolems.content.entity.humanoid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.events.event.GolemRenderItemInHandEvent;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;
import net.neoforged.neoforge.common.NeoForge;

public class ItemInGolemHandLayer<T extends ArmedEntityRenderState, M extends EntityModel<T> & ArmedModel<T>> extends ItemInHandLayer<T, M> {

	public ItemInGolemHandLayer(RenderLayerParent<T, M> parent) {
		super(parent);
	}

	@Override
	protected void submitArmWithItem(T state, ItemStackRenderState item, ItemStack stack, HumanoidArm arm, PoseStack pose, SubmitNodeCollector col, int light) {
		if (!item.isEmpty()) {
			pose.pushPose();
			this.getParentModel().translateToHand(state, arm, pose);
			pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
			pose.mulPose(Axis.YP.rotationDegrees(180.0F));
			boolean isLeftHand = arm == HumanoidArm.LEFT;
			float offsetX = 1.0F;
			float offsetY = 2.0F;
			float offsetZ = -10.0F;
			pose.translate((isLeftHand ? -1 : 1) * offsetX / 16.0F, offsetY / 16.0F, offsetZ / 16.0F);
			if (state.attackTime > 0.0F && state.attackArm == arm && state.swingAnimationType == SwingAnimationType.STAB) {
				SpearAnimations.thirdPersonAttackItem(state, pose);
			}

			float ticksUsingItem = state.ticksUsingItem(arm);
			if (ticksUsingItem != 0.0F) {
				(arm == HumanoidArm.RIGHT ? state.rightArmPose : state.leftArmPose).animateUseItem(state, pose, ticksUsingItem, arm, stack);
			}
			if (!NeoForge.EVENT_BUS.post(new GolemRenderItemInHandEvent(state, item, stack, arm, pose, col, light)).isCanceled())
				item.submit(pose, col, light, OverlayTexture.NO_OVERLAY, state.outlineColor);
			pose.popPose();
		}
	}


}
