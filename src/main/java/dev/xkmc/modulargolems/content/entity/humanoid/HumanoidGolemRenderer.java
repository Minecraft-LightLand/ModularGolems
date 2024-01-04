package dev.xkmc.modulargolems.content.entity.humanoid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemRenderer;
import dev.xkmc.modulargolems.content.entity.common.GolemBannerLayer;
import dev.xkmc.modulargolems.content.entity.common.ResizedLayer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

public class HumanoidGolemRenderer extends AbstractGolemRenderer<HumanoidGolemEntity, HumaniodGolemPartType, HumanoidGolemModel> {

	protected static void transform(PoseStack stack, ItemDisplayContext transform, @Nullable HumaniodGolemPartType part) {
		switch (transform) {
			case GUI:
			case FIRST_PERSON_LEFT_HAND:
			case FIRST_PERSON_RIGHT_HAND:
				break;
			case THIRD_PERSON_LEFT_HAND:
			case THIRD_PERSON_RIGHT_HAND: {
				stack.translate(0.25, 0.4, 0.5);
				float size = 0.625f;
				stack.scale(size, size, size);
				break;
			}
			case GROUND: {
				stack.translate(0.25, 0, 0.5);
				float size = 0.625f;
				stack.scale(size, size, size);
				break;
			}
			case NONE:
			case HEAD:
			case FIXED: {
				stack.translate(0.5, 0.5, 0.5);
				float size = 0.5f;
				stack.scale(size, -size, size);
				stack.translate(0, -0.5, 0);
				return;
			}
		}
		stack.mulPose(Axis.ZP.rotationDegrees(135));
		stack.mulPose(Axis.YP.rotationDegrees(-155));
		if (part == null) {
			float size = 0.45f;
			stack.scale(size, size, size);
			stack.translate(0, -2, 0);
		} else if (part == HumaniodGolemPartType.BODY) {
			float size = 0.65f;
			stack.scale(size, size, size);
			stack.translate(0, -1.2, 0);
		} else if (part == HumaniodGolemPartType.LEGS) {
			float size = 0.8f;
			stack.scale(size, size, size);
			stack.translate(0, -2, 0);
		} else if (part == HumaniodGolemPartType.ARMS) {
			float size = 0.6f;
			stack.scale(size, size, size);
			stack.translate(0, -1.5, 0);
		}
	}

	public HumanoidGolemRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new HumanoidGolemModel(ctx.bakeLayer(ModelLayers.PLAYER)), 0.5f, HumaniodGolemPartType::values);
		this.addLayer(new ResizedLayer<>(this, new HumanoidArmorLayer<>(this,
				new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
				new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
				ctx.getModelManager())));
		this.addLayer(new ResizedLayer<>(this, new CustomHeadLayer<>(this, ctx.getModelSet(),
				1, 1, 1, ctx.getItemInHandRenderer())));
		this.addLayer(new ResizedLayer<>(this, new ElytraLayer<>(this, ctx.getModelSet())));
		this.addLayer(new ResizedLayer<>(this, new LayerWrapper<>(this,
				new ItemInHandLayer<>(this, ctx.getItemInHandRenderer()))));
		this.addLayer(new ResizedLayer<>(this, new GolemBannerLayer<>(this, ctx.getItemInHandRenderer())));
	}

	@Override
	public void render(HumanoidGolemEntity entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i) {
		var camera = Minecraft.getInstance().getCameraEntity();
		if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON &&
				camera != null && camera.getVehicle() != null &&
				entity.getVehicle() == camera.getVehicle())
			return;
		super.render(entity, f1, f2, stack, source, i);
	}
}
