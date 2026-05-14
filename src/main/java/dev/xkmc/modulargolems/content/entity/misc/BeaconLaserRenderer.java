package dev.xkmc.modulargolems.content.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.entity.metalgolem.BeaconRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.DyeColor;

public class BeaconLaserRenderer extends EntityRenderer<BeaconLaserEntity, BeaconLaserRenderState> {

	public BeaconLaserRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public BeaconLaserRenderState createRenderState() {
		return new BeaconLaserRenderState();
	}

	@Override
	public void extractRenderState(BeaconLaserEntity entity, BeaconLaserRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.update(entity, partialTicks);
	}

	@Override
	public boolean shouldRender(BeaconLaserEntity entity, Frustum culler, double camX, double camY, double camZ) {
		return true;
	}

	@Override
	public void submit(BeaconLaserRenderState e, PoseStack pose, SubmitNodeCollector col, CameraRenderState camera) {
		pose.pushPose();
		pose.mulPose(Axis.YP.rotationDegrees(e.yrot));
		pose.mulPose(Axis.XP.rotationDegrees(e.xrot + 90));
		var perc = e.perc;
		var r = e.scale * 0.5f * perc * perc;
		BeaconRenderer.submitBeaconBeam(pose, col, r, 0, 0, e.len, DyeColor.WHITE.getTextureDiffuseColor());
		pose.popPose();
	}

}
