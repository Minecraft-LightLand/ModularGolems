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

	private static final byte EVENT_SLAM = 86;
	private static final byte EVENT_LANDING_EXPLOSION = 89;

	private static final int SLAM_DELAY = 20;

	/**
	 * The landing explosion is fed over four ticks, matching {@code inRange(40, 43)}.
	 */
	private static final int EXPLOSION_DELAY = 4;

	/**
	 * Landing damage, also reported by the skill tooltip.
	 */
	public static final float IMPACT_DAMAGE = 15.0F;

	/**
	 * Delayed shockwave damage, also reported by the skill tooltip.
	 */
	public static final float SLAM_DAMAGE = 18.0F;
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
		if (event == EarthquakeHelper.FLAG) {
			EEEABProxy.realmWardenLeapImpactEffect(golem, false);
			EEEABProxy.realmWardenLeapLandingEffect(golem, isSecondPhase(golem), false);
		} else if (event == EVENT_SLAM) {
			EEEABProxy.realmWardenLeapImpactEffect(golem, true);
			EEEABProxy.realmWardenLeapLandingEffect(golem, isSecondPhase(golem), true);
			if (isSecondPhase(golem)) EEEABProxy.realmWardenLeapShock(golem);
		} else if (event == EVENT_LANDING_EXPLOSION) {
			if (isSecondPhase(golem)) EEEABProxy.realmWardenLeapExplosion(golem);
		}
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		var data = golem.getPersistentData();
		landingExplosion(golem);
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
		EEEABProxy.realmWardenLeapTelegraph(golem, landingPos(golem), secondPhase, (float) TELEGRAPH_SCALE);
		EEEABProxy.realmWardenLeapImpactEffect(golem, false);
		impactDamage(golem, secondPhase);
	}

	/**
	 * The telegraph marks the landing spot on the ground, but this skill triggers while the golem is still
	 * hovering, so its own position is in mid air and has to be projected down onto the surface below.
	 */
	private static Vec3 landingPos(AbstractGolemEntity<?, ?> golem) {
		int groundY = golem.level().getHeight(Heightmap.Types.WORLD_SURFACE, Mth.floor(golem.getX()), Mth.floor(golem.getZ()));
		return new Vec3(golem.getX(), groundY + 0.1, golem.getZ());
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
			EEEABProxy.realmWardenLeapImpactDamage(golem, target, IMPACT_DAMAGE, 100, false, secondPhase);
			EarthquakeHelper.launch(golem, target, 0.75F);
		}
	}

	private void slamDamage(AbstractGolemEntity<?, ?> golem, boolean secondPhase) {
		for (LivingEntity target : targetsInRange(golem, TELEGRAPH_SCALE)) {
			EEEABProxy.realmWardenLeapImpactDamage(golem, target, SLAM_DAMAGE, 100, true, secondPhase);
			EarthquakeHelper.launch(golem, target, 1.0F);
		}
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
