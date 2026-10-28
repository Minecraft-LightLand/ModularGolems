package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public record CannonPoseUtil(float x0, float y0, float z0, float y1, float z1) {

	public static final float MAX_DEGREE = 15;
	private static final float MAX_PITCH = (float) (Math.PI / 2 - 1.0E-3F);

	/**
	 * Muzzle numbers in model pixels, sixteen to a block: the sideways and depth offsets and the height
	 * of the mount, then how far the barrel sits above the mount line and how long it is.
	 */
	public static final CannonPoseUtil BEACON_CANNON = new CannonPoseUtil(7f, 33f, 3f, 5f, 22.5f);
	public static final CannonPoseUtil FLAME_THROWER = new CannonPoseUtil(7f, 33f, 3f, 3.5f, 22.5f);

	public static final CannonPoseUtil DOG_BEACON_CANNON = new CannonPoseUtil(0f, 13f, 3f, 5f, 22.5f);
	public static final CannonPoseUtil DOG_FLAME_THROWER = new CannonPoseUtil(0f, 13f, 3f, 3.5f, 22.5f);

	@Deprecated
	public static final CannonPoseUtil BEACON = FLAME_THROWER;

	/**
	 * Picks the aiming parameters matching the golem type carrying the weapon. The dog hangs its weapon
	 * on a back far lower than a bipedal shoulder and mounts it centred rather than off to one side.
	 */
	public static CannonPoseUtil forEntity(AbstractGolemEntity<?, ?> e, CannonPoseUtil bipedal) {
		var flame = bipedal == FLAME_THROWER || bipedal == BEACON;
		var dog = e instanceof DogGolemEntity;
		var src = dog ? (flame ? DOG_FLAME_THROWER : DOG_BEACON_CANNON) : bipedal;
		return new CannonPoseUtil(src.x0() / 16F, src.y0() / 16F, src.z0() / 16F, src.y1() / 16F, src.z1() / 16F);
	}

	public float[] getAngle(AbstractGolemEntity<?, ?> e, InteractionHand hand) {
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

		ans[0] = (float) Math.atan2(diff.x, diff.z);
		// a target straight below collapses the horizontal distance, which pushes the pitch to the
		// limit and made the aim look out of range, so aim straight down once there is no sideways room
		var flat = diff.horizontalDistance();
		ans[1] = flat < 1.0E-4F ? (diff.y < 0 ? -MAX_PITCH : MAX_PITCH)
				: (float) Math.asin(Math.min(1.0F, y1 * scale / diff.length()))
				- (float) Math.atan2(diff.y, flat);
		return ans;
	}

	public Vec3 getOrigin(AbstractGolemEntity<?, ?> e, InteractionHand hand) {
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

	public boolean isOutOfRange(AbstractGolemEntity<?, ?> e, InteractionHand hand) {
		return isOutOfRange(e, hand, 5);
	}

	public boolean isOutOfRange(AbstractGolemEntity<?, ?> e, InteractionHand hand, float allowance) {
		var rot = getAngle(e, hand);
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
