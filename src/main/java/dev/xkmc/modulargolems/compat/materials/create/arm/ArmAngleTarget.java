package dev.xkmc.modulargolems.compat.materials.create.arm;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ArmAngleTarget {
	static final ArmAngleTarget NO_TARGET = new ArmAngleTarget();
	float baseAngle;
	float lowerArmAngle;
	float upperArmAngle;
	float headAngle;

	private ArmAngleTarget() {
		this.lowerArmAngle = 135.0F;
		this.upperArmAngle = 45.0F;
		this.headAngle = 0.0F;
	}

	public ArmAngleTarget(Vec3 origin, Vec3 pointTarget, Direction clawFacing, boolean ceiling) {
		Vec3 target = pointTarget.add(Vec3.atLowerCornerOf(clawFacing.getOpposite().getNormal()).scale(0.5F));
		Vec3 diff = target.subtract(origin);
		float horizontalDistance = (float) diff.multiply(1.0F, 0.0F, 1.0F).length();
		float baseAngle = deg(Mth.atan2(diff.x, diff.z)) + 180.0F;
		if (ceiling) {
			diff = diff.multiply(1.0F, -1.0F, 1.0F);
			baseAngle = 180.0F - baseAngle;
		}

		float alphaOffset = deg(Mth.atan2(diff.y, horizontalDistance));
		float a = 0.875F;
		float a2 = a * a;
		float b = 0.9375F;
		float b2 = b * b;
		float diffLength = Mth.clamp(Mth.sqrt((float) (diff.y * diff.y + (horizontalDistance * horizontalDistance))), 0.125F, a + b);
		float diffLength2 = diffLength * diffLength;
		float alphaRatio = (-b2 + a2 + diffLength2) / (2.0F * a * diffLength);
		float alpha = deg(Math.acos(alphaRatio)) + alphaOffset;
		float betaRatio = (-diffLength2 + a2 + b2) / (2.0F * b * a);
		float beta = deg(Math.acos(betaRatio));
		if (Float.isNaN(alpha)) {
			alpha = 0.0F;
		}

		if (Float.isNaN(beta)) {
			beta = 0.0F;
		}

		Vec3 headPos = new Vec3(0.0F, 0.0F, 0.0F);
		headPos = rotate(headPos.add(0.0F, b, 0.0F), (beta + 180.0F), Axis.X);
		headPos = rotate(headPos.add(0.0F, a, 0.0F), (alpha - 90.0F), Axis.X);
		headPos = rotate(headPos, baseAngle, Axis.Y);
		headPos = rotate(headPos, ceiling ? 180.0F : 0.0F, Axis.X);
		headPos = headPos.add(origin);
		Vec3 headDiff = pointTarget.subtract(headPos);
		if (ceiling) {
			headDiff = headDiff.multiply(1.0F, -1.0F, 1.0F);
		}

		float horizontalHeadDistance = (float) headDiff.multiply(1.0F, 0.0F, 1.0F).length();
		float headAngle = alpha + beta + 135.0F - deg(Mth.atan2(headDiff.y, horizontalHeadDistance));
		this.lowerArmAngle = alpha;
		this.upperArmAngle = beta;
		this.headAngle = -headAngle;
		this.baseAngle = baseAngle;
	}


	public static float deg(double angle) {
		return angle == (double) 0.0F ? 0.0F : (float) (angle * (double) 180.0F / Math.PI);
	}

	public static Vec3 rotate(Vec3 vec, double deg, Direction.Axis axis) {
		if (deg == (double) 0.0F) {
			return vec;
		} else if (vec == Vec3.ZERO) {
			return vec;
		} else {
			float angle = (float) (deg / (double) 180.0F * Math.PI);
			double sin = (double) Mth.sin(angle);
			double cos = (double) Mth.cos(angle);
			double x = vec.x;
			double y = vec.y;
			double z = vec.z;
			if (axis == Axis.X) {
				return new Vec3(x, y * cos - z * sin, z * cos + y * sin);
			} else if (axis == Axis.Y) {
				return new Vec3(x * cos + z * sin, y, z * cos - x * sin);
			} else {
				return axis == Axis.Z ? new Vec3(x * cos - y * sin, y * cos + x * sin, z) : vec;
			}
		}
	}

}
