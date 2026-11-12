package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.compat.materials.alexscaves.modifier.AtomicBreathModifier;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

public interface GolemBeamImpl extends GolemImplBase, GolemBeamSource {

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

	default boolean isBreathAttack() {
		return entityData().get(AbstractGolemEntity.BREATH_ATTACK);
	}

	default void setBreathAttack(boolean flag) {
		entityData().set(AbstractGolemEntity.BREATH_ATTACK, flag);
	}

	@Override
	default boolean isBeamActive() {
		return isBreathAttack();
	}

	@Override
	default Vec3 getBeamMouth(float partialTicks) {
		return getBeamOrigin();
	}

	@Override
	default Vec3 getBeamEnd(float partialTicks) {
		Vec3 from = getBeamMouth(partialTicks);
		Vec3 to = from.add(GolemBeamSource.lookVector(asGolem(), partialTicks).normalize().scale(AtomicBreathModifier.range()));
		return asGolem().level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, asGolem())).getLocation();
	}

	/**
	 * 冲刺期间把身体锁定在固定偏航角，让一切依据傀儡姿态绘制的效果
	 * 都跟随冲锋线，而不是实时视线。
	 */
	default void setDashYaw(float yaw) {
		entityData().set(AbstractGolemEntity.DASH_YAW, yaw);
	}

	default boolean isDashing() {
		return !Float.isNaN(entityData().get(AbstractGolemEntity.DASH_YAW));
	}

	default float getDashYaw() {
		return entityData().get(AbstractGolemEntity.DASH_YAW);
	}

}
