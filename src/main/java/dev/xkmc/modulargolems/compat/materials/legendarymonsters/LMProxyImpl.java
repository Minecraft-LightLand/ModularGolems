package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.AnimatedEntity.TheObliteratorCloneWithArmsEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.*;
import net.miauczel.legendary_monsters.entity.ModEntities;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LMProxyImpl {

	public static void shake(LivingEntity user, Vec3 pos) {
		CameraShakeEntity.cameraShake(user.level(), pos, 15.0F, 0.3F, 0, 15);
	}

	public static List<LivingEntity> stun(ServerLevel level, double x, double y, double z, LivingEntity golem, float reach, int lv) {
		List<LivingEntity> affected = new ArrayList<>();

		int n = 128;
		for (double i = 0; i < n; ++i) {
			var a = (Math.PI * 2D) / n * i;
			level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
					x + 0.5F + Math.cos(a) * reach,
					y,
					z + 0.5F + Math.sin(a) * reach,
					0, 0.0F, 0.05, 0.0F, 1);
		}

		level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.NEUTRAL, 1.0F, 1.0F);
		Vec3 cen = new Vec3(x, y, z);
		for (LivingEntity le : level.getEntitiesOfClass(LivingEntity.class, (new AABB(cen, cen)).inflate(reach), e -> e != golem)
				.stream().sorted(Comparator.comparingDouble(ec -> ec.distanceToSqr(cen))).toList()) {
			if (golem.isAlliedTo(le)) continue;
			le.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 20 * lv, 255, false, false));
			le.hurt(golem.damageSources().mobAttack(golem), (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE) * lv * 0.4f);
			affected.add(le);
		}

		return affected;
	}

	public static void performThunderAttack(Mob e, LivingEntity target, int lv) {

		spawnBoltStrip(e, target, 8 + 8 * lv, 4 + 3 * lv);

	}

	public static void spawnElectricShock(Entity attacker, LivingEntity entity, float damage, int n) {

		var dir = attacker.position().subtract(entity.position()).normalize();
		double val = (dir.x * dir.x + dir.z * dir.z);
		Vec3 ax0 = val < 1e-4 ? new Vec3(1, 0, 0) :
				new Vec3(-dir.x * dir.y, val, -dir.z * dir.y).normalize();
		Vec3 ax1 = dir.cross(ax0).normalize();
		for (int i = 0; i < n; i++) {
			double rad = Math.PI * 2 / n * i;
			var vec = ax1.scale(Math.sin(rad)).add(dir.scale(Math.cos(rad)));
			float angle = (float) (Math.atan2(vec.z, vec.x) * Mth.RAD_TO_DEG);
			var e = createElectricity(entity, vec, angle, damage);
			Vec3 pos = entity.position().add(vec);
			e.setPos(pos.x, pos.y, pos.z);
			entity.level().addFreshEntity(e);
		}

	}

	private static void spawnBoltStrip(Mob e, LivingEntity target, int step, int dmg) {
		double d0 = Math.min(target.getY(), e.getY());
		double d1 = Math.max(target.getY(), e.getY()) + (double) 2.0F;
		float angle = (float) Mth.atan2(target.getZ() - e.getZ(), target.getX() - e.getX());
		for (int l = 0; l < step; ++l) {
			double len = (double) 1.25F * (double) (l + 1);
			int delay = (int) (1.25F * (float) l);
			var x = e.getX() + Mth.cos(angle) * len;
			var z = e.getZ() + Mth.sin(angle) * len;
			spawnSingleBolt(e, x, z, d0, d1, angle, delay, dmg);
		}

	}

	private static void spawnSingleBolt(Mob e, double x, double z, double minY, double maxY, float rotation, int delay, int dmg) {
		BlockPos pos = new BlockPos((int) x, (int) maxY, (int) z);
		boolean flag = false;
		double d0 = 0.0F;

		do {
			BlockPos low = pos.below();
			BlockState state = e.level().getBlockState(low);
			if (state.isFaceSturdy(e.level(), low, Direction.UP)) {
				if (!e.level().isEmptyBlock(pos)) {
					BlockState topState = e.level().getBlockState(pos);
					VoxelShape topShape = topState.getCollisionShape(e.level(), pos);
					if (!topShape.isEmpty()) {
						d0 = topShape.max(Direction.Axis.Y);
					}
				}

				flag = true;
				break;
			}

			pos = pos.below();
		} while (pos.getY() >= Mth.floor(minY) - 1);

		if (flag) {
			e.level().addFreshEntity(new LightningBoltEntity(e.level(), x, (double) pos.getY() + d0, z, rotation, delay, e, 20, dmg));
		}

	}

	private static Entity createElectricity(LivingEntity entity, Vec3 vec, float angle, float damage) {
		return new ElectricityEntity(entity, vec.x, vec.y, vec.z, entity.level(), damage, angle, 20.0F);
	}

	// Obliterator: large bomb (single-target) - AnnihilationBombEntity - single proxy
	public static void spawnObliteratorLargeBomb(LivingEntity golem, LivingEntity target, int lv) {
		if (golem.level().isClientSide) return;
		float base = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float damage = base * (0.9f + lv * 0.25f);
		@SuppressWarnings("unchecked")
		EntityType<AnnihilationBombEntity> type = (EntityType<AnnihilationBombEntity>) (EntityType<?>) ModEntities.ANNIHILATION_BOMB_ENTITY.get();
		AnnihilationBombEntity bomb = new AnnihilationBombEntity(type, golem.level(), golem, damage, 7 + lv * 3, false);
		bomb.moveTo(golem.getX(), golem.getEyeY(), golem.getZ(), golem.getYRot(), golem.getXRot());
		double d0 = target.getX() - bomb.getX();
		double d1 = target.getY() + target.getBbHeight() * 0.5 - bomb.getY();
		double d2 = target.getZ() - bomb.getZ();
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		float velocity = 1.0f;
		float inaccuracy = (float) (14 - golem.level().getDifficulty().getId() * 4) * 0.0f;
		bomb.shoot(d0, d1 + d3 * 0.2, d2, velocity, inaccuracy);
		bomb.setOwner(golem);
		golem.level().addFreshEntity(bomb);
	}

	// Obliterator: small bomb (multi-target) - SmallAnnihilationBombEntity - single per target, multi overall
	public static void spawnObliteratorSmallBomb(LivingEntity golem, LivingEntity target, int lv) {
		if (golem.level().isClientSide) return;
		float base = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float damage = base * (0.5f + lv * 0.15f);
		@SuppressWarnings("unchecked")
		EntityType<SmallAnnihilationBombEntity> type = (EntityType<SmallAnnihilationBombEntity>) (EntityType<?>) ModEntities.SMALL_ANNIHILATION_BOMB_ENTITY.get();
		SmallAnnihilationBombEntity bomb = new SmallAnnihilationBombEntity(type, golem.level(), golem, damage);
		bomb.setTurnRate(0.0f);
		bomb.moveTo(golem.getX(), golem.getEyeY(), golem.getZ(), golem.getYRot(), golem.getXRot());
		double d0 = target.getX() - bomb.getX();
		double d1 = target.getY() + target.getBbHeight() * 0.5 - bomb.getY();
		double d2 = target.getZ() - bomb.getZ();
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		bomb.shoot(d0, d1 + d3 * 0.2, d2, 1.2f, 0.0f);
		bomb.setOwner(golem);
		golem.level().addFreshEntity(bomb);
	}

	// Obliterator: plasma orb (multi-target) - PlasmaOrbEntity
	public static void spawnObliteratorPlasmaOrb(LivingEntity golem, LivingEntity target, int lv) {
		if (golem.level().isClientSide) return;
		float base = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float damage = base * (0.6f + lv * 0.15f);
		double dxRaw = target.getX() - golem.getX();
		double dzRaw = target.getZ() - golem.getZ();
		double len = Math.sqrt(dxRaw * dxRaw + dzRaw * dzRaw);
		if (len < 1e-4) {
			dxRaw = 1;
			dzRaw = 0;
			len = 1;
		}
		dxRaw /= len;
		dzRaw /= len;
		float angle = (float) (Math.atan2(-dxRaw, dzRaw) * Mth.RAD_TO_DEG);
		PlasmaOrbEntity orb = new PlasmaOrbEntity(golem, dxRaw, 0.0, dzRaw, golem.level(), damage, angle, 20.0f);
		double spawnX = golem.getX();
		double spawnY = golem.getEyeY() - 0.2;
		double spawnZ = golem.getZ();
		orb.setPos(spawnX, spawnY, spawnZ);
		orb.setTurnLeft(golem.getRandom().nextBoolean());
		orb.setTurnStrength(1.0f);
		golem.level().addFreshEntity(orb);
	}

	// Obliterator: laser (single-target) - AnnihilationBeamEntity - single proxy
	@javax.annotation.Nullable
	public static Entity spawnObliteratorLaser(LivingEntity golem, LivingEntity target, int lv) {
		if (golem.level().isClientSide) return null;
		float base = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float damage = base * (1.0f + lv * 0.25f);
		int duration = 20;
		float yRot = golem.getYRot() + 90.0f;
		float xRot = -golem.getXRot();
		float f = Mth.cos(golem.getYRot() * Mth.DEG_TO_RAD);
		float f1 = Mth.sin(golem.getYRot() * Mth.DEG_TO_RAD);
		double theta = Math.toRadians(golem.getYRot());
		double vecX = Math.cos(theta + Math.PI / 2);
		double vecZ = Math.sin(theta + Math.PI / 2);
		float vec = 2.0f;
		float offset = 0.0f;
		double spawnX = golem.getX() + vec * vecX + f * offset;
		double spawnZ = golem.getZ() + vec * vecZ + f1 * offset;
		double spawnY = golem.getY() + 2.0;
		@SuppressWarnings("unchecked")
		EntityType<AnnihilationBeamEntity> type = (EntityType<AnnihilationBeamEntity>) (EntityType<?>) ModEntities.ANNIHILATION_BEAM.get();
		AnnihilationBeamEntity beam = new AnnihilationBeamEntity(type, golem.level(), golem, spawnX, spawnY, spawnZ, yRot, xRot, duration, damage, 5.0f, 1, false, 0.0f, 0.0f, 0.0f, false, 30.0f);
		golem.level().addFreshEntity(beam);
		return beam;
	}

	// Obliterator: jump ground charge quake - replicates state 22:3023 proxies + visuals
	public static void spawnObliteratorJumpGroundChargeQuake(LivingEntity golem, int lv) {
		Level level = golem.level();
		float base = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float flameDamage = base * (0.6f + lv * 0.15f);
		float plasmaDamage = base * (0.6f + lv * 0.15f);
		// visuals: CameraShake + doSmashEffects (Circle Ring) + controlledSmashParticles
		CameraShakeEntity.cameraShake(level, golem.position(), 20.0F, 0.15F, 0, 20);
		if (level instanceof ServerLevel sl) {
			// Circle RingData GROW 60 like doSmashEffects:5128
			float g = (float) Math.toRadians(-golem.getXRot() + 180.0F);
			ParticleOptions ring = new Circle.RingData(g, 0.0f, 30, 0.0f, 1.0f, 0.0f, 1.0f, 60.0f, true, Circle.EnumRingBehavior.GROW_THEN_SHRINK);
			sl.sendParticles(ring, golem.getX(), golem.getY(), golem.getZ(), 1, 0, 0, 0, 0);
			// additional ring at 88 ticks
			ParticleOptions ring2 = new Circle.RingData(0.0f, 1.5707964f, 20, 0.0f, 1.0f, 0.0f, 1.0f, 100.0f, false, Circle.EnumRingBehavior.GROW);
			sl.sendParticles(ring2, golem.getX(), golem.getY() + 0.1, golem.getZ(), 1, 0, 0, 0, 0);
		}
		level.playSound(null, BlockPos.containing(golem.getX(), golem.getY(), golem.getZ()), ModSounds.HUGE_ENERGY_EXPLOSION.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
		if (level.isClientSide) return;
		// proxies: 6 flames circular multiplier 4.0 size 2.0 (3044)
		double multiplier = 4.0;
		float size = 2.0f;
		int amount = 6;
		int floorY = Mth.floor(golem.getY());
		for (int k = 0; k < amount; ++k) {
			float f33 = (float) k * (float) Math.PI * size / (float) amount + (float) Math.PI * size / 10.0f;
			double fx = golem.getX() + Mth.cos(f33) * multiplier;
			double fz = golem.getZ() + Mth.sin(f33) * multiplier;
			spawnObliteratorFlame(golem, fx, fz, floorY, golem.getY() + 1.0, f33, 2, flameDamage);
		}
		// proxy: 1 armed clone offset 3 left/right (3058) - use golem target if present else random offset
		boolean right = golem.getRandom().nextBoolean();
		float f = Mth.cos(golem.getYRot() * Mth.DEG_TO_RAD);
		float f1 = Mth.sin(golem.getYRot() * Mth.DEG_TO_RAD);
		double theta = Math.toRadians(golem.getYRot());
		double vecX = Math.cos(theta + Math.PI / 2);
		double vecZ = Math.sin(theta + Math.PI / 2);
		float offset = right ? -3.0f : 3.0f;
		double cx = golem.getX() + f * offset;
		double cz = golem.getZ() + f1 * offset;
		double dx = golem.getX() + vecX * 5.0;
		double dz = golem.getZ() + vecZ * 5.0;
		if (golem instanceof Mob mob && mob.getTarget() != null) {
			dx = mob.getTarget().getX();
			dz = mob.getTarget().getZ();
		}
		// proxies: 5 plasma orbs 2+2+1 (3079-3083)
		spawnPlasmaSpread(golem, 2, 20.0f, true, 1.0f, plasmaDamage);
		spawnPlasmaSpread(golem, 2, 20.0f, false, 1.0f, plasmaDamage);
		spawnPlasmaSpread(golem, 1, 30.0f, false, 0.0f, plasmaDamage);
	}

	// Obliterator: ultimate quake - replicates state 53:3976 proxies + visuals
	public static void spawnObliteratorUltimateQuake(LivingEntity golem, int lv) {
		Level level = golem.level();
		float base = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float portalDamage = base * (1.0f + lv * 0.2f);
		float flameDamage = base * (0.7f + lv * 0.15f);
		// visuals: CameraShake multiple, MovingTrail, Circle Rings, Sphereparticle
		CameraShakeEntity.cameraShake(level, golem.position(), 20.0F, 0.15F, 20, 6);
		CameraShakeEntity.cameraShake(level, golem.position(), 30.0F, 0.2F, 10, 5);
		if (level instanceof ServerLevel sl) {
			ParticleOptions ring = new Circle.RingData(0.0f, 1.5707964f, 20, 0.0f, 1.0f, 0.0f, 1.0f, 150.0f, false, Circle.EnumRingBehavior.SHRINK);
			sl.sendParticles(ring, golem.getX(), golem.getY(), golem.getZ(), 3, 0, 0, 0, 0);
			// Sphereparticle small annihilation flame sphere
			for (int i = 0; i < 20; ++i) {
				double sx = golem.getX() + (golem.getRandom().nextFloat() - 0.5) * 4.0;
				double sy = golem.getY() + golem.getRandom().nextFloat() * 2.0;
				double sz = golem.getZ() + (golem.getRandom().nextFloat() - 0.5) * 4.0;
				sl.sendParticles((ParticleOptions) ModParticles.SMALL_ANNIHILATION_FLAME.get(), sx, sy, sz, 1, 0, 0.05, 0, 0);
			}
			ParticleOptions ring2 = new Circle.RingData(0.0f, 0.0f, 30, 0.0f, 1.0f, 0.0f, 1.0f, 60.0f, true, Circle.EnumRingBehavior.GROW_THEN_SHRINK);
			sl.sendParticles(ring2, golem.getX(), golem.getY(), golem.getZ(), 1, 0, 0, 0, 0);
		}
		level.playSound(null, BlockPos.containing(golem.getX(), golem.getY(), golem.getZ()), ModSounds.ULTIMATE_FLAME_IMPACT.get(), SoundSource.NEUTRAL, 3.0F, 1.0F);
		level.playSound(null, BlockPos.containing(golem.getX(), golem.getY(), golem.getZ()), ModSounds.OMINOUS_EXPLOSION.get(), SoundSource.NEUTRAL, 3.0F, 1.0F);
		if (level.isClientSide) return;
		// proxies: central portal (4006)
		spawnObliteratorPortal(golem, golem.getX(), golem.getZ(), golem.getY() - 5.0, golem.getY() + 5.0, 50, 12, 6.0f, 20.0f);
		// proxies: 10 outer portals doPortalEffect 15.0 and 8.0 x5 each (4011)
		doPortalEffect(golem, 15.0, 2.0f, 5, 7, 3.5f, 50, 15.0f);
		doPortalEffect(golem, 8.0, 2.0f, 5, 7, 3.5f, 50, 15.0f);
		// proxies: 6 flames circular doFlamesEffect 5.0 2.0 6 (4027)
		int floorY = Mth.floor(golem.getY());
		for (int k = 0; k < 6; ++k) {
			float f3 = (float) k * (float) Math.PI * 2.0f / 6.0f + (float) Math.PI * 2.0f / 10.0f;
			double fx = golem.getX() + Mth.cos(f3) * 5.0;
			double fz = golem.getZ() + Mth.sin(f3) * 5.0;
			spawnObliteratorFlame(golem, fx, fz, floorY, golem.getY() + 1.0, f3, 2, flameDamage);
		}
		// proxies: shockwave flames flameRadagonShockwave 2.0 distance series (4042-4047) - replicate 2 rings
		flameRadagonShockwave(golem, 2.0f, 10, 1.0f, 2, 0.0f, 2.0f, flameDamage, true);
		flameRadagonShockwave(golem, 2.0f, 15, 1.0f, 2, 0.0f, 2.0f, flameDamage, false);
	}

	private static void spawnObliteratorFlame(LivingEntity golem, double x, double z, double minY, double maxY, float rotation, int delay, float damage) {
		Level level = golem.level();
		BlockPos pos = new BlockPos((int) x, (int) maxY, (int) z);
		boolean flag = false;
		double d0 = 0;
		do {
			BlockPos low = pos.below();
			BlockState state = level.getBlockState(low);
			if (state.isFaceSturdy(level, low, Direction.UP)) {
				if (!level.isEmptyBlock(pos)) {
					BlockState topState = level.getBlockState(pos);
					VoxelShape topShape = topState.getCollisionShape(level, pos);
					if (!topShape.isEmpty()) d0 = topShape.max(Direction.Axis.Y);
				}
				flag = true;
				break;
			}
			pos = pos.below();
		} while (pos.getY() >= Mth.floor(minY) - 1);
		if (flag) {
			level.addFreshEntity(new AnnihilationFlameStrike(level, x, pos.getY() + d0, z, rotation, delay, golem, 20, damage + 2.0f));
		}
	}

	private static void spawnObliteratorPortal(LivingEntity golem, double x, double z, double minY, double maxY, int life, int warmup, float scale, float damage) {
		Level level = golem.level();
		BlockPos pos = new BlockPos((int) x, (int) maxY, (int) z);
		boolean flag = false;
		double d0 = 0;
		do {
			BlockPos low = pos.below();
			BlockState state = level.getBlockState(low);
			if (state.isFaceSturdy(level, low, Direction.UP)) {
				if (!level.isEmptyBlock(pos)) {
					BlockState topState = level.getBlockState(pos);
					VoxelShape topShape = topState.getCollisionShape(level, pos);
					if (!topShape.isEmpty()) d0 = topShape.max(Direction.Axis.Y);
				}
				flag = true;
				break;
			}
			pos = pos.below();
		} while (pos.getY() >= Mth.floor(minY) - 1);
		if (flag) {
			level.addFreshEntity(new AnnihilationPortalEntity(level, x, pos.getY() + d0, z, 0.0f, warmup, golem, life, damage, true, scale));
		}
	}

	private static void doPortalEffect(LivingEntity golem, double multiplier, float size, int amount, int warmup, float scale, int life, float damage) {
		for (int k = 0; k < amount; ++k) {
			float f3 = (float) k * (float) Math.PI * size / (float) amount + (float) Math.PI * size / 10.0f;
			spawnObliteratorPortal(golem, golem.getX() + Mth.cos(f3) * multiplier, golem.getZ() + Mth.sin(f3) * multiplier, golem.getY() - 5.0, golem.getY() + 5.0, life, warmup, scale, damage);
		}
	}

	private static void spawnPlasmaSpread(LivingEntity golem, int count, float angleStep, boolean turnLeft, float turnStrength, float damage) {
		Level level = golem.level();
		double theta = Math.toRadians(golem.getYRot());
		double vecX = Math.cos(theta + Math.PI / 2);
		double vecZ = Math.sin(theta + Math.PI / 2);
		for (int i = 0; i < count; ++i) {
			float angle = golem.getYRot() + (i - count / 2.0f) * angleStep;
			float rad = (float) Math.toRadians(angle);
			double dx = -Mth.sin(rad);
			double dz = Mth.cos(rad);
			PlasmaOrbEntity orb = new PlasmaOrbEntity(golem, dx, 0.0, dz, level, damage, angle, 20.0f);
			double spawnX = golem.getX() + vecX;
			double spawnY = golem.getEyeY() - 0.2;
			double spawnZ = golem.getZ() + vecZ;
			orb.setPos(spawnX, spawnY, spawnZ);
			orb.setTurnLeft(turnLeft);
			orb.setTurnStrength(turnStrength);
			level.addFreshEntity(orb);
		}
	}

	private static void flameRadagonShockwave(LivingEntity golem, float spreadarc, int distance, float vec, int delay, float pos, float offset, float damage, boolean warningParticle) {
		Level level = golem.level();
		float f = Mth.cos(golem.getYRot() * Mth.DEG_TO_RAD);
		float f1 = Mth.sin(golem.getYRot() * Mth.DEG_TO_RAD);
		double theta1 = Math.toRadians(golem.getYRot());
		double vecX = Math.cos(theta1 + Math.PI / 2);
		double vecZ = Math.sin(theta1 + Math.PI / 2);
		double x = golem.getX() + pos * vecX + f * offset;
		double z = golem.getZ() + pos * vecZ + f1 * offset;
		double perpFacing = Math.toRadians(golem.getYRot()) + Math.PI / 2;
		double spread = Math.PI * spreadarc;
		int arcLen = Mth.ceil(distance * spread * 0.15f);
		for (int i = 0; i < arcLen; ++i) {
			double theta = ((double) i / (arcLen - 1.0) - 0.5) * spread + perpFacing;
			double vx = Math.cos(theta);
			double vz = Math.sin(theta);
			double px = x + vx * distance + vec * Math.cos(Math.toRadians(golem.getYRot() + 90.0f));
			double pz = z + vz * distance + vec * Math.sin(Math.toRadians(golem.getYRot() + 90.0f));
			int hitX = Mth.floor(px);
			int hitZ = Mth.floor(pz);
			spawnObliteratorFlame(golem, hitX + 0.5, hitZ + 0.5, golem.getY() - 5.0, golem.getY() + 3.0, (float) theta, delay, damage);
		}
	}

}
