package dev.xkmc.modulargolems.content.entity.dog;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Renders the rider inside the dog's already-rotated pose stack while it spin-charges, and marks it
 * so its own render is cancelled that frame.
 */
public class DogRiderLayer extends RenderLayer<DogGolemEntity, DogGolemModel> {

	public static final Set<UUID> BLOCKED = new HashSet<>();

	private static final float SEAT_HEIGHT_TRIM = 1.5F;

	public DogRiderLayer(DogGolemRenderer parent) {
		super(parent);
	}

	@Override
	public void render(PoseStack pose, MultiBufferSource buffer, int light, DogGolemEntity dog,
		float legAngle, float legSwing, float bob, float ageInTicks, float yRot, float xRot) {
		if (dog.getPassengers().isEmpty() || !dog.isAutoSpinAttack()) return;
		for (Entity passenger : dog.getPassengers()) {
			if (passenger == Minecraft.getInstance().getCameraEntity() && Minecraft.getInstance().options.getCameraType().isFirstPerson())
				continue;
			BLOCKED.remove(passenger.getUUID());
			int index = dog.getPassengers().indexOf(passenger);
			int total = dog.getPassengers().size();
			float width = dog.getBbWidth();
			float offset = index == 0 ? 0.7f : index + (dog.getControllingPassenger() instanceof Player ? 1.7f : 1.2f);
			double seatZ = width / 2 - width / total * offset;
			double seatY = 0.0;
			pose.pushPose();
			pose.translate(0.0F, (float) (seatY - dog.getBbHeight() * 0.5F) + SEAT_HEIGHT_TRIM, (float) -seatZ);
			pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
			pose.mulPose(Axis.YP.rotationDegrees(180.0F));
			pose.mulPose(Axis.YP.rotationDegrees(dog.yBodyRot));
			EntityRenderer<? super Entity> r = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(passenger);
			if (r != null) r.render(passenger, 0.0F, bob, pose, buffer, light);
			pose.popPose();
			BLOCKED.add(passenger.getUUID());
		}
	}

}
