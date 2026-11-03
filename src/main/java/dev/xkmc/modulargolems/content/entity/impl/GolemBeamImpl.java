package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.common.GolemBeamSource;
import net.minecraft.world.phys.Vec3;

public interface GolemBeamImpl extends GolemImplBase {

	default Vec3 getBeamOrigin() {
		var self = asGolem();
		var mount = getHeadMount().scaled(self.getScale());
		double body = Math.toRadians(self.yBodyRot);
		var forward = new Vec3(-Math.sin(body), 0, Math.cos(body));
		var pivot = self.position()
				.add(0, mount.pivotY(), 0)
				.add(forward.scale(mount.pivotZ()));
		double yaw = Math.toRadians(self.yHeadRot);
		double pitch = Math.toRadians(self.getXRot());
		double cosPitch = Math.cos(pitch), sinPitch = Math.sin(pitch);
		var headForward = new Vec3(-Math.sin(yaw) * cosPitch, -sinPitch, Math.cos(yaw) * cosPitch);
		var headUp = new Vec3(-Math.sin(yaw) * sinPitch, cosPitch, Math.cos(yaw) * sinPitch);
		return pivot.add(GolemBeamSource.muzzleOffset(headForward, headUp, mount));
	}

	default GolemBeamSource.HeadMount getHeadMount() {
		return GolemBeamSource.HeadMount.METAL;
	}


}
