package dev.xkmc.modulargolems.compat.materials.eeeab;

import com.eeeab.eeeabsmobs.sever.entity.effect.EntityElectromagnetic;
import com.eeeab.eeeabsmobs.sever.entity.effect.EntityGuardianLaser;
import com.eeeab.eeeabsmobs.sever.entity.effect.EntityInfraredRay;
import com.eeeab.eeeabsmobs.sever.entity.effect.EntityGuardianBlade;
import com.eeeab.eeeabsmobs.sever.entity.effect.projectile.EntityAnnihilatorMissile;
import com.eeeab.eeeabsmobs.sever.init.EffectInit;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

/**
 * Actual EEEAB implementations. All direct references to {@code com.eeeab.*} must stay here.
 * Called via {@link EEEABProxy} with try-catch.
 * Reference: {@code LMProxyImpl} for legendarymonsters.
 */
public class EEEABProxyImpl {

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

	public static Entity spawnGuardianLaser(LivingEntity golem, int lv) {
		if (golem.level().isClientSide) return null;
		double px = golem.getX();
		double py = golem.getY() + 1.4;
		double pz = golem.getZ();
		int duration = 70; // original GuardianShootLaserGoal 70
		EntityGuardianLaser laser = new EntityGuardianLaser(golem.level(), golem, px, py, pz, duration);
		float base = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		laser.setDamage(base / 3.0F * (1 + 0.2F * (lv - 1)));
		golem.level().addFreshEntity(laser);
		return laser;
	}

	public static void shootAnnihilatorMissile(LivingEntity golem, LivingEntity target) {
		if (golem.level().isClientSide) return;
		Vec3 muzzle = golem.getEyePosition().add(golem.getForward().scale(1.2)).add(0, -0.2, 0);
		Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.4, 0);
		Vec3 projectileMid = muzzle.add(0, 0.25, 0);
		Vec3 shootVec = targetPos.subtract(projectileMid).normalize();
		EntityAnnihilatorMissile.ElementType element = target.hasEffect(EffectInit.ELECTRIFIED_EFFECT.get())
				? EntityAnnihilatorMissile.ElementType.BLAZE
				: EntityAnnihilatorMissile.ElementType.VOLT;
		if (golem.getHealth() / golem.getMaxHealth() < 0.5F && golem.getRandom().nextFloat() < 0.2F) {
			element = EntityAnnihilatorMissile.ElementType.SPARKFERNO;
		}
		EntityAnnihilatorMissile missile = new EntityAnnihilatorMissile(golem.level(), golem, element);
		missile.moveTo(muzzle.x, muzzle.y, muzzle.z, golem.getYRot(), golem.getXRot());
		missile.shoot(shootVec.x, shootVec.y, shootVec.z, 1.6F, 0.0F);
		golem.level().addFreshEntity(missile);
	}

	public static Entity spawnAnnihilatorLaser(LivingEntity golem) {
		if (golem.level().isClientSide) return null;
		double x = golem.getX();
		double y = golem.getEyeY() - 0.3;
		double z = golem.getZ();
		EntityInfraredRay ray = new EntityInfraredRay(golem.level(), golem, x, y, z, 29);
		golem.level().addFreshEntity(ray);
		EntityGuardianLaser laser = new EntityGuardianLaser(golem.level(), golem,
				golem.getX(), golem.getY(), golem.getZ(), 20);
		laser.setCountDown(1);
		EntityGuardianLaser.UserType type = EntityGuardianLaser.UserType.RELIC_ANNIHILATOR;
		laser.updateWithEntity(golem, type.wOffset, type.hOffset);
		golem.level().addFreshEntity(laser);
		return laser;
	}

	public static void spawnElectromagneticBurst(LivingEntity golem, int lv) {
		if (golem.level().isClientSide) return;
		Vec3 look = golem.getForward().normalize();
		Vec3 poundPos = golem.position().add(look.scale(2.25)).add(0, 0.2, 0);
		float offset = (float) Math.toRadians(golem.getRandom().nextFloat() * 360.0F - 180.0F);
		for (int i = 0; i < 6; i++) {
			float f1 = (float) (golem.getYRot() + (i + offset) * Math.PI * 0.3333333333333333);
			Vec3 spawnPos = new Vec3(poundPos.x, poundPos.y, poundPos.z);
			float yawDeg = f1 * Mth.RAD_TO_DEG - 90.0F;
			EntityElectromagnetic.shoot(golem.level(), golem, spawnPos, 2.0F, 10, 5, yawDeg, false);
		}
		if (lv >= 3) {
			for (int i = 0; i < 2; i++) {
				float f1 = (float) (golem.getYRot() + (i + offset + 3) * Math.PI * 0.3333333333333333);
				Vec3 spawnPos = new Vec3(poundPos.x, poundPos.y, poundPos.z);
				float yawDeg = f1 * Mth.RAD_TO_DEG - 90.0F;
				EntityElectromagnetic.shoot(golem.level(), golem, spawnPos, 1.5F, 8, 6, yawDeg, false);
			}
		}
	}

}
