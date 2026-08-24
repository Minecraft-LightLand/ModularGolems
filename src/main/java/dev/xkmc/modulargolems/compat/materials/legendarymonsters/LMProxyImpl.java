package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.AnnihilationBeamEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.AnnihilationBombEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.ElectricityEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.LightningBoltEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.PlasmaOrbEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SmallAnnihilationBombEntity;
import net.miauczel.legendary_monsters.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
		AnnihilationBombEntity bomb = new AnnihilationBombEntity(type, golem.level(), golem, damage, 16, false);
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
	public static void spawnObliteratorLaser(LivingEntity golem, LivingEntity target, int lv) {
		if (golem.level().isClientSide) return;
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
	}

}
