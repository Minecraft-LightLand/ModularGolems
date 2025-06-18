package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.miauczel.legendary_monsters.LegendaryMonsters;
import net.miauczel.legendary_monsters.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class LMDispatch extends ModDispatch {

	public static final String MODID = LegendaryMonsters.MOD_ID;

	public LMDispatch() {
		LMCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".molten_metal", "Molten Metal");
		pvd.add("golem_material." + MODID + ".cloud", "Cloud");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LMCompatRegistry.CLOUD_CUBE, 9)::unlockedBy,
						ModItems.AIR_RUNE.get())
				.pattern("RIR").pattern("IXI").pattern("RIR")
				.define('I', GolemItems.GOLEM_TEMPLATE)
				.define('R', ModItems.CLOUD_ROD.get())
				.define('X', Ingredient.of(ModItems.AIR_RUNE.get(), ModItems.ATMOSPHERIC_BOOTS.get()))
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LMCompatRegistry.UPGRADE_THUNDER.get())::unlockedBy,
						ModItems.AIR_RUNE.get())
				.pattern(" X ").pattern("ROR").pattern(" R ")
				.define('R', ModItems.CLOUD_ROD.get())
				.define('X', ModItems.AIR_RUNE.get())
				.define('O', GolemItems.EMPTY_UPGRADE)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

	}

	@Override
	public @Nullable ConfigDataProvider getDataGen(DataGenerator gen) {
		return new LMConfigGen(gen);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void dispatchClientSetup() {
		ModelOverrides.registerOverride(new ResourceLocation(MODID, "cloud"),
				ModelOverride.texturePredicate((e) -> e.getHealth() <= e.getMaxHealth() / 2 ? "_dark" : ""));
	}

}
