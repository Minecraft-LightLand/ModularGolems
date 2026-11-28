package dev.xkmc.modulargolems.compat.materials.eeeab.realm;

import dev.xkmc.modulargolems.compat.materials.eeeab.EEEABProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;
import java.util.function.Consumer;

/**
 * {@code LEAP_LANDING_ANIMATION} of {@code EntityRealmWarden}, driven by the shared
 * {@link EarthquakeHelper} jump. Landing deals shieldable damage, then an armor ignoring shockwave follows.
 * <p>
 * Below half health the slam is followed by {@code doLandingExplosionEffect}: bolts fan out of both feet
 * and three rings of surges walk outward from the landing spot.
 */
public class RealmWardenJumpSmashModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	private static final String KEY_SLAM = "RealmLeapSlamTick";
	private static final String KEY_EXPLOSION = "RealmLandingExplosionTick";
	private static final String KEY_TELEGRAPH = "RealmLeapTelegraphTick";
	private static final String KEY_AIR_TELEGRAPH = "RealmLeapAirTelegraphTick";
	private static final String KEY_AIR_X = "RealmLeapAirX";
	private static final String KEY_AIR_Z = "RealmLeapAirZ";
	private static final String KEY_TELEGRAPH_ENTITY = "RealmLeapTelegraphEntity";

	/** 落地 telegraph 延迟（tick）：之前 10 偏早，稍微往后挪，贴近实际落地/触发时机。 */
	private static final int TELEGRAPH_DELAY = 16;

	/** 空中（起跳后）telegraph 延迟（tick）：起跳瞬间生成太早，稍后一点再显示。 */
	private static final int AIR_TELEGRAPH_DELAY = 8;

	private static final byte EVENT_SLAM = 86;
	private static final byte EVENT_LANDING_EXPLOSION = 89;

	private static final int SLAM_DELAY = 35;

	/**
	 * The landing explosion is fed over four ticks, matching {@code inRange(40, 43)}.
	 */
	private static final int EXPLOSION_DELAY = 4;

	private static final double TELEGRAPH_SCALE = 15.0;
	private static final double IMPACT_RANGE = 10.0;
	private static final double SUMMON_RANGE = 16.0;

	public RealmWardenJumpSmashModifier() {
		super(StatFilterType.MOVEMENT, 1);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> cons) {
		cons.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (isLeaping(entity)) event.setAmount(event.getAmount() * EarthquakeHelper.LEAP_DAMAGE_MULT);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var reduction = EarthquakeHelper.leapReduction();
		return List.of(Component.translatable(getDescriptionId() + ".desc", reduction)
				.withStyle(ChatFormatting.GREEN));
	}

	private static boolean isLeaping(AbstractGolemEntity<?, ?> golem) {
		// the shockwave follows a short delay, so the golem has already landed by then
		return EarthquakeHelper.isLeaping(golem) || golem.getPersistentData().getInt(KEY_SLAM) > 0;
	}

	private static boolean isSecondPhase(AbstractGolemEntity<?, ?> golem) {
		return golem.getHealth() < golem.getMaxHealth() * 0.5F;
	}

	@Override
	public void handleEvent(AbstractGolemEntity<?, ?> golem, int value, byte event) {
		if (!golem.level().isClientSide) return;
		boolean secondPhase = isSecondPhase(golem);
		if (event == EarthquakeHelper.FLAG) {
			// boss tick 5: doLeapLandingEffect(60, 1, 1.4, {45,25})
			EEEABProxy.realmWardenLeapLandingFull(golem, 60F, 1.0F, 1.4F, new double[]{45, 25}, secondPhase);
		} else if (event == EVENT_SLAM) {
			// boss tick 40: doLeapLandingEffect(120, 1.4, 1.2, {55,25})
			EEEABProxy.realmWardenLeapLandingFull(golem, 120F, 1.4F, 1.2F, new double[]{55, 25}, secondPhase);
		} else if (event == EVENT_LANDING_EXPLOSION) {
			// boss inRange(40,43): phase one ribbon particles, phase two bolts
			EEEABProxy.realmWardenLeapExplosion(golem, secondPhase);
		}
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		var data = golem.getPersistentData();
		landingExplosion(golem);
		airTelegraphTick(golem, data);
		telegraphTick(golem, data);
		followTelegraph(golem, data);
		int timer = data.getInt(KEY_SLAM);
		if (timer <= 0) {
			data.remove(KEY_SLAM);
			return;
		}
		pullNearby(golem);
		timer--;
		data.putInt(KEY_SLAM, timer);
		if (timer > 0) return;
		data.remove(KEY_SLAM);
		// 砸地时移除跟随的落地 telegraph
		if (data.hasUUID(KEY_TELEGRAPH_ENTITY) && golem.level() instanceof net.minecraft.server.level.ServerLevel sl) {
			var telegraph = sl.getEntity(data.getUUID(KEY_TELEGRAPH_ENTITY));
			if (telegraph != null) telegraph.discard();
		}
		data.remove(KEY_TELEGRAPH_ENTITY);
		boolean secondPhase = isSecondPhase(golem);
		EEEABProxy.realmWardenLeapImpactEffect(golem, true);
		// the ring of ground blocks goes up on the slam tick in both phases, not just the second one
		EEEABProxy.realmWardenLeapRingShockWave(golem);
		if (secondPhase) EEEABProxy.realmWardenLeapShock(golem);
		golem.level().broadcastEntityEvent(golem, EVENT_SLAM);
		slamDamage(golem, secondPhase);
		if (secondPhase) {
			// the outward burst of rings, the server half of the landing explosion
			EEEABProxy.realmWardenLeapSurgeRing(golem);
		}
		data.putInt(KEY_EXPLOSION, EXPLOSION_DELAY);
	}

	private void telegraphTick(AbstractGolemEntity<?, ?> golem, net.minecraft.nbt.CompoundTag data) {
		int timer = data.getInt(KEY_TELEGRAPH);
		if (timer <= 0) {
			data.remove(KEY_TELEGRAPH);
			return;
		}
		timer--;
		if (timer > 0) {
			data.putInt(KEY_TELEGRAPH, timer);
			return;
		}
		data.remove(KEY_TELEGRAPH);
		var telegraph = EEEABProxy.realmWardenLeapTelegraph(golem, landingPos(golem), isSecondPhase(golem), 15F, (float) TELEGRAPH_SCALE);
		if (telegraph != null) data.putUUID(KEY_TELEGRAPH_ENTITY, telegraph.getUUID());
	}

	/** 让落地 telegraph 跟随傀儡（砸地范围随傀儡移动）。 */
	private void followTelegraph(AbstractGolemEntity<?, ?> golem, net.minecraft.nbt.CompoundTag data) {
		if (!data.hasUUID(KEY_TELEGRAPH_ENTITY)) return;
		if (!(golem.level() instanceof net.minecraft.server.level.ServerLevel sl)) return;
		var telegraph = sl.getEntity(data.getUUID(KEY_TELEGRAPH_ENTITY));
		if (telegraph == null || telegraph.isRemoved()) {
			data.remove(KEY_TELEGRAPH_ENTITY);
			return;
		}
		Vec3 pos = landingPos(golem);
		telegraph.setPos(pos.x, pos.y, pos.z);
	}

	private void airTelegraphTick(AbstractGolemEntity<?, ?> golem, net.minecraft.nbt.CompoundTag data) {
		int timer = data.getInt(KEY_AIR_TELEGRAPH);
		if (timer <= 0) {
			data.remove(KEY_AIR_TELEGRAPH);
			return;
		}
		timer--;
		if (timer > 0) {
			data.putInt(KEY_AIR_TELEGRAPH, timer);
			return;
		}
		data.remove(KEY_AIR_TELEGRAPH);
		double x = data.getDouble(KEY_AIR_X);
		double z = data.getDouble(KEY_AIR_Z);
		data.remove(KEY_AIR_X);
		data.remove(KEY_AIR_Z);
		EEEABProxy.realmWardenLeapTelegraph(golem, landingPosAt(golem, x, z), false, 10F, 10F);
	}

	/**
	 * {@code inRange(40, 43)} of the original: the client keeps drawing bolts for four ticks while the
	 * server only spawned its rings on the first one.
	 */
	private void landingExplosion(AbstractGolemEntity<?, ?> golem) {
		var data = golem.getPersistentData();
		int timer = data.getInt(KEY_EXPLOSION);
		if (timer <= 0) return;
		data.putInt(KEY_EXPLOSION, timer - 1);
		if (timer <= 1) data.remove(KEY_EXPLOSION);
		golem.level().broadcastEntityEvent(golem, EVENT_LANDING_EXPLOSION);
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		boolean secondPhase = isSecondPhase(golem);
		golem.getPersistentData().putInt(KEY_SLAM, SLAM_DELAY);
		golem.getPersistentData().putInt(KEY_TELEGRAPH, TELEGRAPH_DELAY);
		EEEABProxy.realmWardenLeapImpactEffect(golem, false);
		impactDamage(golem, secondPhase);
	}

	/**
	 * The telegraph marks the landing spot on the ground, but this skill triggers while the golem is still
	 * hovering, so its own position is in mid air and has to be projected down onto the surface below.
	 */
	private static Vec3 landingPos(AbstractGolemEntity<?, ?> golem) {
		return landingPosAt(golem, golem.getX(), golem.getZ());
	}

	private static Vec3 landingPosAt(AbstractGolemEntity<?, ?> golem, double x, double z) {
		int groundY = golem.level().getHeight(Heightmap.Types.WORLD_SURFACE, Mth.floor(x), Mth.floor(z));
		return new Vec3(x, groundY + 0.1, z);
	}

	/**
	 * boss shows a radius-10 red telegraph on the landing spot while still airborne
	 * ({@code EntityRealmWarden} leap hold, hoverTimer 20), so the target can see where it will land.
	 */
	@Override
	public void performJump(AbstractGolemEntity<?, ?> golem, int lv) {
		EarthquakeHelper.performDefaultJump(golem);
		var target = golem.getTarget();
		if (target != null) {
			// 起跳瞬间就显示空中 telegraph 太早，稍后按落点再生成
			var data = golem.getPersistentData();
			data.putDouble(KEY_AIR_X, target.getX());
			data.putDouble(KEY_AIR_Z, target.getZ());
			data.putInt(KEY_AIR_TELEGRAPH, AIR_TELEGRAPH_DELAY);
		}
	}

	private void pullNearby(AbstractGolemEntity<?, ?> golem) {
		List<LivingEntity> nearby = golem.level().getEntitiesOfClass(LivingEntity.class,
				golem.getBoundingBox().inflate(SUMMON_RANGE), e -> GolemTargetUtil.canTarget(golem, e));
		for (LivingEntity entity : nearby) {
			Vec3 diff = entity.position().subtract(golem.position()).normalize().scale(0.065);
			entity.setDeltaMovement(entity.getDeltaMovement().subtract(diff));
		}
	}

	private void impactDamage(AbstractGolemEntity<?, ?> golem, boolean secondPhase) {
		for (LivingEntity target : targetsInRange(golem, IMPACT_RANGE)) {
			EEEABProxy.realmWardenLeapImpactDamage(golem, target, attack(golem) * 1.0F, 0, false, secondPhase);
			EarthquakeHelper.launch(golem, target, 0.75F);
		}
	}

	private void slamDamage(AbstractGolemEntity<?, ?> golem, boolean secondPhase) {
		for (LivingEntity target : targetsInRange(golem, TELEGRAPH_SCALE)) {
			EEEABProxy.realmWardenLeapImpactDamage(golem, target, attack(golem) * 1.2F, 100, true, secondPhase);
			EarthquakeHelper.launch(golem, target, 1.0F);
		}
	}

	private static float attack(AbstractGolemEntity<?, ?> golem) {
		return (float) golem.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
	}

	private static List<LivingEntity> targetsInRange(AbstractGolemEntity<?, ?> golem, double range) {
		Vec3 pos = golem.position();
		AABB area = new AABB(pos.x - range, pos.y - range, pos.z - range, pos.x + range, pos.y + range, pos.z + range);
		return golem.level().getEntitiesOfClass(LivingEntity.class, area, e -> GolemTargetUtil.canTarget(golem, e));
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return 25 * 25;
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		return 160;
	}

}
