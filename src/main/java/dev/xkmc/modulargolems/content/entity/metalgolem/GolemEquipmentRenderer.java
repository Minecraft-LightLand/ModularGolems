package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.content.client.pose.GolemShoulderPose;
import dev.xkmc.modulargolems.content.client.weapon.GolemModelAnimations;
import dev.xkmc.modulargolems.content.client.weapon.IEntityModelWeapon;
import dev.xkmc.modulargolems.content.item.equipments.GolemModelItem;
import dev.xkmc.modulargolems.content.item.ranged.IShoulderWeapon;
import dev.xkmc.modulargolems.events.event.GolemRenderItemInHandEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.LIST;

public class GolemEquipmentRenderer extends RenderLayer<MetalGolemEntity, MetalGolemModel> {

	public HashMap<ModelLayerLocation, MetalGolemModel> map = new HashMap<>();
	private final ItemInHandRenderer itemInHandRenderer;

	public GolemEquipmentRenderer(RenderLayerParent<MetalGolemEntity, MetalGolemModel> r, EntityRendererProvider.Context e) {
		super(r);
		itemInHandRenderer = e.getItemInHandRenderer();
		for (var l : LIST) {
			map.put(l, new MetalGolemModel(e.bakeLayer(l)));
		}
	}

	@Override
	public void render(@NotNull PoseStack pose, MultiBufferSource source, int i, @NotNull MetalGolemEntity entity, float f1, float f2, float f3, float f4, float f5, float f6) {
		for (var e : EquipmentSlot.values()) {
			ItemStack stack = entity.getItemBySlot(e);
			if (stack.getItem() instanceof GolemModelItem mgaitem) {
				renderArmor(entity, stack, mgaitem, pose, source, i);
			} else {
				renderArmWithItem(entity, stack, e, pose, source, i, f3);
			}
		}
		renderShoulderWeapon(entity, entity.getRightShoulder().getItem(), InteractionHand.MAIN_HAND, pose, source, i, f3);
		renderShoulderWeapon(entity, entity.getLeftShoulder().getItem(), InteractionHand.OFF_HAND, pose, source, i, f3);
	}

	protected void renderModel(MetalGolemModel model, GolemModelPath gmpath, PoseStack pose, VertexConsumer buffer, int light) {
		for (List<String> ls : gmpath.paths()) {
			ModelPart gemr = model.root();
			pose.pushPose();
			for (String s : ls) {
				gemr.translateAndRotate(pose);
				gemr = gemr.getChild(s);
			}
			gemr.render(pose, buffer, light, OverlayTexture.NO_OVERLAY, -1);
			pose.popPose();
		}
	}

	protected void renderArmor(MetalGolemEntity entity, ItemStack stack, GolemModelItem mgaitem, PoseStack pose, MultiBufferSource source, int light) {
		GolemModelPath gmpath = GolemModelPath.get(mgaitem.getModelPath());
		MetalGolemModel model = map.get(gmpath.models());
		model.copyFrom(getParentModel());
		var buffer = source.getBuffer(RenderType.armorCutoutNoCull(mgaitem.getModelTexture(entity)));
		renderModel(model, gmpath, pose, buffer, light);
		if (mgaitem.emissive()) {
			buffer = source.getBuffer(RenderType.armorCutoutNoCull(mgaitem.getEmissiveModelTexture(entity)));
			renderModel(model, gmpath, pose, buffer, LightTexture.FULL_BRIGHT);
		}
		if (stack.hasFoil()) {
			buffer = source.getBuffer(RenderType.armorEntityGlint());
			renderModel(model, gmpath, pose, buffer, light);
		}
	}

	protected void renderShoulderWeapon(
			MetalGolemEntity entity, ItemStack stack, InteractionHand hand,
			PoseStack pose, MultiBufferSource source, int light, float pTick) {
		if (!(stack.getItem() instanceof IShoulderWeapon weapon)) return;
		var id = weapon.getModelForHand(hand);
		if (id == null) return;
		GolemModelPath gmpath = GolemModelPath.MAP.get(id);
		if (gmpath == null) {
			var sp = GolemShoulderPose.MAP.get(id);
			if (sp != null) {
				sp.render(entity, getParentModel(), stack, hand, pose, source, light, pTick);
			}
			return;
		}
		MetalGolemModel model = map.get(gmpath.models());
		model.root().getAllParts().forEach(ModelPart::resetPose);
		model.copyFrom(getParentModel());
		var list = weapon.getAnimationData(entity, stack, hand);
		for (var entry : list) {
			if (GolemModelAnimations.MAP.containsKey(entry.id())) {
				var anim = GolemModelAnimations.MAP.get(entry.id());
				if (anim != null) {
					var state = new AnimationState();
					state.startIfStopped(0);
					model.animate(state, anim, entry.tick() + entry.speed() * pTick);
				}
			}
		}
		var sp = GolemShoulderPose.MAP.get(id);
		if (sp != null) {
			sp.setup(entity, model, stack, hand, pTick);
			sp.render(entity, model, stack, hand, pose, source, light, pTick);
		}
		var buffer = source.getBuffer(RenderType.armorCutoutNoCull(weapon.getModelTexture(entity, stack, hand)));
		renderModel(model, gmpath, pose, buffer, light);
		if (weapon.emissive()) {
			buffer = source.getBuffer(RenderType.armorCutoutNoCull(weapon.getEmissiveTexture(entity, stack, hand)));
			renderModel(model, gmpath, pose, buffer, LightTexture.FULL_BRIGHT);
		}
		if (stack.hasFoil()) {
			buffer = source.getBuffer(RenderType.armorEntityGlint());
			renderModel(model, gmpath, pose, buffer, light);
		}
	}

	protected boolean renderWeaponModel(
			MetalGolemEntity entity, IEntityModelWeapon weapon, ItemStack stack, InteractionHand hand,
			PoseStack pose, MultiBufferSource source, int light, float pTick) {
		var id = weapon.getModelForHand(hand);
		if (id == null) return false;
		GolemModelPath gmpath = GolemModelPath.get(id);
		MetalGolemModel model = map.get(gmpath.models());
		model.root().getAllParts().forEach(ModelPart::resetPose);
		model.copyFrom(getParentModel());
		if (weapon.shouldPlayAnimation(entity, stack, hand)) {
			var anim = GolemModelAnimations.MAP.get(id);
			if (anim != null) {
				float speed = weapon.getAnimationSpeed(entity, stack, hand);
				float tick = weapon.getAnimationTick(entity, stack, hand);
				var state = new AnimationState();
				state.startIfStopped(0);
				model.animate(state, anim, tick + speed * pTick);
			}
		}
		var buffer = source.getBuffer(RenderType.armorCutoutNoCull(weapon.getModelTexture(entity, stack, hand)));
		renderModel(model, gmpath, pose, buffer, light);
		if (weapon.emissive()) {
			buffer = source.getBuffer(RenderType.armorCutoutNoCull(weapon.getEmissiveTexture(entity, stack, hand)));
			renderModel(model, gmpath, pose, buffer, LightTexture.FULL_BRIGHT);
		}
		if (stack.hasFoil()) {
			buffer = source.getBuffer(RenderType.armorEntityGlint());
			renderModel(model, gmpath, pose, buffer, light);
		}
		return true;
	}

	protected void renderArmWithItem(MetalGolemEntity entity, ItemStack stack, EquipmentSlot slot,
	                                 PoseStack pose, MultiBufferSource source, int light, float pTick) {
		if (stack.isEmpty()) return;
		if (stack.getItem() instanceof IEntityModelWeapon weapon) {
			InteractionHand hand = slot == EquipmentSlot.MAINHAND ? InteractionHand.MAIN_HAND :
					slot == EquipmentSlot.OFFHAND ? InteractionHand.OFF_HAND : null;
			if (hand != null) {
				if (renderWeaponModel(entity, weapon, stack, hand, pose, source, light, pTick))
					return;
			}
		}
		ItemDisplayContext ctx = null;
		if (slot == EquipmentSlot.MAINHAND) {
			ctx = ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
		} else if (slot == EquipmentSlot.OFFHAND) {
			ctx = ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
		}
		if (ctx == null) return;
		var arm = slot == EquipmentSlot.MAINHAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
		pose.pushPose();
		getParentModel().transformToHand(slot, pose);
		boolean offhand = slot == EquipmentSlot.OFFHAND;
		pose.translate((offhand ? 1 : -1) * 0.7f, 0.8F, -0.25F);
		pose.mulPose(Axis.XP.rotationDegrees(-90));
		pose.mulPose(Axis.YP.rotationDegrees(180));
		Minecraft.getInstance().getItemRenderer()
				.renderStatic(entity, stack, ctx, offhand,
						pose, source, entity.level(), light, OverlayTexture.NO_OVERLAY,
						entity.getId() + slot.ordinal());
		var r = itemInHandRenderer;
		if (!NeoForge.EVENT_BUS.post(new GolemRenderItemInHandEvent(entity, stack, ctx, arm, pose, source, light, r)).isCanceled())
			r.renderItem(entity, stack, ctx, offhand, pose, source, light);
		pose.popPose();

	}

}
