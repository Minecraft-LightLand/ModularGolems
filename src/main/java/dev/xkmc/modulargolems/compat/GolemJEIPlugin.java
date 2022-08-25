package dev.xkmc.modulargolems.compat;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.GolemPart;
import dev.xkmc.modulargolems.content.recipe.GolemAssembleRecipe;
import dev.xkmc.modulargolems.init.ModularGolems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.common.plugins.vanilla.anvil.AnvilRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class GolemJEIPlugin implements IModPlugin {

	public static final ResourceLocation ID = new ResourceLocation(ModularGolems.MODID, "main");

	private static final ResourceLocation EMPTY = new ResourceLocation(ModularGolems.MODID, "empty");

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		for (Item item : GolemPart.LIST) {
			registration.registerSubtypeInterpreter(item, GolemJEIPlugin::partSubtype);
		}
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		GolemMaterialConfig config = GolemMaterialConfig.get();
		List<IJeiAnvilRecipe> recipes = new ArrayList<>();
		for (var mat : config.getAllMaterials()) {
			var arr = config.ingredients.get(mat).getItems();
			for (var item : GolemPart.LIST) {
				List<ItemStack> list = new ArrayList<>();
				for (ItemStack stack : arr) {
					list.add(new ItemStack(stack.getItem(), item.count));
				}
				recipes.add(new AnvilRecipe(List.of(new ItemStack(item)), list,
						List.of(GolemPart.setMaterial(new ItemStack(item), mat))));
			}
		}
		registration.addRecipes(RecipeTypes.ANVIL, recipes);
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addCategoryExtension(GolemAssembleRecipe.class, GolemAssemblyExtension::new);
	}

	private static String partSubtype(ItemStack stack, UidContext ctx) {
		return GolemPart.getMaterial(stack).orElse(EMPTY).toString();
	}

}
