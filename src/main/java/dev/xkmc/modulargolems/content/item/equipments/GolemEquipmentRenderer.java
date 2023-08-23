package dev.xkmc.modulargolems.content.item.equipments;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import static dev.xkmc.modulargolems.content.item.equipments.GolemEquipmentModels.GolemModelPath;
import static dev.xkmc.modulargolems.content.item.equipments.GolemEquipmentModels.LIST;

public class GolemEquipmentRenderer extends RenderLayer<MetalGolemEntity, MetalGolemModel> {
	public HashMap<ModelLayerLocation, MetalGolemModel> map = new HashMap<>();

	public GolemEquipmentRenderer(RenderLayerParent<MetalGolemEntity, MetalGolemModel> r, EntityRendererProvider.Context e) {
		super(r);
		for (var l : LIST) {
			map.put(l, new MetalGolemModel(e.bakeLayer(l)));
		}
	}

	@Override
	public void render(@NotNull PoseStack stack, MultiBufferSource source, int i, @NotNull MetalGolemEntity entity, float f1, float f2, float f3, float f4, float f5, float f6) {
		for (var e : EquipmentSlot.values()) {
			if (entity.getItemBySlot(e).getItem() instanceof MetalGolemArmorItem mgaitem) {
				GolemModelPath gmpath = mgaitem.getModelPath();
				for (List<String> ls : gmpath.l()) {
					MetalGolemModel model = map.get(gmpath.mll());
					model.setupAnim(entity, f1, f2, f4, f5, f6);
					ModelPart gemr = model.root();
					for (String s : ls) {
						gemr = gemr.getChild(s);
					}
					gemr.render(stack, source.getBuffer(RenderType.armorCutoutNoCull(mgaitem.customResLocation())), i, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
				}
			}
		}
	}
}
