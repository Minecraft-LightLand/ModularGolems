package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.l2library.base.BaseEntity;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.impl.GolemBeamSource;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 天穹激光的光束（对应 Cloud_Golem state 14 的 {@code EnergyBeamEntity}）。
 * <p>
 * 不复用 Legendary Monsters 的 {@code EnergyBeamEntity}：它在 tick 里写死
 * {@code caster.getY(0D) + 1}（脚底上一格，再横移约一格），发射点永远在傀儡腿上，
 * 而且每 tick 覆盖位置，外部改不了。这里由 MG 自己拥有发射器，每 tick 定位到
 * {@code getBeamOrigin()}（头部挂点），画法见 {@link CloudLaserRenderer}。
 */
@SerialClass
public class CloudLaserEntity extends BaseEntity {

	/** 光束最大长度（格）。 */
	public static final double BEAM_LENGTH = 32.0D;

	private static final EntityDataAccessor<Integer> OWNER_ID =
			SynchedEntityData.defineId(CloudLaserEntity.class, EntityDataSerializers.INT);

	@SerialClass.SerialField
	public UUID owner;
	@SerialClass.SerialField
	public int life;
	@SerialClass.SerialField
	public float damage;

	/** 光束实际长度（被方块截断后的长度），客户端据此渲染。 */
	public float len = (float) BEAM_LENGTH;
	/** 渲染用朝向（度），约定与 LM 的 {@code EnergyBeamRender2d} 一致：yaw = yHeadRot + 90，pitch = -xRot。 */
	public float beamYaw, beamPitch;
	public float prevYaw, prevPitch;
	/** 光束末端（世界坐标），渲染端据此画落点贴图与闪电。 */
	public double endX, endY, endZ;
	public double prevEndX, prevEndY, prevEndZ;
	/** 光束撞到的方块面，渲染端据此把冲击 quad 贴在面上。 */
	@Nullable
	public Direction blockSide;

	private LivingEntity ownerCache;

	public CloudLaserEntity(EntityType<?> type, Level level) {
		super(type, level);
		this.noCulling = true;
	}

	public CloudLaserEntity(EntityType<?> type, Level level, LivingEntity owner, int life, float damage) {
		this(type, level);
		this.owner = owner.getUUID();
		this.ownerCache = owner;
		entityData.set(OWNER_ID, owner.getId());
		this.life = life;
		this.damage = damage;
		if (owner instanceof AbstractGolemEntity<?, ?> golem) {
			Vec3 origin = golem.getBeamOrigin();
			setPos(origin.x, origin.y, origin.z);
		} else {
			setPos(owner.getX(), owner.getEyeY(), owner.getZ());
		}
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(OWNER_ID, -1);
	}

	@Nullable
	public LivingEntity getOwner() {
		if (ownerCache != null && !ownerCache.isRemoved()) return ownerCache;
		if (level() instanceof ServerLevel sl && owner != null) {
			Entity e = sl.getEntity(owner);
			if (e instanceof LivingEntity le) ownerCache = le;
		}
		if (ownerCache == null) {
			Entity e = level().getEntity(entityData.get(OWNER_ID));
			if (e instanceof LivingEntity le) ownerCache = le;
		}
		return ownerCache;
	}

	@Override
	public void tick() {
		super.tick();
		LivingEntity o = getOwner();
		if (!level().isClientSide() && (o == null || !o.isAlive() || tickCount > life)) {
			discard();
			return;
		}
		if (o == null) {
			discard();
			return;
		}
		// 发射点：傀儡头部挂点，每 tick 跟随
		Vec3 origin = o instanceof AbstractGolemEntity<?, ?> g ? g.getBeamOrigin() : o.getEyePosition();
		setPos(origin.x, origin.y, origin.z);
		prevYaw = beamYaw;
		prevPitch = beamPitch;
		beamYaw = o.yHeadRot + 90.0F;
		beamPitch = -o.getXRot();
		// 方向取头部朝向，和 Cataclysm 的死光一致
		Vec3 dir = GolemBeamSource.lookVector(o, 1.0F);
		Vec3 end = origin.add(dir.scale(BEAM_LENGTH));
		BlockHitResult clip = level().clip(new ClipContext(origin, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		Vec3 hitEnd = clip.getType() == HitResult.Type.MISS ? end : clip.getLocation();
		blockSide = clip.getType() == HitResult.Type.MISS ? null : clip.getDirection();
		len = (float) origin.distanceTo(hitEnd);
		prevEndX = endX;
		prevEndY = endY;
		prevEndZ = endZ;
		endX = hitEnd.x;
		endY = hitEnd.y;
		endZ = hitEnd.z;
		beamEffects(origin, hitEnd);
		// 蓄力阶段（前 20 tick）不结算伤害：LM 的 EnergyBeamEntity 也是 tickCount > 20 才开始打人，
		// 之前这里每 tick 都调 damageAlong，导致蓄力时就掉血。
		if (!level().isClientSide() && tickCount > 20) damageAlong(o, origin, hitEnd);
	}

	/**
	 * 复刻 LM {@code EnergyBeamEntity.tick()} 的随光束效果：镜头抖动（服务端）+ 沿光束的螺旋
	 * 粒子（客户端），外加 Boss state14 在 20/39 tick 播的蓄力与开火音。缺了这些光束只剩一条静止的带子。
	 */
	private void beamEffects(Vec3 origin, Vec3 hitEnd) {
		if (tickCount == 1 && !level().isClientSide) {
			playSound(ModSounds.BEAM_CHARGE.get(), 3.0F, 1.0F);
		}
		// Boss state14：光束生成后 20 tick 播 BEAM_GO，LM 的光束也是 tickCount > 20 才开始结算
		if (tickCount == 20 && !level().isClientSide) {
			playSound(ModSounds.BEAM_GO.get(), 3.0F, 1.0F);
		}
		// 蓄力阶段不播粒子/镜头抖动：只在开火后（tickCount > 20）才出现，和别的开火特效同步。
		// LM 的 EnergyBeamEntity 也是 tickCount > 20 才画螺旋粒子。
		if (tickCount <= 20) return;
		if (!level().isClientSide) {
			CameraShakeEntity.cameraShake(level(), position(), 20.0F, 0.2F, 0, 2);
		} else {
			spawnSpiralParticles(origin, hitEnd);
		}
	}

	/** 与 LM 同名方法逐字一致：沿光束每 0.2 格撒一圈绕轴的 BEAM 粒子。 */
	private void spawnSpiralParticles(Vec3 from, Vec3 to) {
		Vec3 dir = to.subtract(from);
		double distance = dir.length();
		if (distance < 1.0E-4) return;
		dir = dir.normalize();
		for (double i = 0; i < distance; i += 0.2) {
			Vec3 center = from.add(dir.scale(i));
			double progress = i / distance;
			double angle = progress * 2 * Math.PI * 20;
			double offsetX = 0.7 * Math.cos(angle);
			double offsetY = 0.7 * Math.sin(angle);
			level().addParticle(ModParticles.BEAM.get(), center.x + offsetX, center.y + offsetY, center.z, 0, 0, 0);
		}
	}

	private void damageAlong(LivingEntity o, Vec3 from, Vec3 to) {
		if (!(o instanceof AbstractGolemEntity<?, ?> golem)) return;
		AABB box = new AABB(from, to).inflate(1.0);
		for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, box)) {
			if (e == golem || !GolemTargetUtil.canTarget(golem, e)) continue;
			if (e.getBoundingBox().inflate(0.5).clip(from, to).isEmpty()) continue;
			e.hurt(golem.damageSources().mobAttack(golem), damage);
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
	}

}
