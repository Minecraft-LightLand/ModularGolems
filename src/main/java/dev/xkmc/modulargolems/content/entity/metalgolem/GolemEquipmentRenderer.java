package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.content.client.pose.GolemShoulderPose;
import dev.xkmc.modulargolems.content.client.weapon.GolemModelAnimations;
import dev.xkmc.modulargolems.content.item.equipments.GolemModelItem;
import dev.xkmc.modulargolems.events.event.GolemRenderItemInHandEvent;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.LIST;

public class GolemEquipmentRenderer extends RenderLayer<MetalGolemRenderState, MetalGolemModel> {

	public final HashMap<ModelLayerLocation, MetalGolemModel> map = new HashMap<>();
	public final HashMap<Identifier, KeyframeAnimation> cache = new HashMap<>();

	public GolemEquipmentRenderer(RenderLayerParent<MetalGolemRenderState, MetalGolemModel> r, EntityRendererProvider.Context e) {
		super(r);
		for (var l : LIST) {
			map.put(l, new MetalGolemModel(e.bakeLayer(l), l));
		}
	}

	@Override
	public void submit(PoseStack pose, SubmitNodeCollector col, int light, MetalGolemRenderState state, float yRot, float xRot) {
		submitArmor(state, state.headEquipment, pose, col, light);
		submitArmor(state, state.chestEquipment, pose, col, light);
		submitArmor(state, state.legsEquipment, pose, col, light);
		submitArmor(state, state.feetEquipment, pose, col, light);
		submitArmWithItem(state, state.rightHandItemState, state.rightWeaponState, state.rightHandItemStack, HumanoidArm.RIGHT, pose, col, light);
		submitArmWithItem(state, state.leftHandItemState, state.leftWeaponState, state.leftHandItemStack, HumanoidArm.LEFT, pose, col, light);
		submitShoulder(state, state.rightShoulderState, state.rightShoulderItem, HumanoidArm.RIGHT, pose, col, light);
		submitShoulder(state, state.leftShoulderState, state.leftShoulderItem, HumanoidArm.LEFT, pose, col, light);
	}

	public void submitModel(
			MetalGolemRenderState state, MetalGolemModel model,
			SubmitNodeCollector col, PoseStack pose, RenderType type, @Nullable RenderType altType,
			int light, boolean foil, int outline
	) {

		col.submitModel(model, state, pose, type, light, OverlayTexture.NO_OVERLAY,
				-1, null, outline, null);
		if (altType != null)
			col.submitModel(model, state, pose, type, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
					-1, null, 0, null);
		if (foil)
			col.submitModel(model, state, pose, RenderTypes.armorEntityGlint(), light, OverlayTexture.NO_OVERLAY,
					-1, null, 0, null);
	}

	protected void submitArmor(MetalGolemRenderState entity, ItemStack stack, PoseStack pose, SubmitNodeCollector col, int light) {
		if (!(stack.getItem() instanceof GolemModelItem mgaitem)) return;
		GolemModelPath gmpath = GolemModelPath.get(mgaitem.getModelPath());
		MetalGolemModel model = map.get(gmpath.models());
		entity.put(gmpath.models(), MetalGolemModelItemState.ofArmor(gmpath.paths()));
		RenderType rt = RenderTypes.armorCutoutNoCull(mgaitem.getModelTexture(entity.model));
		RenderType alt = null;
		if (mgaitem.emissive()) {
			alt = RenderTypes.armorCutoutNoCull(mgaitem.getEmissiveModelTexture(entity.model));
		}
		submitModel(entity, model, col, pose, rt, alt, light, stack.hasFoil(), entity.outlineColor);
	}

	protected void submitShoulder(
			MetalGolemRenderState entity, @Nullable MetalGolemShoulderModelState shoulder,
			ItemStack stack, HumanoidArm hand,
			PoseStack pose, SubmitNodeCollector source, int light) {
		if (shoulder == null) return;
		var id = shoulder.model();
		GolemModelPath gmpath = GolemModelPath.MAP.get(id);
		if (gmpath == null) {
			var sp = GolemShoulderPose.MAP.get(id);
			if (sp != null) {
				sp.submit(entity, stack, hand, pose, source, light);
			}
			return;
		}
		MetalGolemModel model = map.get(gmpath.models());
		var sp = GolemShoulderPose.MAP.get(id);
		if (sp != null) {
			sp.submit(entity, stack, hand, pose, source, light);
		}
		List<Pair<KeyframeAnimation, Float>> anims = new ArrayList<>();
		for (var entry : shoulder.anims().object2FloatEntrySet()) {
			var aid = entry.getKey();
			var anim = GolemModelAnimations.MAP.get(aid);
			if (anim == null) continue;
			var key = cache.computeIfAbsent(aid, x -> anim.bake(model.root()));
			anims.add(Pair.of(key, entry.getFloatValue()));
		}
		entity.put(gmpath.models(), MetalGolemModelItemState.ofShoulder(gmpath.paths(), anims, stack, hand, shoulder.model(), entity.aim));
		RenderType rt = RenderTypes.armorCutoutNoCull(shoulder.tex());
		RenderType alt = null;
		if (shoulder.emissive() != null) {
			alt = RenderTypes.armorCutoutNoCull(shoulder.emissive());
		}
		submitModel(entity, model, source, pose, rt, alt, light, stack.hasFoil(), entity.outlineColor);
	}

	protected void submitWeaponModel(
			MetalGolemRenderState entity, MetalGolemWeaponModelState data, ItemStack stack, HumanoidArm arm, PoseStack pose, SubmitNodeCollector col, int light) {
		GolemModelPath gmpath = GolemModelPath.get(data.model());
		MetalGolemModel model = map.get(gmpath.models());

		Pair<KeyframeAnimation, Float> animData = null;
		if (data.playAnim()) {
			var anim = GolemModelAnimations.MAP.get(data.model());
			if (anim != null) {
				var state = new AnimationState();
				state.startIfStopped(0);
				var key = cache.computeIfAbsent(data.model(), x -> anim.bake(model.root()));
				animData = Pair.of(key, data.animTick());
			}
		}
		entity.put(gmpath.models(), MetalGolemModelItemState.ofWeapon(gmpath.paths(), animData));
		RenderType rt = RenderTypes.armorCutoutNoCull(data.tex());
		RenderType alt = null;
		if (data.emissive() != null) {
			alt = RenderTypes.armorCutoutNoCull(data.emissive());
		}
		submitModel(entity, model, col, pose, rt, alt, light, stack.hasFoil(), entity.outlineColor);
	}

	protected void submitArmWithItem(MetalGolemRenderState state, ItemStackRenderState item, @Nullable MetalGolemWeaponModelState data, ItemStack stack, HumanoidArm arm, PoseStack pose, SubmitNodeCollector col, int light) {
		if (item.isEmpty()) return;
		if (data != null) {
			submitWeaponModel(state, data, stack, arm, pose, col, light);
			return;
		}
		pose.pushPose();
		this.getParentModel().translateToHand(state, arm, pose);
		pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
		pose.mulPose(Axis.YP.rotationDegrees(180.0F));
		pose.translate((arm == HumanoidArm.RIGHT ? 1 : -1) * 10 / 16f, 4f / 16f, -4f / 16f);
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
