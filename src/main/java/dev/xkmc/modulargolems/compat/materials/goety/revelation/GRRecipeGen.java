package dev.xkmc.modulargolems.compat.materials.goety.revelation;

import com.Polarice3.Goety.common.items.ModItems;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.goety.GoetyCompatRegistry;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.material.GolemWeaponType;
import dev.xkmc.modulargolems.init.material.VanillaGolemWeaponMaterial;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import static dev.xkmc.modulargolems.compat.materials.goety.revelation.GRDispatch.MODID;
import static dev.xkmc.modulargolems.init.data.RecipeGen.unlock;

public class GRRecipeGen {

	public static void genRecipe(RegistrateRecipeProvider pvd) {

		var output = ConditionalRecipeWrapper.mod(pvd, MODID);

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
						GRCompatRegistry.UPGRADE_CD.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
				.pattern("BFB").pattern("BOB").pattern("BUB")
				.define('U', GolemItems.EMPTY_UPGRADE)
				.define('O', GoetyCompatRegistry.REV_INGOT)
				.define('F', ModItems.UNHOLY_FABRIC.get())
				.define('B', ModItems.UNHOLY_BLOOD.get())
				.save(output);

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
						GRCompatRegistry.UPGRADE_BOW.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
				.pattern(" B ").pattern("FOF").pattern(" U ")
				.define('U', GolemItems.EMPTY_UPGRADE)
				.define('O', GoetyCompatRegistry.REV_INGOT)
				.define('F', ModItems.UNHOLY_FABRIC.get())
				.define('B', ModItems.UNHOLY_BLOOD.get())
				.save(output);

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
						GRCompatRegistry.UPGRADE_FAST.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
				.pattern("BBB").pattern("FOF").pattern("FUF")
				.define('U', GolemItems.EMPTY_UPGRADE)
				.define('O', GoetyCompatRegistry.REV_INGOT)
				.define('F', ModItems.UNHOLY_FABRIC.get())
				.define('B', ModItems.UNHOLY_BLOOD.get())
				.save(output);

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
						GRCompatRegistry.UPGRADE_CURSE.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
				.pattern(" F ").pattern("BOB").pattern(" U ")
				.define('U', GolemItems.EMPTY_UPGRADE)
				.define('O', GoetyCompatRegistry.REV_INGOT)
				.define('F', ModItems.UNHOLY_FABRIC.get())
				.define('B', ModItems.UNHOLY_BLOOD.get())
				.save(output);

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(GoetyCompatRegistry.REV_TEMPLATE),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_HELMET.get()),
						Ingredient.of(GoetyCompatRegistry.REV_RING),
						RecipeCategory.COMBAT, GRCompatRegistry.APOCALYPTIUM_HELMET.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_HELMET.get())
				.save(output, GRCompatRegistry.APOCALYPTIUM_HELMET.getId());

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(GoetyCompatRegistry.REV_TEMPLATE),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE.get()),
						Ingredient.of(GoetyCompatRegistry.REV_RING),
						RecipeCategory.COMBAT, GRCompatRegistry.APOCALYPTIUM_CHESTPLATE.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_CHESTPLATE.get())
				.save(output, GRCompatRegistry.APOCALYPTIUM_CHESTPLATE.getId());

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(GoetyCompatRegistry.REV_TEMPLATE),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD.get()),
						Ingredient.of(GoetyCompatRegistry.REV_RING),
						RecipeCategory.COMBAT, GRCompatRegistry.APOCALYPTIUM_SHINGUARD.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_SHINGUARD.get())
				.save(output, GRCompatRegistry.APOCALYPTIUM_SHINGUARD.getId());

		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(GoetyCompatRegistry.REV_TEMPLATE),
						Ingredient.of(GolemItems.BARBARICFLAMEVANGUARD_BOOTS.get()),
						Ingredient.of(GoetyCompatRegistry.REV_RING),
						RecipeCategory.COMBAT, GRCompatRegistry.APOCALYPTIUM_BOOTS.get())::unlocks,
				GolemItems.BARBARICFLAMEVANGUARD_BOOTS.get())
				.save(output, GRCompatRegistry.APOCALYPTIUM_BOOTS.getId());

		var sword = GolemItems.METALGOLEM_WEAPON[GolemWeaponType.SWORD.ordinal()][VanillaGolemWeaponMaterial.NETHERITE.ordinal()].get();
		unlock(pvd, SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(GoetyCompatRegistry.REV_TEMPLATE),
						Ingredient.of(sword),
						Ingredient.of(GoetyCompatRegistry.REV_RING),
						RecipeCategory.COMBAT, GRCompatRegistry.STELLAR_APOCALYPSE.get())::unlocks,
				sword)
				.save(output, GRCompatRegistry.STELLAR_APOCALYPSE.getId());
	}

}
