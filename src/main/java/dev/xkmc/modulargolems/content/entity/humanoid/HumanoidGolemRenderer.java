package dev.xkmc.modulargolems.content.entity.humanoid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.compat.curio.ClientCuriosRenderHelper;
import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderer;
import dev.xkmc.modulargolems.content.entity.render.GolemBannerLayer;
import dev.xkmc.modulargolems.content.entity.render.GolemTransformType;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class HumanoidGolemRenderer extends AbstractGolemRenderer<
		HumanoidGolemEntity, HumanoidGolemRenderState, HumanoidGolemPartType, HumanoidGolemModel> {

	public static void transform(PoseStack stack, GolemTransformType transform, @Nullable HumanoidGolemPartType part) {
		switch (transform) {
			case FIRST:
				break;
			case THIRD: {
				stack.translate(0.25, 0.4, 0.5);
				float size = 0.625f;
				stack.scale(size, size, size);
				break;
			}
			case ENTITY: {
				stack.translate(0.25, 0, 0.5);
				float size = 0.625f;
				stack.scale(size, size, size);
				break;
			}
			case DEF: {
				stack.translate(0.5, 0.5, 0.5);
				float size = 0.5f;
				stack.scale(size, -size, size);
				stack.translate(0, -0.5, 0);
				return;
			}
			case OTHER:
				stack.translate(0, 0, 0.5);
				break;
		}
		stack.mulPose(Axis.ZP.rotationDegrees(135));
		stack.mulPose(Axis.YP.rotationDegrees(-155));
		if (part == null) {
			float size = 0.45f;
			stack.scale(size, size, size);
			stack.translate(0, -2, 0);
		} else if (part == HumanoidGolemPartType.BODY) {
			float size = 0.65f;
			stack.scale(size, size, size);
			stack.translate(0, -1.2, 0);
		} else if (part == HumanoidGolemPartType.LEGS) {
			float size = 0.8f;
			stack.scale(size, size, size);
			stack.translate(0, -2, 0);
		} else if (part == HumanoidGolemPartType.ARMS) {
			float size = 0.6f;
			stack.scale(size, size, size);
			stack.translate(0, -1.5, 0);
		}
	}

	public HumanoidGolemRenderer(EntityRendererProvider.Context ctx) {
		this(ctx, false);
	}

	public HumanoidGolemRenderer(EntityRendererProvider.Context ctx, boolean slim) {
		super(ctx, new HumanoidGolemModel(ctx.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER)), 0.5f, HumanoidGolemPartType::values);
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
		super.extractRenderState(entity, state, pt);
		state.update(entity, pt, itemModelResolver);
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

}
