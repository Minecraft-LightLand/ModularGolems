package dev.xkmc.modulargolems.content.entity.humanoid;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.mob_weapon_api.example.behavior.ThrowableBehavior;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.modulargolems.compat.curio.ClientCuriosRenderHelper;
import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderer;
import dev.xkmc.modulargolems.content.entity.render.GolemBannerLayer;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.SwingAnimation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

public class HumanoidGolemRenderer extends AbstractGolemRenderer<
		HumanoidGolemEntity, HumanoidGolemRenderState, HumanoidGolemPartType, HumanoidGolemModel> {

	public HumanoidGolemRenderer(EntityRendererProvider.Context ctx) {
		this(ctx, false);
	}

	public HumanoidGolemRenderer(EntityRendererProvider.Context ctx, boolean slim) {
		super(ctx, GolemTypes.TYPE_HUMANOID.get(), new HumanoidGolemModel(ctx.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER)), 0.5f);
		this.addLayer(new HumanoidArmorLayer<>(this, ArmorModelSet.bake(
				slim ? ModelLayers.PLAYER_SLIM_ARMOR : ModelLayers.PLAYER_ARMOR,
				ctx.getModelSet(), HumanoidGolemModel::new
		), ctx.getEquipmentRenderer()));
		this.addLayer(new CustomHeadLayer<>(this, ctx.getModelSet(), ctx.getPlayerSkinRenderCache()));
		this.addLayer(new WingsLayer<>(this, ctx.getModelSet(), ctx.getEquipmentRenderer()));
		this.addLayer(new ItemInGolemHandLayer<>(this));
		this.addLayer(new GolemBannerLayer<>(this));
		if (ModList.get().isLoaded("curios"))
			ClientCuriosRenderHelper.addLayer(this, ctx);
	}

	@Override
	public HumanoidGolemRenderState createRenderState() {
		return new HumanoidGolemRenderState();
	}

	@Override
	public void extractRenderState(HumanoidGolemEntity entity, HumanoidGolemRenderState state, float pt) {
		entity.setupRendering = true;
		super.extractRenderState(entity, state, pt);
		HumanoidMobRenderer.extractHumanoidRenderState(entity, state, pt, this.itemModelResolver);
		state.leftArmPose = this.getArmPose(entity, HumanoidArm.LEFT);
		state.rightArmPose = this.getArmPose(entity, HumanoidArm.RIGHT);
		state.update(entity, pt, itemModelResolver);
		entity.setupRendering = false;
	}

	@Override
	public void submit(HumanoidGolemRenderState entity, PoseStack stack, SubmitNodeCollector source, CameraRenderState cam) {
		var camera = Minecraft.getInstance().getCameraEntity();
		if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
			if (camera != null && camera.getVehicle() != null) {
				if (entity.common().getVehicleId() == camera.getVehicle().getId())
					return;
			}
		}
		var profile = entity.skinProfile;
		if (profile != null) {
			profile.submit(entity, stack, source, cam);
			return;
		}
		submitImpl(entity, stack, source, cam);
	}

	public void submitImpl(HumanoidGolemRenderState entity, PoseStack stack, SubmitNodeCollector source, CameraRenderState cam) {
		super.submit(entity, stack, source, cam);
	}

	public static final ThreadLocal<@Nullable HumanoidGolemModel> MODEL_DELEGATE = new ThreadLocal<>();

	@Override
	public HumanoidGolemModel getModel() {
		var override = MODEL_DELEGATE.get();
		if (override != null) return override;
		return super.getModel();
	}

	protected HumanoidModel.ArmPose getArmPose(HumanoidGolemEntity entity, HumanoidArm arm) {
		var hand = entity.getMainArm() == arm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		var stack = entity.getItemInHand(hand);
		return getArmPose(entity, stack, hand);
	}

	private static HumanoidModel.ArmPose getArmPose(HumanoidGolemEntity entity, ItemStack stack, InteractionHand hand) {
		var extensions = IClientItemExtensions.of(stack);
		var armPose = extensions.getArmPose(entity, hand, stack);
		if (armPose != null) return armPose;
		if (stack.isEmpty()) return HumanoidModel.ArmPose.EMPTY;

		if (!entity.swinging && stack.is(Items.CROSSBOW) && CrossbowItem.isCharged(stack))
			return HumanoidModel.ArmPose.CROSSBOW_HOLD;

		if (entity.isBlocking() && entity.shieldSlot() == hand) {
			return HumanoidModel.ArmPose.BLOCK;
		}

		if (entity.getUsedItemHand() == hand && entity.getUseItemRemainingTicks() > 0) {


			if (entity.isAggressive() && entity.isUsingItem() && WeaponRegistry.HOLD.get(entity, stack)
					.orElse(null) instanceof ThrowableBehavior)
				return HumanoidModel.ArmPose.THROW_TRIDENT;


			ItemUseAnimation anim = stack.getUseAnimation();
			if (anim == ItemUseAnimation.BLOCK) {
				return HumanoidModel.ArmPose.BLOCK;
			}

			if (anim == ItemUseAnimation.BOW) {
				return HumanoidModel.ArmPose.BOW_AND_ARROW;
			}

			if (anim == ItemUseAnimation.TRIDENT) {
				return HumanoidModel.ArmPose.THROW_TRIDENT;
			}

			if (anim == ItemUseAnimation.CROSSBOW) {
				return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
			}

			if (anim == ItemUseAnimation.SPYGLASS) {
				return HumanoidModel.ArmPose.SPYGLASS;
			}

			if (anim == ItemUseAnimation.TOOT_HORN) {
				return HumanoidModel.ArmPose.TOOT_HORN;
			}

			if (anim == ItemUseAnimation.BRUSH) {
				return HumanoidModel.ArmPose.BRUSH;
			}

			if (anim == ItemUseAnimation.SPEAR) {
				return HumanoidModel.ArmPose.SPEAR;
			}
		}

		SwingAnimation attack = stack.get(DataComponents.SWING_ANIMATION);
		if (attack != null && attack.type() == SwingAnimationType.STAB && entity.swinging) {
			return HumanoidModel.ArmPose.SPEAR;
		} else {
			return stack.is(ItemTags.SPEARS) ? HumanoidModel.ArmPose.SPEAR : HumanoidModel.ArmPose.ITEM;
		}

	}

}
