package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.client.GolemModelPath;
import dev.xkmc.modulargolems.content.item.equipments.GolemModelItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import static dev.xkmc.modulargolems.content.client.GolemEquipmentModels.LIST;

public class GolemEquipmentRenderer extends RenderLayer<MetalGolemEntity, MetalGolemModel> {

	public HashMap<ModelLayerLocation, MetalGolemModel> map = new HashMap<>();

	public GolemEquipmentRenderer(RenderLayerParent<MetalGolemEntity, MetalGolemModel> r, EntityRendererProvider.Context e) {
		super(r);
		for (var l : LIST) {
			map.put(l, new MetalGolemModel(e.bakeLayer(l)));
		}
	}

	@Override
	public void render(@NotNull PoseStack pose, MultiBufferSource source, int i, @NotNull MetalGolemEntity entity, float f1, float f2, float f3, float f4, float f5, float f6) {
		for (var e : EquipmentSlot.values()) {
			ItemStack stack = entity.getItemBySlot(e);
			if (stack.getItem() instanceof GolemModelItem mgaitem) {
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
					gemr.render(pose, source.getBuffer(RenderType.armorCutoutNoCull(mgaitem.getModelTexture())),
							i, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
					pose.popPose();
				}
			} else {
				renderArmWithItem(entity, stack, e, pose, source, i);
			}
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

		pose.pushPose();
		getParentModel().transformToHand(slot, pose);
		boolean offhand = slot == EquipmentSlot.OFFHAND;
		pose.translate((offhand ? 1 : -1) * 0.7f, 0.8F, -0.25F);
		pose.mulPose(Axis.XP.rotationDegrees(-90));
		Minecraft.getInstance().getItemRenderer()
				.renderStatic(entity, stack, ctx, offhand,
						pose, source, entity.level(), light, OverlayTexture.NO_OVERLAY,
						entity.getId() + slot.ordinal());
		pose.popPose();

	}


}
