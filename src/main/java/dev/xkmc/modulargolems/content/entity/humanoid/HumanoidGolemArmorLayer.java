package dev.xkmc.modulargolems.content.entity.humanoid;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.entity.LivingEntity;

public class HumanoidGolemArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>>
		extends HumanoidArmorLayer<T, M, A> {

	public HumanoidGolemArmorLayer(RenderLayerParent<T, M> parent, A inner, A outer, ModelManager manager) {
		super(parent, inner, outer, manager);
	}

	@Override
	public void render(PoseStack pose, MultiBufferSource source, int light, T entity, float f0, float f1, float f2, float f3, float f4, float f5) {
		pose.pushPose();
		float r = entity.getScale();
		pose.translate(0, (1 - r) * 1.501, 0);
		pose.scale(r, r, r);
		super.render(pose, source, light, entity, f0, f1, f2, f3, f4, f5);
		pose.popPose();
	}
}
