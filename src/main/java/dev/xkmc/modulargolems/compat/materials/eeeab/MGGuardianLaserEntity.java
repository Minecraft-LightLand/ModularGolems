package dev.xkmc.modulargolems.compat.materials.eeeab;

import dev.xkmc.l2library.base.BaseEntity;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * EEEAB's guardian laser, reimplemented so the golem owns its own emitter.
 * <p>
 * The original {@code EntityGuardianLaser} repositions itself to {@code owner.getY() + hOffset} every
 * tick, and the relic's offset is chest height. Because MG wants the beam on the head mount, using it
 * meant racing another writer for the same coordinates, which could only be resolved by guessing which
 * tick order won. This entity has no second writer: the position is computed here from
 * {@code getBeamOrigin()} and stays there.
 * <p>
 * The renderer is a port of {@code RenderAbsBeam} / {@code RenderGuardianLaser}, keeping the four
 * crossed quads that make the beam look solid from any angle, and reusing the original's render state
 * verbatim so the brightness matches.
 * <p>
 * The constants come from {@code UserType.RELIC_ANNIHILATOR}: wOffset 1.2, hOffset 0.6, radius 0.6,
 * beamRadius 0.525, default length 32.
 */
@SerialClass
public class MGGuardianLaserEntity extends BaseEntity {

	public static final float QUAD_RADIUS = 0.6F;
	public static final float BEAM_RADIUS = 0.525F;
	/** The original swells these while the beam is still growing, see the renderer. */
	public static final float ACCUMULATE_QUAD_SCALE = 1.75F;
	public static final float ACCUMULATE_BEAM_SCALE = 1.5F;
	public static final double BEAM_LENGTH = 32.0D;
	/** {@code getBaseScale()} in the original, added to every target's pick radius. */
	public static final float BASE_SCALE = 0.5F;
	/** {@code new ControlledAnimation(3)} in the original, i.e. the grow-in takes three ticks. */
	public static final int ANIM_DURATION = 3;

	private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(MGGuardianLaserEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> ON = SynchedEntityData.defineId(MGGuardianLaserEntity.class, EntityDataSerializers.BOOLEAN);

	@SerialClass.SerialField
	public int life;
	@SerialClass.SerialField
	public float damage;
	@SerialClass.SerialField
	public int countDown;

	public float yaw, pitch;
	public float preYaw, prePitch;
	public double endPosX, endPosY, endPosZ;
	public double collidePosX, collidePosY, collidePosZ;
	public double prevCollidePosX, prevCollidePosY, prevCollidePosZ;
	@Nullable
	public Direction blockSide;

	/** Mirrors {@code ControlledAnimation}; it ramps up to ANIM_DURATION and then holds. */
	private int animTimer;

	/**
	 * Mirrors {@code scaleControlled}, a second timer the original starts only once
	 * {@link #animTimer} has reached its end, so the beam swells for exactly two frames rather than for
	 * the whole grow-in. Kept apart from {@link #animTimer} because merging them widens the swell.
	 */
	private int scaleTimer;

	public LivingEntity ownerCache;
	private final Set<LivingEntity> hit = new HashSet<>();

	public MGGuardianLaserEntity(EntityType<?> type, Level level) {
		super(type, level);
		this.noCulling = true;
	}

	public MGGuardianLaserEntity(EntityType<?> type, Level level, LivingEntity owner, int life, float damage, int countDown) {
		this(type, level);
		ownerCache = owner;
		entityData.set(OWNER_ID, owner.getId());
		entityData.set(ON, true);
		this.life = life;
		this.damage = damage;
		this.countDown = countDown;
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(OWNER_ID, -1);
		entityData.define(ON, false);
	}

	@Nullable
	public LivingEntity getOwner() {
		if (ownerCache != null && !ownerCache.isRemoved()) return ownerCache;
		if (level().getEntity(entityData.get(OWNER_ID)) instanceof LivingEntity le) ownerCache = le;
		return ownerCache;
	}

	/** Mirrors {@code EntityAbsBeam#isAccumulating}: the beam only exists past the charge-up. */
	public boolean isAccumulating() {
		return tickCount > countDown;
	}

	/**
	 * True while the swell is still playing, matching
	 * {@code !scaleControlled.isEnd() && displayControlled.isEnd()}.
	 */
	public boolean isGrowing() {
		return scaleTimer < ANIM_DURATION;
	}

	/**
	 * Texture scroll frame, matching {@code RenderAbsBeam}: {@code floor((timer - 1 + delta) * 2)}, with
	 * the underflow wrapped to 6 so the beam starts at its shortest and lengthens.
	 */
	public int animFrame(float partialTick) {
		int frame = Mth.floor((animTimer - 1 + partialTick) * 2);
		return frame < 0 ? 6 : frame;
	}

	@Override
	public void tick() {
		super.tick();
		prevCollidePosX = collidePosX;
		prevCollidePosY = collidePosY;
		prevCollidePosZ = collidePosZ;
		preYaw = yaw;
		prePitch = pitch;
		xo = getX();
		yo = getY();
		zo = getZ();

		// scaleControlled only starts once displayControlled has ended. This has to read animTimer from
		// before this tick's increment, which is what the original gets from running beamTick() ahead of
		// incrementOrDecreaseTimer; reading it after would start the swell a tick early.
		if (tickCount >= countDown && animTimer >= ANIM_DURATION && scaleTimer < ANIM_DURATION) scaleTimer++;

		boolean on = getEntityData().get(ON);
		if (on && isAccumulating() && animTimer < ANIM_DURATION) {
			animTimer++;
		} else if (!on && animTimer > 0) {
			animTimer--;
		}
		// hold the top for a beat, then wind down and vanish, same as the original's isStop() gate
		if (!on && animTimer == 0) {
			discard();
			return;
		}
		if (tickCount - countDown > life) {
			if (getEntityData().get(ON)) getEntityData().set(ON, false);
			else {
				discard();
				return;
			}
		}

		var owner = getOwner();
		if (owner == null || owner.isRemoved() || !(owner instanceof AbstractGolemEntity<?, ?> golem)) {
			discard();
			return;
		}

		// the emitter is ours to decide, which is the entire point of this entity existing
		Vec3 origin = golem.getBeamOrigin();
		setPos(origin.x, origin.y, origin.z);
		yaw = (float) Math.toRadians(owner.yHeadRot + 90);
		pitch = (float) -Math.toRadians(owner.getXRot());
		setYRot(owner.yHeadRot);
		setXRot(owner.getXRot());

		calculateEndPos();
		if (isAccumulating()) {
			collide();
			damageTargets(golem);
		} else {
			collidePosX = endPosX;
			collidePosY = endPosY;
			collidePosZ = endPosZ;
			blockSide = null;
		}
		if (!level().isClientSide) hit.removeIf(e -> !e.isAlive());
	}

	private void calculateEndPos() {
		endPosX = getX() + BEAM_LENGTH * Math.cos(yaw) * Math.cos(pitch);
		endPosZ = getZ() + BEAM_LENGTH * Math.sin(yaw) * Math.cos(pitch);
		endPosY = getY() + BEAM_LENGTH * Math.sin(pitch);
	}

	/**
	 * Clips the beam against blocks and records the face it stopped on, which the renderer uses to lay
	 * the impact quad flat on that surface.
	 */
	private void collide() {
		Vec3 from = position();
		Vec3 to = new Vec3(endPosX, endPosY, endPosZ);
		BlockHitResult block = level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		if (block.getType() != BlockHitResult.Type.MISS) {
			Vec3 hitPos = block.getLocation();
			collidePosX = hitPos.x;
			collidePosY = hitPos.y;
			collidePosZ = hitPos.z;
			blockSide = block.getDirection();
		} else {
			collidePosX = endPosX;
			collidePosY = endPosY;
			collidePosZ = endPosZ;
			blockSide = null;
		}
	}

	private void damageTargets(AbstractGolemEntity<?, ?> golem) {
		if (level().isClientSide || tickCount % 2 != 0) return;
		Vec3 from = position();
		Vec3 to = new Vec3(endPosX, endPosY, endPosZ);
		AABB area = new AABB(
				Math.min(from.x, to.x), Math.min(from.y, to.y), Math.min(from.z, to.z),
				Math.max(from.x, to.x), Math.max(from.y, to.y), Math.max(from.z, to.z)).inflate(1, 1, 1);
		List<LivingEntity> candidates = level().getEntitiesOfClass(LivingEntity.class, area,
				e -> !hit.contains(e) && GolemTargetUtil.canTarget(golem, e));
		var source = golem.damageSources().mobAttack(golem);
		for (LivingEntity target : candidates) {
			// the original pads by getBaseScale() on top of the pick radius, which is what keeps the beam
			// from skipping a target the golem is visually sitting on top of
			float pad = target.getPickRadius() + BASE_SCALE;
			AABB box = target.getBoundingBox().inflate(pad, pad, pad);
			if (!box.contains(from) && !box.clip(from, to).isPresent()) continue;
			if (target.hurt(source, damage)) {
				hit.add(target);
				target.setSecondsOnFire(5);
			}
		}
	}

	@Override
	public boolean shouldRender(double x, double y, double z) {
		return true;
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return distance < 4096;
	}

}