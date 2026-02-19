package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.content.item.equipments.GolemModelItem;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemBeaconItem;
import dev.xkmc.modulargolems.events.event.GolemRenderItemInHandEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
				var buffer = source.getBuffer(RenderType.armorCutoutNoCull(mgaitem.getModelTexture(entity)));
				renderArmor(mgaitem, pose, buffer, i);
				if (mgaitem.emissive()) {
					buffer = source.getBuffer(RenderType.armorCutoutNoCull(mgaitem.getEmissiveModelTexture(entity)));
					renderArmor(mgaitem, pose, buffer, LightTexture.FULL_BRIGHT);
				}
			} else {
				renderArmWithItem(entity, stack, e, pose, source, i);
			}
		}
	}


	protected void renderArmor(GolemModelItem mgaitem, PoseStack pose, VertexConsumer buffer, int light) {
		GolemModelPath gmpath = GolemModelPath.get(mgaitem.getModelPath());
		for (List<String> ls : gmpath.paths()) {
			MetalGolemModel model = map.get(gmpath.models());
			model.copyFrom(getParentModel());
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


	protected void renderArmWithItem(MetalGolemEntity entity, ItemStack stack, EquipmentSlot slot,
									 PoseStack pose, MultiBufferSource source, int light) {
		if (stack.isEmpty()) return;
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
