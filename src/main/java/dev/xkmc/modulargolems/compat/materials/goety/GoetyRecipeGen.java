package dev.xkmc.modulargolems.compat.materials.goety;

import com.Polarice3.Goety.common.items.ModItems;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;

import static dev.xkmc.modulargolems.compat.materials.goety.GoetyDispatch.MODID;

public class GoetyRecipeGen {

	public static void genRecipe(RegistrateRecipeProvider pvd) {

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
						GoetyCompatRegistry.UPGRADE_SOUL.get())::unlockedBy, ModItems.DARK_METAL_INGOT.get())
				.pattern("EDE").pattern("FXF").pattern("IFI")
				.define('X', GolemItems.EMPTY_UPGRADE)
				.define('I', ModItems.DARK_METAL_INGOT.get())
				.define('E', ModItems.ECTOPLASM.get())
				.define('F', ModItems.SPIRIT_FABRIC.get())
				.define('D', ModItems.SOUL_EMERALD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,
						GoetyCompatRegistry.UPGRADE_BLAST.get())::unlockedBy, ModItems.UNHOLY_BLOOD.get())
				.requires(GolemItems.EMPTY_UPGRADE)
				.requires(ModItems.UNHOLY_BLOOD.get())
				.requires(ModItems.FIRE_BLAST_FOCUS.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,
						GoetyCompatRegistry.UPGRADE_TORNADO.get())::unlockedBy, ModItems.UNHOLY_BLOOD.get())
				.requires(GolemItems.EMPTY_UPGRADE)
				.requires(ModItems.UNHOLY_BLOOD.get())
				.requires(ModItems.CYCLONE_FOCUS.get())
				.requires(Items.LAVA_BUCKET)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,
						GoetyCompatRegistry.UPGRADE_CLOUD.get())::unlockedBy, ModItems.UNHOLY_BLOOD.get())
				.requires(GolemItems.EMPTY_UPGRADE)
				.requires(ModItems.UNHOLY_BLOOD.get())
				.requires(ModItems.HAIL_FOCUS.get())
				.requires(Items.LAVA_BUCKET)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,
						GoetyCompatRegistry.UPGRADE_BOLT.get())::unlockedBy, ModItems.UNHOLY_FABRIC.get())
				.requires(GolemItems.EMPTY_UPGRADE)
				.requires(ModItems.FIREBALL_FOCUS.get())
				.requires(ModItems.UNHOLY_FABRIC.get())
				.requires(ModItems.UNHOLY_BLOOD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,
						GoetyCompatRegistry.UPGRADE_BALL.get())::unlockedBy, ModItems.UNHOLY_FABRIC.get())
				.requires(GolemItems.EMPTY_UPGRADE)
				.requires(ModItems.LAVABALL_FOCUS.get())
				.requires(ModItems.UNHOLY_FABRIC.get())
				.requires(ModItems.UNHOLY_BLOOD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));
		{


			var base = ConditionalRecipeWrapper.of(pvd,
					new ModLoadedCondition(MODID), new NotCondition(new ModLoadedCondition("goety_revelation")));

			var rev = ConditionalRecipeWrapper.of(pvd,
					new ModLoadedCondition(MODID), new ModLoadedCondition("goety_revelation"));

			RecipeGen.unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,
							GoetyCompatRegistry.UPGRADE_APOSTLE.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
					.requires(GolemItems.EMPTY_UPGRADE)
					.requires(ModItems.UNHOLY_HAT.get())
					.save(base, GoetyCompatRegistry.UPGRADE_APOSTLE.getId().withSuffix("_base"));

			RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
							GoetyCompatRegistry.UPGRADE_APOSTLE.get())::unlockedBy, ModItems.UNHOLY_HAT.get())
					.pattern("BFB").pattern("BOB").pattern("BUB")
					.define('U', GolemItems.EMPTY_UPGRADE)
					.define('F', GoetyCompatRegistry.REV_RING)
					.define('B', GoetyCompatRegistry.REV_DOOM)
					.define('O', ModItems.UNHOLY_ROBE.get())
					.save(rev, GoetyCompatRegistry.UPGRADE_APOSTLE.getId().withSuffix("_revelation"));

		}
	}

}
