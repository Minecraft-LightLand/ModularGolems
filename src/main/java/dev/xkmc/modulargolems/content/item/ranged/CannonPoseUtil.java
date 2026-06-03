package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemAimState;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.phys.Vec3;

public record CannonPoseUtil(float x0, float y0, float z0, float y1, float z1) {

	public static final CannonPoseUtil BEACON_CANNON = new CannonPoseUtil(7f / 16f, 33 / 16f, 3 / 16f, 4.5f / 16f, 23f / 16f);
	public static final CannonPoseUtil FLAME_THROWER = new CannonPoseUtil(7f / 16f, 33 / 16f, 3 / 16f, 4.5f / 16f, 17f / 16f);

	public static final float MAX_DEGREE = 15;

	public float[] getAngle(MetalGolemAimState e, HumanoidArm hand) {
		var ans = new float[2];
		var opt = e.targetAimPos();
		if (opt.isEmpty()) return ans;
		var dst = opt.get();
		int x = hand == HumanoidArm.RIGHT ? -1 : 1;
		var scale = e.scale();
		var br = e.yBodyRot() * Mth.DEG_TO_RAD;
		var forward = new Vec3(-Math.sin(br), 0, Math.cos(br));
		var side = forward.yRot(-(float) (Math.PI / 2));
		var diff = dst.add(new Vec3(0, -y0, 0)
				.add(side.scale(x * x0))
				.add(forward.scale(z0)).scale(scale));

		ans[0] = (float) Math.atan2(diff.x, diff.z);
		ans[1] = (float) Math.asin(y1 * scale / diff.length())
				- (float) Math.atan2(diff.y, diff.horizontalDistance());
		return ans;
	}

	public Vec3 getOrigin(MetalGolemEntity e, HumanoidArm hand) {
		var opt = e.getTargetAimPos();
		if (opt.isEmpty()) return e.position();
		var dst = opt.get();
		int x = hand == HumanoidArm.RIGHT ? -1 : 1;
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

	public boolean isOutOfRange(MetalGolemEntity e, HumanoidArm hand) {
		return isOutOfRange(e, hand, 5);
	}

	public boolean isOutOfRange(MetalGolemEntity e, HumanoidArm hand, float allowance) {
		var rot = getAngle(MetalGolemAimState.of(e, 1), hand);
		var f0 = Vec3.directionFromRotation(rot[1] * Mth.RAD_TO_DEG, rot[0] * Mth.RAD_TO_DEG);
		var diff = Mth.wrapDegrees(rot[0] * Mth.RAD_TO_DEG + e.yBodyRot);
		if (Math.abs(diff) <= CannonPoseUtil.MAX_DEGREE) return false;
		if (diff > MAX_DEGREE) {
			rot[0] = (MAX_DEGREE - e.yBodyRot) * Mth.DEG_TO_RAD;
		}
		if (diff < -MAX_DEGREE) {
			rot[0] = (-MAX_DEGREE - e.yBodyRot) * Mth.DEG_TO_RAD;
		}
		var f1 = Vec3.directionFromRotation(rot[1] * Mth.RAD_TO_DEG, rot[0] * Mth.RAD_TO_DEG);
		return Math.acos(f0.dot(f1)) > allowance * Mth.DEG_TO_RAD;
	}

}
