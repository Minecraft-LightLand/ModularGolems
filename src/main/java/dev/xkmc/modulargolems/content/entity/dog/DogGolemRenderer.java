package dev.xkmc.modulargolems.content.entity.dog;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemRenderState;
import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderer;
import dev.xkmc.modulargolems.content.entity.render.GolemBannerLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

public class DogGolemRenderer extends AbstractGolemRenderer<DogGolemEntity, DogGolemRenderState, DogGolemPartType, DogGolemModel> {

	protected static void transform(PoseStack stack, ItemDisplayContext transform, @Nullable DogGolemPartType part) {
		switch (transform) {
			case GUI:
			case FIRST_PERSON_LEFT_HAND:
			case FIRST_PERSON_RIGHT_HAND:
				break;
			case THIRD_PERSON_LEFT_HAND:
			case THIRD_PERSON_RIGHT_HAND: {
				stack.translate(0.25, 0.4, 0.5);
				float size = 0.5F;
				stack.scale(size, size, size);
				break;
			}
			case GROUND: {
				stack.translate(0.25, 0, 0.5);
				float size = 0.5F;
				stack.scale(size, size, size);
				break;
			}
			case NONE:
			case HEAD:
			case FIXED: {
				stack.translate(0.5, 0.5, 0.5);
				float size = 1f;
				stack.scale(size, -size, size);
				stack.translate(0, -0.5, 0);
				return;
			}
			default:
				stack.translate(0.1, 0, 0.5);
				float size = 0.75F;
				stack.scale(size, size, size);
				break;
		}
		stack.mulPose(Axis.ZP.rotationDegrees(135));
		stack.mulPose(Axis.YP.rotationDegrees(-155));
		if (part == null) {
			float size = 0.8f;
			stack.scale(size, size, size);
			stack.translate(0, -1.9, 0);
		} else if (part == DogGolemPartType.BODY) {
			float size = 0.9f;
			stack.scale(size, size, size);
			stack.translate(0, -1.6, 0);
		} else if (part == DogGolemPartType.LEGS) {
			float size = 1f;
			stack.scale(size, size, size);
			stack.translate(0, -1.9, 0);
		}
	}

	public DogGolemRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new DogGolemModel(ctx.bakeLayer(GolemEquipmentModels.DOGGOLEM)), 1F, DogGolemPartType::values);
		addLayer(new DogArmorLayer(this, ctx.getModelSet(), ctx.getEquipmentRenderer()));
		addLayer(new GolemBannerLayer<>(this));
	}

	@Override
	public DogGolemRenderState createRenderState() {
		return new DogGolemRenderState();
	}

	@Override
	public void extractRenderState(DogGolemEntity entity, DogGolemRenderState state, float pt) {
		super.extractRenderState(entity, state, pt);
		state.update(entity, pt, itemModelResolver);
	}

}