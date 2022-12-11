package dev.xkmc.modulargolems.init.data;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.repack.registrate.util.DataIngredient;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.content.recipe.GolemAssembleBuilder;
import dev.xkmc.modulargolems.init.registrate.GolemItemRegistry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.Objects;
import java.util.function.BiFunction;

public class RecipeGen {

	public static void genRecipe(RegistrateRecipeProvider pvd) {
		ITagManager<Item> manager = Objects.requireNonNull(ForgeRegistries.ITEMS.tags());

		unlock(pvd, ShapedRecipeBuilder.shaped(GolemItemRegistry.GOLEM_TEMPLATE.get())::unlockedBy,
				Items.CLAY).pattern("CBC").pattern("BAB").pattern("CBC")
				.define('A', Items.COPPER_INGOT).define('B', Items.STICK)
				.define('C', Items.CLAY_BALL).save(pvd);

		unlock(pvd, ShapedRecipeBuilder.shaped(GolemItemRegistry.RETRIEVAL_WAND.get())::unlockedBy, Items.ENDER_PEARL)
				.pattern(" ET").pattern(" SE").pattern("S  ")
				.define('E', Items.ENDER_PEARL)
				.define('S', Items.STICK)
				.define('T', GolemItemRegistry.GOLEM_TEMPLATE.get())
				.save(pvd);

		unlock(pvd, ShapedRecipeBuilder.shaped(GolemItemRegistry.COMMAND_WAND.get())::unlockedBy, Items.GOLD_INGOT)
				.pattern(" ET").pattern(" SE").pattern("S  ")
				.define('E', Items.GOLD_INGOT)
				.define('S', Items.STICK)
				.define('T', GolemItemRegistry.GOLEM_TEMPLATE.get())
				.save(pvd);

		unlock(pvd, ShapedRecipeBuilder.shaped(GolemItemRegistry.EMPTY_UPGRADE.get(), 4)::unlockedBy,
				Items.AMETHYST_SHARD).pattern("CBC").pattern("BAB").pattern("CBC")
				.define('A', Items.AMETHYST_SHARD).define('B', Items.IRON_INGOT)
				.define('C', Items.CLAY_BALL).save(pvd);

		pvd.stonecutting(DataIngredient.items(GolemItemRegistry.GOLEM_TEMPLATE.get()), GolemItemRegistry.GOLEM_BODY);
		pvd.stonecutting(DataIngredient.items(GolemItemRegistry.GOLEM_TEMPLATE.get()), GolemItemRegistry.GOLEM_ARM);
		pvd.stonecutting(DataIngredient.items(GolemItemRegistry.GOLEM_TEMPLATE.get()), GolemItemRegistry.GOLEM_LEGS);
		pvd.stonecutting(DataIngredient.items(GolemItemRegistry.GOLEM_TEMPLATE.get()), GolemItemRegistry.HUMANOID_BODY);
		pvd.stonecutting(DataIngredient.items(GolemItemRegistry.GOLEM_TEMPLATE.get()), GolemItemRegistry.HUMANOID_ARMS);
		pvd.stonecutting(DataIngredient.items(GolemItemRegistry.GOLEM_TEMPLATE.get()), GolemItemRegistry.HUMANOID_LEGS);
		pvd.stonecutting(DataIngredient.items(GolemItemRegistry.GOLEM_TEMPLATE.get()), GolemItemRegistry.DOG_BODY);
		pvd.stonecutting(DataIngredient.items(GolemItemRegistry.GOLEM_TEMPLATE.get()), GolemItemRegistry.DOG_LEGS);

		unlock(pvd, new GolemAssembleBuilder(GolemItemRegistry.HOLDER_GOLEM.get(), 1)::unlockedBy,
				GolemItemRegistry.GOLEM_BODY.get())
				.pattern("ABA").pattern(" L ")
				.define('A', GolemItemRegistry.GOLEM_ARM.get())
				.define('B', GolemItemRegistry.GOLEM_BODY.get())
				.define('L', GolemItemRegistry.GOLEM_LEGS.get())
				.save(pvd);

		unlock(pvd, new GolemAssembleBuilder(GolemItemRegistry.HOLDER_HUMANOID.get(), 1)::unlockedBy,
				GolemItemRegistry.HUMANOID_BODY.get())
				.pattern("A").pattern("B").pattern("C")
				.define('A', GolemItemRegistry.HUMANOID_BODY.get())
				.define('B', GolemItemRegistry.HUMANOID_ARMS.get())
				.define('C', GolemItemRegistry.HUMANOID_LEGS.get())
				.save(pvd);

		unlock(pvd, new GolemAssembleBuilder(GolemItemRegistry.HOLDER_DOG.get(), 1)::unlockedBy,
				GolemItemRegistry.HUMANOID_BODY.get())
				.pattern("A").pattern("B")
				.define('A', GolemItemRegistry.DOG_BODY.get())
				.define('B', GolemItemRegistry.DOG_LEGS.get())
				.save(pvd);

		// upgrades
		{

			unlock(pvd, ShapedRecipeBuilder.shaped(GolemItemRegistry.FIRE_IMMUNE.get())::unlockedBy, Items.MAGMA_CREAM)
					.pattern(" A ").pattern("ABA").pattern(" A ")
					.define('A', Items.MAGMA_CREAM)
					.define('B', GolemItemRegistry.EMPTY_UPGRADE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(GolemItemRegistry.THUNDER_IMMUNE.get())::unlockedBy, Items.LIGHTNING_ROD)
					.pattern(" A ").pattern("ABA").pattern(" A ")
					.define('A', Items.LIGHTNING_ROD)
					.define('B', GolemItemRegistry.EMPTY_UPGRADE.get())
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(GolemItemRegistry.RECYCLE.get())::unlockedBy, Items.TOTEM_OF_UNDYING)
					.pattern(" C ").pattern("ABA").pattern(" D ")
					.define('A', Items.ENDER_PEARL)
					.define('B', GolemItemRegistry.EMPTY_UPGRADE.get())
					.define('C', Items.TOTEM_OF_UNDYING)
					.define('D', Items.RESPAWN_ANCHOR)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(GolemItemRegistry.DIAMOND.get())::unlockedBy, Items.DIAMOND)
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.DIAMOND_BLOCK)
					.define('B', GolemItemRegistry.EMPTY_UPGRADE.get())
					.define('C', Items.DIAMOND)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(GolemItemRegistry.NETHERITE.get())::unlockedBy, Items.NETHERITE_INGOT)
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.NETHERITE_INGOT)
					.define('B', GolemItemRegistry.EMPTY_UPGRADE.get())
					.define('C', Items.DIAMOND)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(GolemItemRegistry.QUARTZ.get())::unlockedBy, Items.QUARTZ)
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.QUARTZ_BLOCK)
					.define('B', GolemItemRegistry.EMPTY_UPGRADE.get())
					.define('C', Items.QUARTZ)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(GolemItemRegistry.GOLD.get())::unlockedBy, Items.GOLDEN_APPLE)
					.pattern("CAC").pattern("ABA").pattern("CAC")
					.define('A', Items.GOLDEN_APPLE)
					.define('B', GolemItemRegistry.EMPTY_UPGRADE.get())
					.define('C', Items.GOLDEN_CARROT)
					.save(pvd);

			unlock(pvd, ShapedRecipeBuilder.shaped(GolemItemRegistry.SPONGE.get())::unlockedBy, Items.WET_SPONGE)
					.pattern(" A ").pattern("ABA").pattern(" A ")
					.define('A', Items.WET_SPONGE)
					.define('B', GolemItemRegistry.EMPTY_UPGRADE.get())
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(GolemItemRegistry.ENCHANTED_GOLD.get())::unlockedBy, Items.ENCHANTED_GOLDEN_APPLE)
					.requires(GolemItemRegistry.EMPTY_UPGRADE.get()).requires(Items.ENCHANTED_GOLDEN_APPLE)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(GolemItemRegistry.FLOAT.get())::unlockedBy, GolemItemRegistry.EMPTY_UPGRADE.get())
					.requires(GolemItemRegistry.EMPTY_UPGRADE.get()).requires(ItemTags.BOATS)
					.save(pvd);

			unlock(pvd, ShapelessRecipeBuilder.shapeless(GolemItemRegistry.SWIM.get())::unlockedBy, GolemItemRegistry.EMPTY_UPGRADE.get())
					.requires(GolemItemRegistry.EMPTY_UPGRADE.get()).requires(Items.HEART_OF_THE_SEA)
					.save(pvd);

		}

		CompatManager.dispatchGenRecipe(pvd);
	}

	public static <T> T unlock(RegistrateRecipeProvider pvd, BiFunction<String, InventoryChangeTrigger.TriggerInstance, T> func, Item item) {
		return func.apply("has_" + pvd.safeName(item), DataIngredient.items(item).getCritereon(pvd));
	}

}
