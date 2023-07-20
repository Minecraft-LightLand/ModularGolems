package dev.xkmc.modulargolems.init.data;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import dev.xkmc.l2library.serial.ingredients.EnchantmentIngredient;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.content.recipe.GolemAssembleBuilder;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.Objects;
import java.util.function.BiFunction;

public class RecipeGen {

	public static void genRecipe(RegistrateRecipeProvider pvd) {
		ITagManager<Item> manager = Objects.requireNonNull(ForgeRegistries.ITEMS.tags());

		// golem base
		{
			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.GOLEM_TEMPLATE.get())::unlockedBy,
					Items.CLAY).pattern("CBC").pattern("BAB").pattern("CBC")
					.define('A', Items.COPPER_INGOT).define('B', Items.STICK)
					.define('C', Items.CLAY_BALL).save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.RETRIEVAL_WAND.get())::unlockedBy, GolemItems.GOLEM_TEMPLATE.get())
					.pattern(" ET").pattern(" SE").pattern("S  ")
					.define('E', Items.ENDER_PEARL)
					.define('S', Items.STICK)
					.define('T', GolemItems.GOLEM_TEMPLATE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.DISPENSE_WAND.get())::unlockedBy, GolemItems.GOLEM_TEMPLATE.get())
					.pattern(" ET").pattern(" SE").pattern("S  ")
					.define('E', Items.DISPENSER)
					.define('S', Items.STICK)
					.define('T', GolemItems.GOLEM_TEMPLATE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.COMMAND_WAND.get())::unlockedBy, GolemItems.GOLEM_TEMPLATE.get())
					.pattern(" ET").pattern(" SE").pattern("S  ")
					.define('E', Items.GOLD_INGOT)
					.define('S', Items.STICK)
					.define('T', GolemItems.GOLEM_TEMPLATE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.RIDER_WAND.get())::unlockedBy, GolemItems.GOLEM_TEMPLATE.get())
					.pattern(" ET").pattern(" SE").pattern("S  ")
					.define('E', Items.WHITE_BANNER)
					.define('S', Items.STICK)
					.define('T', GolemItems.GOLEM_TEMPLATE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.EMPTY_UPGRADE.get(), 4)::unlockedBy,
					Items.AMETHYST_SHARD).pattern("CBC").pattern("BAB").pattern("CBC")
					.define('A', Items.AMETHYST_SHARD).define('B', Items.IRON_INGOT)
					.define('C', Items.CLAY_BALL).save(pvd);

			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.GOLEM_BODY);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.GOLEM_ARM);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.GOLEM_LEGS);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.HUMANOID_BODY);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.HUMANOID_ARMS);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.HUMANOID_LEGS);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.DOG_BODY);
			pvd.stonecutting(DataIngredient.items(GolemItems.GOLEM_TEMPLATE.get()), RecipeCategory.MISC, GolemItems.DOG_LEGS);

			unlock(pvd, new GolemAssembleBuilder(GolemItems.HOLDER_GOLEM.get(), 1)::unlockedBy,
					GolemItems.GOLEM_BODY.get())
					.pattern("ABA").pattern(" L ")
					.define('A', GolemItems.GOLEM_ARM.get())
					.define('B', GolemItems.GOLEM_BODY.get())
					.define('L', GolemItems.GOLEM_LEGS.get())
					.save(pvd);

			unlock(pvd, new GolemAssembleBuilder(GolemItems.HOLDER_HUMANOID.get(), 1)::unlockedBy,
					GolemItems.HUMANOID_BODY.get())
					.pattern("A").pattern("B").pattern("C")
					.define('A', GolemItems.HUMANOID_BODY.get())
					.define('B', GolemItems.HUMANOID_ARMS.get())
					.define('C', GolemItems.HUMANOID_LEGS.get())
					.save(pvd);

			unlock(pvd, new GolemAssembleBuilder(GolemItems.HOLDER_DOG.get(), 1)::unlockedBy,
					GolemItems.HUMANOID_BODY.get())
					.pattern("A").pattern("B")
					.define('A', GolemItems.DOG_BODY.get())
					.define('B', GolemItems.DOG_LEGS.get())
					.save(pvd);
		}
		// upgrades
		{

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.FIRE_IMMUNE.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern(" A ").pattern("ABA").pattern(" A ")
					.define('A', Items.MAGMA_CREAM)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.THUNDER_IMMUNE.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern(" A ").pattern("ABA").pattern(" A ")
					.define('A', Items.LIGHTNING_ROD)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.RECYCLE.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern(" C ").pattern("ABA").pattern(" D ")
					.define('A', Items.ENDER_PEARL)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.TOTEM_OF_UNDYING)
					.define('D', Items.RESPAWN_ANCHOR)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.DIAMOND.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.DIAMOND_BLOCK)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.DIAMOND)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.NETHERITE.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.NETHERITE_INGOT)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.DIAMOND)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.QUARTZ.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.QUARTZ_BLOCK)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.QUARTZ)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.GOLD.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.GOLDEN_APPLE)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.GOLDEN_CARROT)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.SPONGE.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern(" A ").pattern("ABA").pattern(" A ")
					.define('A', Items.WET_SPONGE)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GolemItems.ENCHANTED_GOLD.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.ENCHANTED_GOLDEN_APPLE)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GolemItems.FLOAT.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(ItemTags.BOATS)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GolemItems.SWIM.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.HEART_OF_THE_SEA)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GolemItems.PLAYER_IMMUNE.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.NETHER_STAR)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GolemItems.BELL.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.BELL)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GolemItems.ENDER_SIGHT.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.ENDER_EYE)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GolemItems.SPEED.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.RABBIT_FOOT)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.WEAK.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern("CDC").pattern("ABA").pattern("CDC")
					.define('A', Items.DRAGON_BREATH)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.FERMENTED_SPIDER_EYE)
					.define('D', Items.REDSTONE)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.SLOW.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern("CDC").pattern("ABA").pattern("EDE")
					.define('A', Items.DRAGON_BREATH)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.FERMENTED_SPIDER_EYE)
					.define('D', Items.REDSTONE)
					.define('E', Items.SUGAR)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.WITHER.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern("CDC").pattern("ABA").pattern("CDC")
					.define('A', Items.DRAGON_BREATH)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.WITHER_ROSE)
					.define('D', Items.REDSTONE)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.EMERALD.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.EMERALD_BLOCK)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.EMERALD)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.PICKUP.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern("AAA").pattern("DBD").pattern(" C ")
					.define('A', Items.HOPPER)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.LAVA_BUCKET)
					.define('D', Items.ENDER_PEARL)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GolemItems.PICKUP_MENDING.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get())
					.requires(new EnchantmentIngredient(Enchantments.MENDING, 1))
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GolemItems.PICKUP_NO_DESTROY.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get())
					.requires(Items.ZOMBIE_HEAD)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.TALENTED.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern("CEC").pattern("ABA").pattern("CAC")
					.define('A', Items.NETHER_STAR)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Tags.Items.HEADS)
					.define('E', Items.DRAGON_HEAD)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.CAULDRON.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern("CEC").pattern("ABA").pattern("CDC")
					.define('A', Items.NETHER_STAR)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.DRAGON_BREATH)
					.define('D', Items.CAULDRON)
					.define('E', Items.DRAGON_HEAD)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GolemItems.MOUNT_UPGRADE.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.requires(GolemItems.EMPTY_UPGRADE.get()).requires(Items.SADDLE)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GolemItems.SIZE_UPGRADE.get())::unlockedBy, GolemItems.EMPTY_UPGRADE.get())
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.IRON_BLOCK)
					.define('B', GolemItems.EMPTY_UPGRADE.get())
					.define('C', Items.COPPER_BLOCK)
					.save(pvd);

		}

		CompatManager.dispatchGenRecipe(pvd);
	}

	public static <T> T unlock(RegistrateRecipeProvider pvd, BiFunction<String, InventoryChangeTrigger.TriggerInstance, T> func, Item item) {
		return func.apply("has_" + pvd.safeName(item), DataIngredient.items(item).getCritereon(pvd));
	}

}
