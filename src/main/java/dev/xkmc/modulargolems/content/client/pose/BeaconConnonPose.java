package dev.xkmc.modulargolems.content.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.IGolemModel;
import dev.xkmc.modulargolems.content.item.ranged.CannonPoseUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import static dev.xkmc.modulargolems.content.item.ranged.CannonPoseUtil.MAX_DEGREE;

public record BeaconConnonPose(
		CannonPoseUtil transform, String id, float x, float y, float z
) implements GolemShoulderPose {

	@Deprecated
	public BeaconConnonPose(String id, float x, float y, float z) {
		this(CannonPoseUtil.BEACON, id, x, y, z);
	}

	@Override
	public void setup(AbstractGolemEntity<?, ?> entity, IGolemModel<?, ?, ?> model, ItemStack stack, int slot, float pTick) {
		if (entity.getShoulderAnimStarting() < 5) return;
		var root = model.root();
		// models without a hierarchy cannot be aimed by moving a body part
		if (root == null) return;
		var part = root.getChild("body").getChild(id);

		// the registered transform is the bipedal one, so pick the matching numbers for this golem
		var pose = entity.getCannonPose(transform);
		var angles = pose.getAngle(entity, slot);
		var diff = Mth.wrapDegrees(angles[0] * Mth.RAD_TO_DEG + entity.yBodyRot);
		if (diff > MAX_DEGREE) {
			angles[0] = (MAX_DEGREE - entity.yBodyRot) * Mth.DEG_TO_RAD;
		}
		if (diff < -MAX_DEGREE) {
			angles[0] = (-MAX_DEGREE - entity.yBodyRot) * Mth.DEG_TO_RAD;
		}

		part.yRot -= angles[0] + entity.yBodyRot * Mth.DEG_TO_RAD;
		part.xRot += angles[1];
	}

	@Override
	public void render(AbstractGolemEntity<?, ?> entity, IGolemModel<?, ?, ?> model, ItemStack stack, int slot, PoseStack pose, MultiBufferSource source, int light, float pTick) {
	}

}
