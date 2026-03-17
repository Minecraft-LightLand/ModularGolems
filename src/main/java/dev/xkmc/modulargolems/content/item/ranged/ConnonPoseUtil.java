package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public record ConnonPoseUtil(float x0, float y0, float z0, float y1, float z1) {

	public static final ConnonPoseUtil BEACON = new ConnonPoseUtil(7f / 16f, 33 / 16f, 3 / 16f, 4.5f / 16f, 17f / 16f);

	public float[] getAngle(MetalGolemEntity e, InteractionHand hand) {
		var ans = new float[2];
		var dst = e.getTargetAimPos();
		if (dst.length() == 0) return ans;
		int x = hand == InteractionHand.MAIN_HAND ? -1 : 1;
		var scale = e.getScale();
		var br = e.yBodyRot * Mth.DEG_TO_RAD;
		var forward = new Vec3(-Math.sin(br), 0, Math.cos(br));
		var side = forward.yRot(-(float) (Math.PI / 2));
		var diff = dst.add(new Vec3(0, -y0, 0)
				.add(side.scale(x * x0))
				.add(forward.scale(z0)).scale(scale));

		ans[0] = (float) Math.atan2(diff.x, diff.z) + br;
		ans[1] = (float) Math.asin(y1 * scale / diff.length())
				- (float) Math.atan2(diff.y, diff.horizontalDistance());
		return ans;
	}

	public Vec3 getOrigin(MetalGolemEntity e, InteractionHand hand) {
		var dst = e.getTargetAimPos();
		if (dst.length() == 0) return e.position();
		int x = hand == InteractionHand.MAIN_HAND ? -1 : 1;
		var scale = e.getScale();
		var br = e.yBodyRot * Mth.DEG_TO_RAD;
		var forward = new Vec3(-Math.sin(br), 0, Math.cos(br));
		var side = forward.yRot(-(float) (Math.PI / 2));
		var origin = new Vec3(0, y0, 0)
				.add(side.scale(-x * x0))
				.add(forward.scale(-z0)).scale(scale);
		var diff = dst.subtract(origin);

		var xrot = -(float) Math.asin(y1 * scale / diff.length())
				+ (float) Math.atan2(diff.y, diff.horizontalDistance());

		var point = diff.multiply(1, 0, 1).normalize();

		var af = new Vec3(
				point.x * Math.cos(xrot),
				Math.sin(xrot),
				point.z * Math.cos(xrot)
		).scale(z1 * scale);
		var av = new Vec3(
				-point.x * Math.sin(xrot),
				Math.cos(xrot),
				-point.z * Math.sin(xrot)
		).scale(y1 * scale);

		return e.position().add(origin.add(af).add(av));
	}

}
