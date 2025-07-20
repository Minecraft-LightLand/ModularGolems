package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.miauczel.legendary_monsters.item.ModItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class LMProxy {

	public static void shake(LivingEntity user, Vec3 pos) {
		try {
			LMProxyImpl.shake(user, pos);
		} catch (Throwable ignored) {

		}
	}

	public static List<LivingEntity> stun(ServerLevel level, double x, double y, double z, LivingEntity golem, float reach, int lv) {
		try {
			return LMProxyImpl.stun(level, x, y, z, golem, reach, lv);
		} catch (Throwable ignored) {
			return new ArrayList<>();
		}
	}

	public static void performThunderAttack(Mob e, LivingEntity target, int lv) {
		try {
			LMProxyImpl.performThunderAttack(e, target, lv);
		} catch (Throwable ignored) {

		}
	}

	public static void spawnElectricShock(Entity attacker, LivingEntity entity, float damage, int n) {
		try {
			LMProxyImpl.spawnElectricShock(attacker, entity, damage, n);
		} catch (Throwable ignored) {

		}
	}

	public static void genRecipe(RegistrateRecipeProvider pvd) {

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LMCompatRegistry.CLOUD_CUBE, 9)::unlockedBy,
						ModItems.AIR_RUNE.get())
				.pattern("RIR").pattern("IXI").pattern("RIR")
				.define('I', GolemItems.GOLEM_TEMPLATE)
				.define('R', ModItems.CLOUD_ROD.get())
				.define('X', Ingredient.of(ModItems.AIR_RUNE.get(), ModItems.ATMOSPHERIC_BOOTS.get()))
				.save(ConditionalRecipeWrapper.mod(pvd, LMDispatch.MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LMCompatRegistry.UPGRADE_THUNDER.get())::unlockedBy,
						ModItems.AIR_RUNE.get())
				.pattern(" X ").pattern("ROR").pattern(" R ")
				.define('R', ModItems.CLOUD_ROD.get())
				.define('X', ModItems.AIR_RUNE.get())
				.define('O', GolemItems.EMPTY_UPGRADE)
				.save(ConditionalRecipeWrapper.mod(pvd, LMDispatch.MODID));
	}

}
