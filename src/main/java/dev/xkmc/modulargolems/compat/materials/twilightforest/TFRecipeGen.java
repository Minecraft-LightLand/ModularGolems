package dev.xkmc.modulargolems.compat.materials.twilightforest;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.ingredients.EnchantmentIngredient;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.twilightforest.equipments.TFGolemWeaponMaterial;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.material.GolemWeaponType;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import twilightforest.data.tags.ItemTagGenerator;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;

import static dev.xkmc.modulargolems.compat.materials.common.ModDispatch.safeUpgrade;
import static dev.xkmc.modulargolems.compat.materials.twilightforest.TFDispatch.MODID;
import static dev.xkmc.modulargolems.init.data.RecipeGen.unlock;

public class TFRecipeGen {

	public static void genRecipe(RegistrateRecipeProvider pvd) {

		// upgrades
		{
			safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TFCompatRegistry.UP_CARMINITE.get())::unlockedBy, TFItems.CARMINITE.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', TFItems.CARMINITE.get())
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', TFBlocks.ENCASED_TOWERWOOD.get())
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TFCompatRegistry.UP_FIERY.get())::unlockedBy, TFItems.FIERY_INGOT.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', TFItems.FIERY_INGOT.get())
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.BLAZE_POWDER)
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TFCompatRegistry.UP_KNIGHTMETAL.get())::unlockedBy, TFItems.KNIGHTMETAL_INGOT.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', TFItems.KNIGHTMETAL_INGOT.get())
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', TFBlocks.HEDGE.get())
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TFCompatRegistry.UP_STEELEAF.get())::unlockedBy, TFItems.STEELEAF_INGOT.get())
					.pattern(" A ").pattern("ABA").pattern(" A ")
					.define('A', TFItems.STEELEAF_INGOT.get())
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TFCompatRegistry.UP_IRONWOOD.get())::unlockedBy, TFItems.IRONWOOD_INGOT.get())
					.pattern(" A ").pattern("ABA").pattern(" A ")
					.define('A', TFItems.IRONWOOD_INGOT.get())
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TFCompatRegistry.UP_NAGA.get())::unlockedBy, TFItems.NAGA_SCALE.get())
					.pattern(" A ").pattern("ABA").pattern(" A ")
					.define('A', TFItems.NAGA_SCALE.get())
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));


			safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.RECYCLE.get())::unlockedBy, TFItems.CHARM_OF_LIFE_2.get())
					.pattern(" A ").pattern("EBE").pattern(" R ")
					.define('A', Ingredient.of(TFItems.CHARM_OF_LIFE_1.get(), TFItems.CHARM_OF_LIFE_2.get()))
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('E', Items.ENDER_PEARL)
					.define('R', Blocks.RESPAWN_ANCHOR)
					.save(ConditionalRecipeWrapper.mod(pvd, MODID), ModularGolems.loc("recycle_upgrade_from_life_charm"));

			safeUpgrade(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GolemItems.RECYCLE.get())::unlockedBy, TFItems.CHARM_OF_KEEPING_3.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(TFItems.CHARM_OF_KEEPING_3.get())
					.save(ConditionalRecipeWrapper.mod(pvd, MODID), ModularGolems.loc("recycle_upgrade_from_lock_charm_3"));

			safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.RECYCLE.get())::unlockedBy, TFItems.CHARM_OF_KEEPING_2.get())
					.pattern(" A ").pattern("1B2").pattern(" 3 ")
					.define('A', TFItems.CHARM_OF_KEEPING_2.get())
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('1', new EnchantmentIngredient(Enchantments.INFINITY_ARROWS, 1))
					.define('2', new EnchantmentIngredient(Enchantments.MENDING, 1))
					.define('3', new EnchantmentIngredient(Enchantments.UNBREAKING, 3))
					.save(ConditionalRecipeWrapper.mod(pvd, MODID), ModularGolems.loc("recycle_upgrade_from_lock_charm_2"));
		}

		// equipments
		{


			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TFCompatRegistry.IRONWOOD_HELMET.get())::unlockedBy, TFItems.IRONWOOD_INGOT.get())
					.pattern(" B ").pattern("III").pattern("IAI")
					.define('I', TFItems.IRONWOOD_HELMET.get())
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', TFItems.LIVEROOT.get())
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TFCompatRegistry.IRONWOOD_CHESTPLATE.get())::unlockedBy, TFItems.IRONWOOD_INGOT.get())
					.pattern("IAI").pattern("III").pattern("BIB")
					.define('I', TFItems.IRONWOOD_CHESTPLATE.get())
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', TFItems.LIVEROOT.get())
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TFCompatRegistry.IRONWOOD_SHINGUARD.get())::unlockedBy, TFItems.IRONWOOD_INGOT.get())
					.pattern("BIB").pattern(" A ").pattern("I I")
					.define('I', TFItems.IRONWOOD_LEGGINGS.get())
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', TFItems.LIVEROOT.get())
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TFCompatRegistry.IRONWOOD_BOOTS.get())::unlockedBy, TFItems.IRONWOOD_INGOT.get())
					.pattern("I I").pattern("BAB")
					.define('I', TFItems.IRONWOOD_BOOTS.get())
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', TFItems.LIVEROOT.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TFCompatRegistry.KNIGHTMETAL_HELMET.get())::unlockedBy, TFItems.KNIGHTMETAL_INGOT.get())
					.pattern(" B ").pattern("III").pattern("IAI")
					.define('I', TFItems.KNIGHTMETAL_HELMET.get())
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', TFItems.ARMOR_SHARD_CLUSTER.get())
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TFCompatRegistry.KNIGHTMETAL_CHESTPLATE.get())::unlockedBy, TFItems.KNIGHTMETAL_INGOT.get())
					.pattern("IAI").pattern("III").pattern("BIB")
					.define('I', TFItems.KNIGHTMETAL_CHESTPLATE.get())
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', TFItems.ARMOR_SHARD_CLUSTER.get())
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TFCompatRegistry.KNIGHTMETAL_SHINGUARD.get())::unlockedBy, TFItems.KNIGHTMETAL_INGOT.get())
					.pattern("BIB").pattern(" A ").pattern("I I")
					.define('I', TFItems.KNIGHTMETAL_LEGGINGS.get())
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', TFItems.ARMOR_SHARD_CLUSTER.get())
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TFCompatRegistry.KNIGHTMETAL_BOOTS.get())::unlockedBy, TFItems.KNIGHTMETAL_INGOT.get())
					.pattern("I I").pattern("BAB")
					.define('I', TFItems.KNIGHTMETAL_BOOTS.get())
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', TFItems.ARMOR_SHARD_CLUSTER.get())
					.save(pvd);


			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TFCompatRegistry.FIERY_HELMET.get())::unlockedBy, TFItems.FIERY_INGOT.get())
					.pattern(" B ").pattern("III").pattern("IAI")
					.define('I', TFItems.FIERY_HELMET.get())
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', ItemTagGenerator.FIERY_VIAL)
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TFCompatRegistry.FIERY_CHESTPLATE.get())::unlockedBy, TFItems.FIERY_INGOT.get())
					.pattern("IAI").pattern("III").pattern("BIB")
					.define('I', TFItems.FIERY_CHESTPLATE.get())
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', ItemTagGenerator.FIERY_VIAL)
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TFCompatRegistry.FIERY_SHINGUARD.get())::unlockedBy, TFItems.FIERY_INGOT.get())
					.pattern("BIB").pattern(" A ").pattern("I I")
					.define('I', TFItems.FIERY_LEGGINGS.get())
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', ItemTagGenerator.FIERY_VIAL)
					.save(ConditionalRecipeWrapper.mod(pvd, MODID));

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TFCompatRegistry.FIERY_BOOTS.get())::unlockedBy, TFItems.FIERY_INGOT.get())
					.pattern("I I").pattern("BAB")
					.define('I', TFItems.FIERY_BOOTS.get())
					.define('A', GolemItems.GOLEM_TEMPLATE.get())
					.define('B', ItemTagGenerator.FIERY_VIAL)
					.save(pvd);


			for (var type : GolemWeaponType.values()) {
				for (var mat : TFGolemWeaponMaterial.values()) {
					Item item = GolemItems.METALGOLEM_WEAPON[type.ordinal()][mat.ordinal()].get();
					type.pattern(unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, item)::unlockedBy, mat.getIngot()))
							.define('I', mat.getIngot())
							.define('S', mat.getHandle())
							.define('T', GolemItems.GOLEM_TEMPLATE.get())
							.save(ConditionalRecipeWrapper.mod(pvd, MODID));
				}
			}
		}

	}

}
