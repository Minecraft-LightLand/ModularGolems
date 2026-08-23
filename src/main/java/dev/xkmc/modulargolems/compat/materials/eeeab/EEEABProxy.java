package dev.xkmc.modulargolems.compat.materials.eeeab;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Proxy wrapping all EEEAB calls with try-catch.
 * Golem modifiers should only call this class, never {@code com.eeeab.*} directly.
 * Reference: {@code LMProxy} / {@code LMProxyImpl}
 */
public class EEEABProxy {

	public static void spawnGuardianBladeBurst(LivingEntity golem) {
		try {
			EEEABProxyImpl.spawnGuardianBladeBurst(golem);
		} catch (Throwable ignored) {
		}
	}

	public static Entity spawnGuardianLaser(LivingEntity golem, int lv) {
		try {
			return EEEABProxyImpl.spawnGuardianLaser(golem, lv);
		} catch (Throwable ignored) {
			return null;
		}
	}

	public static void shootAnnihilatorMissile(LivingEntity golem, LivingEntity target) {
		try {
			EEEABProxyImpl.shootAnnihilatorMissile(golem, target);
		} catch (Throwable ignored) {
		}
	}

	public static Entity spawnAnnihilatorLaser(LivingEntity golem) {
		try {
			return EEEABProxyImpl.spawnAnnihilatorLaser(golem);
		} catch (Throwable ignored) {
			return null;
		}
	}

	public static void spawnElectromagneticBurst(LivingEntity golem, int lv) {
		try {
			EEEABProxyImpl.spawnElectromagneticBurst(golem, lv);
		} catch (Throwable ignored) {
		}
	}

}
