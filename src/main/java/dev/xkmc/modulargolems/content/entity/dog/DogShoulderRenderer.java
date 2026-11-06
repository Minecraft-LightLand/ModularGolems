package dev.xkmc.modulargolems.content.entity.dog;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.content.client.weapon.GolemModelAnimations;
import dev.xkmc.modulargolems.content.client.pose.GolemShoulderPose;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import dev.xkmc.modulargolems.content.item.ranged.IDogBackWeapon;
import dev.xkmc.modulargolems.content.item.ranged.IGenericShoulderWeapon;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.LIST;

/**
 * Draws the dog golem shoulder weapon on its back. Unlike the two legged golem, the dog golem always
 * keeps the weapon deployed since it walks on four legs.
 */
public class DogShoulderRenderer extends RenderLayer<DogGolemEntity, DogGolemModel> {

	private final HashMap<ModelLayerLocation, MetalGolemModel> map = new HashMap<>();

	/**
	 * Mount point on the back, in model pixels, and how the weapon is oriented once mounted. The dog
	 * model faces negative Z, with the head at z=-7 and the tail at z=+7, and its back sits around y=14.
	 * Deploy and retract are driven by the weapon animations, exactly like on the two legged golem.
	 */
	private static final float MOUNT_X = 6.0F;
	private static final float MOUNT_Y = 15.0F;
	private static final float MOUNT_Z = 5.0F;
	private static final float MOUNT_PITCH = 90.0F;
	private static final float MOUNT_YAW = 0.0F;

	public DogShoulderRenderer(RenderLayerParent<DogGolemEntity, DogGolemModel> parent, EntityRendererProvider.Context ctx) {
		super(parent);
		for (var l : LIST) {
			map.put(l, new MetalGolemModel(ctx.bakeLayer(l)));
		}
	}

	@Override
	public void render(@NotNull PoseStack pose, MultiBufferSource source, int light,
			@NotNull DogGolemEntity entity, float f1, float f2, float f3, float f4, float f5, float f6) {
		ItemStack stack = entity.getShoulderSlot().getItem();
		if (!(stack.getItem() instanceof IGenericShoulderWeapon generic)) return;
		IDogBackWeapon weapon = IDogBackWeapon.of(generic);
		var id = weapon.getModel();
		if (id == null) return;
		GolemModelPath gmpath = GolemModelPath.MAP.get(id);
		var shoulderPose = GolemShoulderPose.MAP.get(id);
		if (gmpath == null) {
			// some weapons draw themselves entirely through a pose, without a model path
			if (shoulderPose != null) {
				pose.pushPose();
				pose.translate(MOUNT_X / 16F, MOUNT_Y / 16F, MOUNT_Z / 16F);
				shoulderPose.render(entity, getParentModel(), stack, 0, pose, source, light, f3);
				pose.popPose();
			}
			return;
		}
		MetalGolemModel model = map.get(gmpath.models());
		if (model == null) return;

		model.root().getAllParts().forEach(ModelPart::resetPose);
		for (var entry : weapon.getAnimationData(entity, stack)) {
			var anim = GolemModelAnimations.MAP.get(entry.id());
			if (anim == null) continue;
			var state = new AnimationState();
			state.startIfStopped(0);
			model.animate(state, anim, entry.tick() + entry.speed() * f3);
		}

		pose.pushPose();
		pose.translate(MOUNT_X / 16F, MOUNT_Y / 16F, MOUNT_Z / 16F);
		pose.mulPose(Axis.XP.rotationDegrees(MOUNT_PITCH));
		pose.mulPose(Axis.YP.rotationDegrees(MOUNT_YAW));
		if (shoulderPose != null) {
			shoulderPose.setup(entity, model, stack, 0, f3);
			shoulderPose.render(entity, model, stack, 0, pose, source, light, f3);
		}
		var buffer = source.getBuffer(RenderType.armorCutoutNoCull(weapon.getModelTexture(entity, stack)));
		renderModel(model, gmpath, pose, buffer, light);
		if (weapon.emissive()) {
			buffer = source.getBuffer(RenderType.armorCutoutNoCull(
					weapon.getEmissiveTexture(entity, stack)));
			renderModel(model, gmpath, pose, buffer, LightTexture.FULL_BRIGHT);
		}
		if (stack.hasFoil()) {
			buffer = source.getBuffer(RenderType.armorEntityGlint());
			renderModel(model, gmpath, pose, buffer, light);
		}
		pose.popPose();
	}

	private void renderModel(MetalGolemModel model, GolemModelPath gmpath, PoseStack pose,
			VertexConsumer buffer, int light) {
		for (List<String> ls : gmpath.paths()) {
			ModelPart part = model.root();
			pose.pushPose();
			for (String s : ls) {
				part.translateAndRotate(pose);
				part = part.getChild(s);
			}
			part.render(pose, buffer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
			pose.popPose();
		}
	}

}
