package dev.xkmc.modulargolems.content.entity.impl;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Implemented by golems that can emit a beam, so that beam particles which normally only accept
 * a specific entity type can be pointed at a golem instead.
 */
public interface GolemBeamSource {

	/**
	 * Where the beam leaves the head, in model pixels, mirroring how {@code CannonPoseUtil} declares the
	 * shoulder mount. {@code pivotY} and {@code pivotZ} place the head's rotation centre, which is what the
	 * beam swings around. {@code muzzleY} and {@code muzzleZ} are the emitter in the head's own frame, up
	 * and ahead, and get rotated with the head pitch as one rigid point.
	 *
	 * <p>Hanging the emitter off the pivot rather than off the eyes is what keeps it on the head: the eyes
	 * already move with the pitch, so offsetting from them would swing the muzzle twice as far and pull it
	 * off the head when looking up.
	 */
	record HeadMount(float pivotY, float pivotZ, float muzzleY, float muzzleZ) {

		public static final HeadMount METAL = new HeadMount(30.0F, 2.1F, 6.5F, 3.0F);
		public static final HeadMount HUMANOID = new HeadMount(24.0F, 0.0F, 4.0F, 3.0F);
		public static final HeadMount DOG = new HeadMount(10.5F, 7.0F, 1.0F, 2.0F);

		public HeadMount scaled(float scale) {
			float f = scale / 16.0F;
			return new HeadMount(pivotY * f, pivotZ * f, muzzleY * f, muzzleZ * f);
		}

	}

	/**
	 * The emitter in world space: the head pivot, plus the muzzle offset carried along by the head's own
	 * orientation. {@code forward} and {@code up} are the head's local axes, so the muzzle stays glued to
	 * the head at any pitch instead of sliding through it.
	 */
	static Vec3 muzzleOffset(Vec3 forward, Vec3 up, HeadMount mount) {
		return up.scale(mount.muzzleY()).add(forward.scale(mount.muzzleZ()));
	}

	/**
	 * Beam direction taken from the head yaw, matching how Cataclysm's death laser aims. Golem body
	 * yaw and head yaw can differ, so {@code getLookAngle()} would not follow what it looks at.
	 */
	static Vec3 lookVector(LivingEntity entity, float partialTicks) {
		float yaw = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
		float yawRadians = yaw * ((float) Math.PI / 180F);
		float pitchRadians = entity.getXRot() * ((float) Math.PI / 180F);
		float cosPitch = Mth.cos(pitchRadians);
		return new Vec3(-Mth.sin(yawRadians) * cosPitch, -Mth.sin(pitchRadians), Mth.cos(yawRadians) * cosPitch);
	}

	/**
	 * Whether the beam is currently being emitted.
	 */
	boolean isBeamActive();

	/**
	 * Beam origin, usually around the head.
	 */
	Vec3 getBeamMouth(float partialTicks);

	/**
	 * Beam endpoint, clipped against blocks.
	 */
	Vec3 getBeamEnd(float partialTicks);

	/**
	 * Beam fade in progress, 0 when not emitting.
	 */
	default float getBeamProgress(float partialTicks) {
		return isBeamActive() ? 1.0F : 0.0F;
	}

}
