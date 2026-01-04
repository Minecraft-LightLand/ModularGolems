package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.config.CMConfig;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Blast_Portal_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Sandstorm_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.*;
import com.github.L_Ender.cataclysm.init.ModEffect;
import com.github.L_Ender.cataclysm.init.ModEntities;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CataclysmProxy {

	public static void sandstormAttack(LivingEntity golem, LivingEntity target, int life) {
		try {
			Vec3 diff = target.position().subtract(golem.position()).normalize();
			float angle = (float) Math.atan2(diff.z, diff.x);
			double sx = target.getX();
			double sy = target.getY();
			double sz = target.getZ();
			Sandstorm_Entity projectile = new Sandstorm_Entity(golem.level(), sx, sy, sz, life, angle, golem);
			golem.level().addFreshEntity(projectile);
		} catch (Throwable e) {
			ModularGolems.LOGGER.error(e);
		}
	}

	public static boolean isLaser(DamageSource source) {
		try {
			return source.getDirectEntity() instanceof Death_Laser_Beam_Entity;
		} catch (Throwable e) {
			return false;
		}
	}

	public static boolean isMissile(DamageSource source) {
		try {
			return source.getDirectEntity() instanceof Wither_Homing_Missile_Entity;
		} catch (Throwable e) {
			return false;
		}
	}

	public static boolean isSandstorm(DamageSource source) {
		try {
			return source.getDirectEntity() instanceof Sandstorm_Entity;
		} catch (Throwable e) {
			return false;
		}
	}

	public static int getSandCurseLevel(LivingEntity e) {
		try {
			var ins = e.getEffect(ModEffect.EFFECTCURSE_OF_DESERT);
			if (ins == null) return 0;
			return ins.getAmplifier() + 1;
		} catch (Throwable ignored) {
			return 0;
		}
	}

	@Nullable
	public static Entity addLaserBeam(LivingEntity user, int dur) {
		try {
			Death_Laser_Beam_Entity beam = new Death_Laser_Beam_Entity(ModEntities.DEATH_LASER_BEAM.get(),
					user.level(), user, user.getX(), user.getEyeY(), user.getZ(),
					(user.yHeadRot + 90.0F) * Mth.DEG_TO_RAD,
					-user.getXRot() * Mth.DEG_TO_RAD, dur, (float) CMConfig.DeathLaserdamage, (float) CMConfig.DeathLaserHpdamage);
			user.level().addFreshEntity(beam);
			return beam;
		} catch (Throwable e) {
			ModularGolems.LOGGER.error(e);
		}
		return null;
	}

	public static void addMissile(LivingEntity user, LivingEntity target, Vec3 pos) {
		try {
			var diff = target.getEyePosition().subtract(pos).normalize();
			Wither_Homing_Missile_Entity laserBeam = new Wither_Homing_Missile_Entity(user, diff, user.level(), (float) CMConfig.HarbingerWitherMissiledamage, target);
			laserBeam.setPosRaw(pos.x(), pos.y(), pos.z());
			user.level().addFreshEntity(laserBeam);
		} catch (Throwable e) {
			ModularGolems.LOGGER.error(e);
		}
	}

	public static void spawnFangs(LivingEntity user, double x, double y, double z, float rotation, int delay) {
		try {
			user.level().addFreshEntity(new Void_Rune_Entity(user.level(), x, y, z, rotation, delay,
					(float) CMConfig.Voidrunedamage, user));
		} catch (Throwable e) {
			ModularGolems.LOGGER.error(e);
		}
	}

	public static void spawnBlastPortal(LivingEntity user, double x, double y, double z, float rotation, int delay) {
		try {
			user.level().addFreshEntity(new Abyss_Blast_Portal_Entity(user.level(), x, y, z, rotation, delay,
					(float) CMConfig.AbyssBlastdamage, (float) CMConfig.AbyssBlastHpdamage, user));
		} catch (Throwable e) {
			ModularGolems.LOGGER.error(e);
		}
	}

	public static void stackBlazingBrand(LivingEntity golem, LivingEntity target, int factor) {
		try {
			var eff = ModEffect.EFFECTBLAZING_BRAND;
			var old = target.getEffect(eff);
			int i = old == null ? 0 : Math.min(4, old.getAmplifier() + 1);
			MobEffectInstance ins = new MobEffectInstance(eff, 240, i, false, true, true);
			target.addEffect(ins);
			golem.heal(factor * (float) CMConfig.IgnisHealingMultiplier * (float) (i + 1));
		} catch (Throwable e) {
			ModularGolems.LOGGER.error(e);
		}
	}


	public static void shootFireball(LivingEntity user, Vec3 shotAt, int timer, boolean abyss) {
		try {
			shotAt = shotAt.yRot(-user.getYRot() * 0.017453292F);
			Projectile shot;
			if (abyss) {
				var bullet = new Ignis_Abyss_Fireball_Entity(user.level(), user);
				bullet.setUp(timer);
				shot = bullet;
			} else {
				var bullet = new Ignis_Fireball_Entity(user.level(), user);
				bullet.setUp(timer);
				if (user.getHealth() < user.getMaxHealth() / 2) {
					bullet.setSoul(true);
				}
				shot = bullet;
			}
			float rot = user.yBodyRot * 0.017453292F;
			double width = (user.getBbWidth() + 1.0F) * 0.15D;
			shot.setPos(user.getX() - width * Mth.sin(rot),
					user.getY() + 1.0D,
					user.getZ() + width * Mth.cos(rot));
			double d0 = shotAt.x;
			double d1 = shotAt.y;
			double d2 = shotAt.z;
			float f = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.35F;
			shot.shoot(d0, d1 + f, d2, 0.25F, 3.0F);
			user.level().addFreshEntity(shot);
		} catch (Throwable e) {
			ModularGolems.LOGGER.error(e);
		}
	}

	public static float monstrosityEarthquakeDamage() {
		try {
			return (float) CMConfig.MonstrositysHpdamage;
		} catch (Throwable e) {
			ModularGolems.LOGGER.error(e);
		}
		return 0;
	}

	public static float maledictusEarthquakeDamage() {
		try {
			return (float) CMConfig.MaledictusAOEHpDamage;
		} catch (Throwable e) {
			ModularGolems.LOGGER.error(e);
		}
		return 0;
	}

	public static void updateLaser(AbstractGolemEntity<?, ?> golem, Entity e) {
		try {
			if (e instanceof Death_Laser_Beam_Entity beam) {
				beam.setYaw((float) ((golem.yHeadRot + 90.0F) * Math.PI / 180.0F));
				beam.setPitch((float) ((-golem.getXRot()) * Math.PI / 180.0F));
			}
		} catch (Throwable ignored) {

		}
	}

}
