package dev.xkmc.modulargolems.content.entity.misc;

import dev.xkmc.l2core.base.entity.BaseEntity;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemAimState;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.ranged.CannonPoseUtil;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGDamageTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SerialClass
public class BeaconLaserEntity extends BaseEntity implements OwnableEntity {

	public static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(BeaconLaserEntity.class, EntityDataSerializers.INT);

	@Nullable
	@SerialField
	public UUID owner;
	@SerialField
	public int life;
	@SerialField
	public float len;
	@SerialField
	public boolean right;
	@SerialField
	private Vec3 lastTarget = Vec3.ZERO;

	@Nullable
	public LivingEntity ownerCache;

	private final Set<LivingEntity> hit = new HashSet<>();


	public BeaconLaserEntity(EntityType<?> type, Level level) {
		super(type, level);
	}

	public BeaconLaserEntity(EntityType<?> type, Level level, LivingEntity owner, int life, boolean right) {
		super(type, level);
		this.owner = owner.getUUID();
		ownerCache = owner;
		entityData.set(OWNER_ID, owner.getId());
		this.life = life;
		this.right = right;
		setup(owner);
	}

	public void setup(LivingEntity le) {
		if (!(le instanceof MetalGolemEntity e)) return;
		var aim = e.getTargetAimPos();
		if (aim.isEmpty()) return;
		var hand = right ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
		var pos = CannonPoseUtil.BEACON.getOrigin(e, hand);
		var rot = CannonPoseUtil.BEACON.getAngle(MetalGolemAimState.of(e, 1), hand);
		var dst = aim.get().add(e.position()).subtract(pos).normalize().scale(35).add(pos);
		var hit = e.level().clip(new ClipContext(pos, dst, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
		len = (float) hit.getLocation().subtract(pos).length();
		lastTarget = hit.getLocation();
		setPos(pos);
		setYRot(rot[0] * Mth.RAD_TO_DEG);
		setXRot(rot[1] * Mth.RAD_TO_DEG);
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
		return false;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(OWNER_ID, -1);
	}

	@Override
	public void tick() {
		var owner = getOwner();
		if (!level().isClientSide() && (owner == null || !owner.isAlive() || tickCount > life)) {
			discard();
			return;
		}
		if (owner != null && tickCount == 0) setup(owner);
		super.tick();
		if (level() instanceof ServerLevel sl && owner instanceof MetalGolemEntity e) {
			var pos = position();
			var dst = lastTarget;
			var list = getEntityHitResult(level(), e, pos, dst, e.getScale() * 0.2f);
			var source = new DamageSource(level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(MGDamageTypes.BEACON), this, owner);
			float dmg = (float) e.getAttributeValue(Attributes.ATTACK_DAMAGE) * MGConfig.COMMON.beaconCannonDamageFactor.get().floatValue();
			for (var x : list) {
				if (x.hurtServer(sl, source, dmg)) {
					hit.add(x);
				}
			}
		}
	}

	public List<LivingEntity> getEntityHitResult(Level level, MetalGolemEntity owner, Vec3 start, Vec3 end, float radius) {
		var list = level.getEntities(EntityTypeTest.forClass(LivingEntity.class),
				new AABB(start, end).inflate(radius), x -> !hit.contains(x) && owner.canAttack(x) && owner.predicateTarget(x));
		var ans = new ArrayList<LivingEntity>();
		for (var e : list) {
			AABB aabb = e.getBoundingBox().inflate(radius);
			if (aabb.intersects(start, end) || aabb.contains(start) || aabb.contains(end)) {
				ans.add(e);
			}
		}
		return ans;
	}

	@Override
	public @Nullable EntityReference<LivingEntity> getOwnerReference() {
		return owner == null ? null : EntityReference.of(owner);
	}

	@Override
	public @Nullable LivingEntity getOwner() {
		if (ownerCache != null) return ownerCache;
		var ans = level().getEntity(entityData.get(OWNER_ID));
		if (ans instanceof LivingEntity le) {
			ownerCache = le;
		}
		return ownerCache;
	}

	@Override
	public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
		return true;
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double p_19883_) {
		return true;
	}

}
