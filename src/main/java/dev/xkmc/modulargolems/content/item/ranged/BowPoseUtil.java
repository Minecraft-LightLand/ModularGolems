package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class BowPoseUtil {

	public static float getAngle(MetalGolemEntity e) {
		var dst = e.getTargetAimPos();
		if (dst.length() == 0) return 0;
		var pos = e.position();
		dst = dst.add(pos);
		var scale = e.getScale();
		var forward = e.getViewVector(1).multiply(1, 0, 1).normalize();
		var side = forward.yRot(-(float) (Math.PI / 2));
		var base = side.scale(0.7).add(0, 2, 0);
		var origin = pos.add(base.scale(scale));
		var diff = dst.subtract(origin);
		return (float) (-Math.atan2(diff.y, diff.horizontalDistance()) - Math.PI / 2);
	}

	public static Vec3 getOrigin(MetalGolemEntity e) {
		var pos = e.position();
		var scale = e.getScale();
		var forward = e.getViewVector(1).multiply(1, 0, 1).normalize();
		var side = forward.yRot(-(float) (Math.PI / 2));
		float angle = -getAngle(e) - (float) (Math.PI / 2);
		var arm = new Vec3(forward.x * Mth.cos(angle), Mth.sin(angle), forward.z * Mth.cos(angle)).scale(2.2);
		var diff = side.scale(0.7).add(0, 2, 0);
		var ans = pos.add(diff.add(arm).scale(scale));

		return ans;
	}

}
