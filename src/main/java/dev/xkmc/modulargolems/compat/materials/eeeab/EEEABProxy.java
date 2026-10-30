package dev.xkmc.modulargolems.compat.materials.eeeab;

import com.eeeab.eeeabsmobs.sever.init.ItemInit;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * Proxy wrapping all EEEAB calls with try-catch.
 * Golem modifiers should only call this class, never {@code com.eeeab.*} directly.
 * Reference: {@code LMProxy} / {@code LMProxyImpl}
 * <p>
 * Failures are logged rather than swallowed: every call here reaches an optional dependency across a
 * version boundary, so a silent catch turns "the version changed" into "the skill silently does
 * nothing" with nothing in the log to point at it.
 */
public class EEEABProxy {

	private static final Logger LOGGER = LogManager.getLogger(EEEABProxy.class);

	private static void failed(String call, Throwable t) {
		LOGGER.warn("EEEAB call {} failed, EEEAB version or mapping changed", call, t);
	}

	public static void spawnGuardianBladeBurst(LivingEntity golem) {
		try {
			EEEABProxyImpl.spawnGuardianBladeBurst(golem);
		} catch (Throwable t) {
			failed("spawnGuardianBladeBurst", t);
		}
	}

	@Nullable
	public static Entity spawnGuardianLaser(LivingEntity golem, int lv) {
		try {
			return EEEABProxyImpl.spawnGuardianLaser(golem, lv);
		} catch (Throwable t) {
			failed("spawnGuardianLaser", t);
			return null;
		}
	}

	public static void shootAnnihilatorMissile(LivingEntity golem, LivingEntity target) {
		try {
			EEEABProxyImpl.shootAnnihilatorMissile(golem, target);
		} catch (Throwable t) {
			failed("shootAnnihilatorMissile", t);
		}
	}

	@Nullable
	public static Entity spawnAnnihilatorLaser(LivingEntity golem) {
		try {
			return EEEABProxyImpl.spawnAnnihilatorLaser(golem);
		} catch (Throwable t) {
			failed("spawnAnnihilatorLaser", t);
			return null;
		}
	}

	public static void spawnElectromagneticBurst(LivingEntity golem, int lv) {
		try {
			EEEABProxyImpl.spawnElectromagneticBurst(golem, lv);
		} catch (Throwable t) {
			failed("spawnElectromagneticBurst", t);
		}
	}

	public static void realmWardenJumpSmashServerEffect(LivingEntity golem, Vec3 pos, float scale, boolean secondPhase) {
		try {
			EEEABProxyImpl.realmWardenJumpSmashServerEffect(golem, pos, scale, secondPhase);
		} catch (Throwable t) {
			failed("realmWardenJumpSmashServerEffect", t);
		}
	}

	public static void realmWardenJumpSmashClientEffect(LivingEntity golem, Vec3 pos, float scale, float yBlockPtcScale,
			boolean dust, boolean ring, double[] angles, boolean secondPhase) {
		try {
			EEEABProxyImpl.realmWardenJumpSmashClientEffect(golem, pos, scale, yBlockPtcScale, dust, ring, angles, secondPhase);
		} catch (Throwable t) {
			failed("realmWardenJumpSmashClientEffect", t);
		}
	}

	public static void realmWardenJumpSmashDerivedEffect(LivingEntity golem, Vec3 pos) {
		try {
			EEEABProxyImpl.realmWardenJumpSmashDerivedEffect(golem, pos);
		} catch (Throwable t) {
			failed("realmWardenJumpSmashDerivedEffect", t);
		}
	}

	public static void realmWardenLeapTelegraph(LivingEntity golem, Vec3 pos, boolean secondPhase, float scale) {
		try {
			EEEABProxyImpl.realmWardenLeapTelegraph(golem, pos, secondPhase, scale);
		} catch (Throwable t) {
			failed("realmWardenLeapTelegraph", t);
		}
	}

	public static void realmWardenLeapImpactEffect(LivingEntity golem, boolean slam) {
		try {
			EEEABProxyImpl.realmWardenLeapImpactEffect(golem, slam);
		} catch (Throwable t) {
			failed("realmWardenLeapImpactEffect", t);
		}
	}

	public static void realmWardenLeapLandingEffect(LivingEntity golem, boolean secondPhase, boolean slam) {
		try {
			EEEABProxyImpl.realmWardenLeapLandingEffect(golem, secondPhase, slam);
		} catch (Throwable t) {
			failed("realmWardenLeapLandingEffect", t);
		}
	}

	public static void realmWardenLeapShock(LivingEntity golem) {
		try {
			EEEABProxyImpl.realmWardenLeapShock(golem);
		} catch (Throwable t) {
			failed("realmWardenLeapShock", t);
		}
	}

	public static void realmWardenLeapRingShockWave(LivingEntity golem) {
		try {
			EEEABProxyImpl.realmWardenLeapRingShockWave(golem);
		} catch (Throwable t) {
			failed("realmWardenLeapRingShockWave", t);
		}
	}

	public static void realmWardenLeapExplosion(LivingEntity golem) {
		try {
			EEEABProxyImpl.realmWardenLeapExplosion(golem);
		} catch (Throwable t) {
			failed("realmWardenLeapExplosion", t);
		}
	}

	public static void realmWardenLeapSurgeRing(LivingEntity golem) {
		try {
			EEEABProxyImpl.realmWardenLeapSurgeRing(golem);
		} catch (Throwable t) {
			failed("realmWardenLeapSurgeRing", t);
		}
	}

	public static void realmWardenLeapImpactDamage(LivingEntity golem, LivingEntity target, float damage,
			int disableShieldTime, boolean bypassArmor, boolean secondPhase) {
		try {
			EEEABProxyImpl.realmWardenLeapImpactDamage(golem, target, damage, disableShieldTime, bypassArmor, secondPhase);
		} catch (Throwable t) {
			failed("realmWardenLeapImpactDamage", t);
		}
	}

	public static void genRecipe(RegistrateRecipeProvider pvd) {

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EEEABCompatRegistry.REALM_CUBE, 9)::unlockedBy,
						ItemInit.ANCIENT_DRIVE_CRYSTAL.get())
				.pattern("XXX").pattern("AAA").pattern("XXX")
				.define('A', ItemInit.ANCIENT_DRIVE_CRYSTAL.get())
				.define('X', ItemInit.BOUNDARY_BRICK.get())
				.save(ConditionalRecipeWrapper.mod(pvd, EEEABDispatch.MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EEEABCompatRegistry.REALM_CONSTRUCT, 9)::unlockedBy,
						ItemInit.ANCIENT_DRIVE_CRYSTAL.get())
				.pattern("XXX").pattern("1A2").pattern("XXX")
				.define('1', ItemInit.GUARDIAN_CUBE.get())
				.define('2', ItemInit.CHAIN_GEAR.get())
				.define('A', ItemInit.ANCIENT_DRIVE_CRYSTAL.get())
				.define('X', EEEABCompatRegistry.REALM_CUBE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, EEEABDispatch.MODID));
	}

}