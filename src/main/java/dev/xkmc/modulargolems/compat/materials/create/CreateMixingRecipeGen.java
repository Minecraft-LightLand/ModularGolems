package dev.xkmc.modulargolems.compat.materials.create;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfigGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class CreateMixingRecipeGen {

	public static void genAllUpgradeRecipes(RegistrateRecipeProvider pvd) {
		var ing = gatherConfig();
		ProcessingRecipeSerializer<MixingRecipe> serializer = AllRecipeTypes.MIXING.getSerializer();
		for (var part : GolemPart.LIST) {
			for (var ent : ing.entrySet()) {
				String item_name = ForgeRegistries.ITEMS.getResourceKey(part).get().location().getPath();
				String name = item_name + "_apply_" + ent.getKey().getPath();
				var recipe = new ProcessingRecipeBuilder<>(serializer.getFactory(), new ResourceLocation(ModularGolems.MODID, name));
				recipe.require(part);
				for (int i = 0; i < part.count; i++) {
					recipe.require(ent.getValue());
				}
				recipe.requiresHeat(HeatCondition.HEATED);
				recipe.output(GolemPart.setMaterial(part.getDefaultInstance(), ent.getKey()));
				String modid = ent.getKey().getNamespace();
				recipe.withCondition(new ModLoadedCondition(CreateDispatch.MODID));
				if (!modid.equals(ModularGolems.MODID) && !modid.equals(CreateDispatch.MODID)) {
					recipe.withCondition(new ModLoadedCondition(modid));
				}
				recipe.build(pvd);
			}
		}
	}

	@SuppressWarnings("ConstantConditions")
	private static Map<ResourceLocation, Ingredient> gatherConfig() {
		ConfigDataProvider.Collector map = new ConfigDataProvider.Collector(new HashMap<>());
		for (ModDispatch dispatch : CompatManager.LIST) {
			var gen = dispatch.getDataGen(null);
			gen.add(map);
		}
		new MGConfigGen(null).add(map);
		Map<ResourceLocation, Ingredient> ing = new HashMap<>();
		for (ConfigDataProvider.ConfigEntry<?> config : map.map().values()) {
			if (config.config() instanceof GolemMaterialConfig mat) {
				ing.putAll(mat.ingredients);
			}
		}
		return ing;
	}

}
