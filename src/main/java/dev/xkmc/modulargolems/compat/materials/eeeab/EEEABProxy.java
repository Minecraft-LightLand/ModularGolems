package dev.xkmc.modulargolems.compat.materials.eeeab;

import com.eeeab.eeeabsmobs.sever.init.ItemInit;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

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

	@Nullable
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

	@Nullable
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
