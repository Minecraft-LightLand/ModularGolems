package dev.xkmc.modulargolems.content.entity.dog;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.item.equipments.DogGolemArmorItem;
import dev.xkmc.modulargolems.content.item.equipments.DogGolemArmorSpecialRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DogArmorRenderer extends RenderLayer<DogGolemEntity, DogGolemModel> {

	public DogGolemModel model;

	public DogArmorRenderer(RenderLayerParent<DogGolemEntity, DogGolemModel> r, EntityRendererProvider.Context e) {
		super(r);
		model = new DogGolemModel(e.bakeLayer(GolemEquipmentModels.DOG_ARMOR));
	}

	@Override
	public void render(@NotNull PoseStack pose, MultiBufferSource source, int i, @NotNull DogGolemEntity entity, float f1, float f2, float f3, float f4, float f5, float f6) {
		ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
		if (stack.getItem() instanceof DogGolemArmorSpecialRenderer.ProviderItem pvd) {
			var opt = pvd.getSpecialRenderer();
			if (opt.isPresent()) {
				getParentModel().copyPropertiesTo(model);
				model.copyFrom(getParentModel());
				opt.get().render(entity, stack, pose, source, i, f3, model);
				return;
			}
		}
		if (stack.getItem() instanceof DogGolemArmorItem item) {
			getParentModel().copyPropertiesTo(model);
			model.copyFrom(getParentModel());
			var buffer = source.getBuffer(RenderType.armorCutoutNoCull(item.getModelTexture(entity, false)));
			model.root().render(pose, buffer, i, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
			buffer = source.getBuffer(RenderType.armorCutoutNoCull(item.getModelTexture(entity, true)));
			int col = item.getColor(stack);
			float r = ((col >> 16) & 0xFF) / 255f;
			float g = ((col >> 8) & 0xFF) / 255f;
			float b = (col & 0xFF) / 255f;
			model.root().render(pose, buffer, i, OverlayTexture.NO_OVERLAY, r, g, b, 1);
		}
	}

}
