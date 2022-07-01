package dev.xkmc.modulargolems.init.data;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.repack.registrate.util.DataIngredient;
import dev.xkmc.modulargolems.content.recipe.GolemAssembleBuilder;
import dev.xkmc.modulargolems.init.registrate.GolemItemRegistry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
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
				.define('C', Items.CLAY).save(pvd);

		pvd.stonecutting(DataIngredient.items(GolemItemRegistry.GOLEM_TEMPLATE.get()), GolemItemRegistry.GOLEM_BODY);
		pvd.stonecutting(DataIngredient.items(GolemItemRegistry.GOLEM_TEMPLATE.get()), GolemItemRegistry.GOLEM_ARM);
		pvd.stonecutting(DataIngredient.items(GolemItemRegistry.GOLEM_TEMPLATE.get()), GolemItemRegistry.GOLEM_LEGS);

		unlock(pvd, new GolemAssembleBuilder(GolemItemRegistry.HOLDER_GOLEM.get(), 1)::unlockedBy,
				GolemItemRegistry.GOLEM_BODY.get())
				.pattern("ABA").pattern(" L ")
				.define('A', GolemItemRegistry.GOLEM_ARM.get())
				.define('B', GolemItemRegistry.GOLEM_BODY.get())
				.define('L', GolemItemRegistry.GOLEM_LEGS.get())
				.save(pvd);

	}

	public static void craft(RegistrateRecipeProvider pvd, Item output, Item center, Item input, Item corner) {
		unlock(pvd, new ShapedRecipeBuilder(output, 1)::unlockedBy, input)
				.pattern("CCC").pattern("BAB").pattern("CCC")
				.define('A', center)
				.define('B', input)
				.define('C', corner)
				.save(pvd);
	}

	public static void addSmithing(String id) {

	}

	public static <T> T unlock(RegistrateRecipeProvider pvd, BiFunction<String, InventoryChangeTrigger.TriggerInstance, T> func, Item item) {
		return func.apply("has_" + pvd.safeName(item), DataIngredient.items(item).getCritereon(pvd));
	}

}
