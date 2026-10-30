package dev.xkmc.modulargolems.compat.materials.eeeab;

import com.eeeab.eeeabsmobs.sever.entity.effect.EntityCameraShake;
import com.eeeab.eeeabsmobs.sever.entity.effect.EntityElectromagnetic;
import com.eeeab.eeeabsmobs.sever.entity.effect.EntityGuardianBlade;
import dev.xkmc.modulargolems.compat.materials.eeeab.client.EEEABClientFx;
import com.eeeab.eeeabsmobs.sever.entity.effect.EntityGuardianLaser;
import com.eeeab.eeeabsmobs.sever.entity.effect.EntitySurge;
import com.eeeab.eeeabsmobs.sever.entity.effect.EntityTelegraph;
import com.eeeab.eeeabsmobs.sever.entity.util.ModEntityUtils;
import com.eeeab.eeeabsmobs.sever.entity.effect.projectile.EntityAnnihilatorMissile;
import com.eeeab.eeeabsmobs.sever.entity.util.damage.ModDamageSource;
import com.eeeab.eeeabsmobs.sever.entity.util.ShockWaveUtils;
import com.eeeab.eeeabsmobs.sever.init.EffectInit;
import com.eeeab.eeeabsmobs.sever.init.SoundInit;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Actual EEEAB implementations. All direct references to {@code com.eeeab.*} must stay here.
 * Called via {@link EEEABProxy} with try-catch.
 * Reference: {@code LMProxyImpl} for legendarymonsters.
 * <p>
 * This class runs on the server, so it must never mention a {@code com.eeeab.eeeabsmobs.client.*}
 * type inside a method body: the dedicated server has no {@code net.minecraft.client.*}.
 * type inside a method body: the dedicated server has no {@code net.minecraft.client.*} and the
 * first such call throws. Client side visuals live in {@link EEEABClientFx} and are reached through
 * an {@code isClientSide} check that returns before the call.
 */
public class EEEABProxyImpl {

	private static final double MISSILE_POD_HEIGHT_FACTOR = 0.65;
	private static final Vec3 UP = new Vec3(0, 1, 0);

	public static void spawnGuardianBladeBurst(LivingEntity golem) {
		if (golem.level().isClientSide) return;
		double y = golem.getY();
		float offset = (float) Math.toRadians(golem.getRandom().nextFloat() * 360.0F - 180.0F);
		for (int i = 0; i < 8; i++) {
			float f1 = (float) (golem.getYRot() + (i + offset) * Math.PI * 0.25);
			double x = golem.getX() + Mth.sin(f1) * 3.0;
			double z = golem.getZ() + Mth.cos(f1) * 3.0;
			EntityGuardianBlade blade = new EntityGuardianBlade(golem.level(), golem, x, y, z, f1, true);
			golem.level().addFreshEntity(blade);
		}
	}

	@Nullable
	public static Entity spawnGuardianLaser(LivingEntity golem, int lv) {
		if (golem.level().isClientSide) return null;
		int duration = 70; // original GuardianShootLaserGoal 70
		float base = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		MGGuardianLaserEntity laser = new MGGuardianLaserEntity(EEEABCompatRegistry.GUARDIAN_LASER.get(),
				golem.level(), golem, duration, base / 3.0F * (1 + 0.2F * (lv - 1)), 0);
		golem.level().addFreshEntity(laser);
		return laser;
	}

	public static void shootAnnihilatorMissile(LivingEntity golem, LivingEntity target) {
		if (golem.level().isClientSide) return;
		Vec3 forward = horizontalForward(golem);
		Vec3 right = forward.cross(UP);
		Vec3 aimPos = target.position().add(0, target.getBbHeight() * 0.1, 0);
		int sideCount = golem.getHealth() < golem.getMaxHealth() * 0.5F ? 5 : 4;
		double podHeight = golem.getBbHeight() * MISSILE_POD_HEIGHT_FACTOR;
		Vec3 toTarget = aimPos.subtract(golem.position().add(0, podHeight, 0));
		Vec3 bias = toTarget.lengthSqr() < 1.0E-4F ? forward : toTarget.normalize();
		for (int side = -1; side <= 1; side += 2) {
			for (int i = 0; i < sideCount; i++) {
				Vec3 launch = forward.scale(-0.5).add(UP.scale(podHeight)).add(right.scale(side * 0.6));
				EntityAnnihilatorMissile missile = new EntityAnnihilatorMissile(golem.level(), golem, EntityAnnihilatorMissile.ElementType.SPARKFERNO);
				missile.setTargetPos(target, aimPos.offsetRandom(golem.getRandom(), 5.0F));
				missile.moveTo(golem.getX() + launch.x, golem.getY() + launch.y, golem.getZ() + launch.z, golem.getYRot(), golem.getXRot());
				Vec3 motion = new Vec3(side, 2.5, 0).normalize().add(bias.scale(0.8)).normalize();
				missile.shoot(motion.x, motion.y, motion.z, 0.14F + golem.getRandom().nextFloat() * 0.02F, 10F);
				golem.level().addFreshEntity(missile);
			}
		}
		golem.playSound(SoundInit.REALM_WARDEN_LAUNCH.get(), 1.5F, 1F);
	}

	private static Vec3 horizontalForward(LivingEntity entity) {
		Vec3 look = entity.getForward();
		Vec3 flat = new Vec3(look.x, 0, look.z);
		return flat.lengthSqr() < 1.0E-8F ? new Vec3(0, 0, 1) : flat.normalize();
	}

	@Nullable
	public static Entity spawnAnnihilatorLaser(LivingEntity golem) {
		if (golem.level().isClientSide) return null;
		MGGuardianLaserEntity laser = new MGGuardianLaserEntity(EEEABCompatRegistry.GUARDIAN_LASER.get(),
				golem.level(), golem, 20, (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE), 1);
		golem.level().addFreshEntity(laser);
		golem.playSound(SoundInit.REALM_WARDEN_BLAST.get(), 1.5F, 1F);
		return laser;
	}

	public static void spawnElectromagneticBurst(LivingEntity golem, int lv) {
		if (golem.level().isClientSide) return;
		golem.playSound(SoundInit.REALM_WARDEN_SHAKE_GROUND.get(), 1.2F, 1.2F);
		Vec3 look = golem.getForward().normalize();
		Vec3 poundPos = golem.position().add(look.scale(2.25)).add(0, 0.2, 0);
		float offset = (float) Math.toRadians(golem.getRandom().nextFloat() * 360.0F - 180.0F);
		int n = 4 + lv * 2;
		for (int i = 0; i < n; i++) {
			float f1 = (float) (golem.getYRot() + (i + offset) * Math.PI * 2 / n);
			Vec3 spawnPos = new Vec3(poundPos.x, poundPos.y, poundPos.z);
			float yawDeg = f1 * Mth.RAD_TO_DEG - 90.0F;
			EntityElectromagnetic.shoot(golem.level(), golem, spawnPos, 2.0F, 10, 5, yawDeg, false);
		}
	}

	public static void realmWardenJumpSmashServerEffect(LivingEntity golem, Vec3 pos, float scale, boolean secondPhase) {
		if (golem.level().isClientSide) return;
		golem.playSound(SoundInit.REALM_WARDEN_SHAKE_GROUND.get(), 1.2F, 1.2F);
		if (secondPhase) golem.playSound(SoundInit.REALM_WARDEN_SHOCK.get(), 1F, 1.3F);
		EntityCameraShake.cameraShake(golem.level(), pos, 24, 0.2F * scale, 4, 6);
	}

	public static void realmWardenJumpSmashClientEffect(LivingEntity golem, Vec3 pos, float scale, float yBlockPtcScale,
			boolean dust, boolean ring, @Nullable double[] angles, boolean secondPhase) {
		if (golem.level().isClientSide) EEEABClientFx.jumpSmash(golem, pos, scale, yBlockPtcScale, dust, ring, angles, secondPhase);
	}

	public static void realmWardenJumpSmashDerivedEffect(LivingEntity golem, Vec3 pos) {
		if (golem.level().isClientSide) EEEABClientFx.jumpSmashDerived(golem, pos);
	}

	public static void realmWardenLeapTelegraph(LivingEntity golem, Vec3 pos, boolean secondPhase, float scale) {
		EntityTelegraph.spawn(golem.level(), pos, secondPhase ? 15 : 10,
				secondPhase ? 0xFF0080FF : 0xFFFF2020, scale);
	}

	public static void realmWardenLeapImpactEffect(LivingEntity golem, boolean slam) {
		Level level = golem.level();
		if (level.isClientSide) {
			EEEABClientFx.leapImpact(golem, slam);
			return;
		}
		if (slam) {
			golem.playSound(SoundInit.REALM_WARDEN_ATTACK.get(), 1.5F, 1F);
			EntityCameraShake.cameraShake(level, golem.position(), 24, 0.2F, 6, 5);
		} else {
			golem.playSound(SoundInit.REALM_WARDEN_LANDING.get());
			golem.playSound(SoundInit.REALM_WARDEN_SHAKE_GROUND.get(), 1.5F, 1.2F);
			EntityCameraShake.cameraShake(level, golem.position(), 24, 0.2F, 4, 6);
		}
	}

	public static void realmWardenLeapLandingEffect(LivingEntity golem, boolean secondPhase, boolean slam) {
		if (golem.level().isClientSide) EEEABClientFx.leapLanding(golem, secondPhase, slam);
	}

	public static void realmWardenLeapShock(LivingEntity golem) {
		// the sound plays for everyone; the bolt geometry has its own event, see realmWardenLeapExplosion
		golem.playSound(SoundInit.REALM_WARDEN_SHOCK.get(), 1.5F, 1.2F);
		if (golem.level().isClientSide) EEEABClientFx.leapShock(golem);
	}

	/**
	 * The client half of the landing explosion, fired once per tick for the four ticks the original runs it
	 * on, so the burst keeps feeding instead of arriving as a single frame.
	 */
	public static void realmWardenLeapExplosion(LivingEntity golem) {
		if (golem.level().isClientSide) EEEABClientFx.leapLandingExplosion(golem);
	}

	/**
	 * Server half of the landing explosion: three concentric rings of {@code EntitySurge} at radius 4, 8
	 * and 12, four, seven and ten of them. Each ring is offset in angle and staggered by a warm-up delay,
	 * so the damage walks outward rather than landing all at once.
	 */
	public static void realmWardenLeapSurgeRing(LivingEntity golem) {
		if (golem.level().isClientSide) return;
		RandomSource random = golem.getRandom();
		Vec3 center = golem.position();
		double angleStepBase = random.nextFloat() * Math.PI;
		for (int layer = 0; layer < 3; layer++) {
			double radius = 4 + layer * 4;
			int count = 4 + layer * 3;
			double step = 2 * Math.PI / count;
			double offset = layer * angleStepBase;
			for (int i = 0; i < count; i++) {
				float angle = (float) (offset + i * step);
				double x = center.x + Math.cos(angle) * radius;
				double z = center.z + Math.sin(angle) * radius;
				// drop onto whatever surface is under the spot so the surge does not hang in the air
				Vec3 point = ModEntityUtils.checkSummonEntityPointNullable(golem.level(), x, z, center.y - 2, center.y + 2);
				if (point == null) point = new Vec3(x, golem.getY(), z);
				int warmup = layer * 7 + random.nextInt(15);
				golem.level().addFreshEntity(new EntitySurge(golem.level(), point.x, point.y, point.z, angle, warmup, golem));
			}
		}
	}

	/**
	 * The ring of ground blocks the leap throws up on landing. The original runs it on both feet,
	 * offsetting by {@code getBbWidth()} to either side of {@code yBodyRot}, which is what makes it read as
	 * two overlapping rings rather than one.
	 * <p>
	 * Server side only: it spawns real {@code EntityFallingBlock} entities, and the damage is the caller's
	 * business, so the returned entity list is deliberately ignored exactly as the original ignores it.
	 */
	public static void realmWardenLeapRingShockWave(LivingEntity golem) {
		if (golem.level().isClientSide) return;
		double yaw = Math.toRadians(golem.yBodyRot);
		double x = Math.cos(yaw) * golem.getBbWidth(), z = Math.sin(yaw) * golem.getBbWidth();
		ShockWaveUtils.doRingShockWave(golem, golem.position().add(x, 0, z), 2, 0.05F, false, 10);
		ShockWaveUtils.doRingShockWave(golem, golem.position().subtract(x, 0, z), 2, 0.05F, false, 10);
	}

	public static void realmWardenLeapImpactDamage(LivingEntity golem, LivingEntity target, float damage,
			int disableShieldTime, boolean bypassArmor, boolean secondPhase) {
		DamageSource source = bypassArmor ? ModDamageSource.bypassArmor(golem) : golem.damageSources().mobAttack(golem);
		if (secondPhase && target.hasEffect(EffectInit.ELECTRIFIED_EFFECT.get())) {
			MobEffectInstance effect = target.getEffect(EffectInit.ELECTRIFIED_EFFECT.get());
			damage += Mth.clamp((effect.getAmplifier() + 1) * 0.05F, 0F, 0.5F);
		}
		boolean hurt = target.hurt(source, damage);
		if (!hurt && disableShieldTime > 0 && target instanceof Player player && player.isBlocking()) {
			player.getCooldowns().addCooldown(player.getUseItem().getItem(), disableShieldTime);
			player.stopUsingItem();
			player.level().broadcastEntityEvent(player, (byte) 30);
		}
	}

	}