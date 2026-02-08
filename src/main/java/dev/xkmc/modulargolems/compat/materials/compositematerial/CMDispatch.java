package dev.xkmc.modulargolems.compat.materials.compositematerial;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.compat.materials.twilightforest.TFCompatRegistry;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import io.github.rcneg.compositematerial.common.init.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;

public class CMDispatch extends ModDispatch {

    public static final String MODID = "composite_material";

    public CMDispatch() {
        CMCompatRegistry.register();
    }

    @Override
    protected void genLang(RegistrateLangProvider pvd) {
        pvd.add("golem_material." + MODID + ".allay_steel", "Allay Steel");
        pvd.add("golem_material." + MODID + ".dungeon_steel", "Dungeon Steel");
        pvd.add("golem_material." + MODID + ".etherite", "Etherite");
        pvd.add("golem_material." + MODID + ".primitive", "Primitive");
        pvd.add("golem_material." + MODID + ".obsidian_steel", "Obsidian Steel");
    }

    @Override
    public void genRecipe(RegistrateRecipeProvider pvd) {
        safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CMCompatRegistry.UP_ETHERITE.get())::unlockedBy, ItemRegistry.ETHERITE_INGOT.get())
                .pattern("CAC").pattern("ABA").pattern("CAC")
                .define('A', ItemRegistry.ETHERITE_INGOT.get())
                .define('B', GolemItems.EMPTY_UPGRADE.get())
                .define('C', ItemRegistry.PERKIN.get())
                .save(ConditionalRecipeWrapper.mod(pvd, MODID));

    }

    @Override
    public ConfigDataProvider getDataGen(DataGenerator gen) {
        // Returns a new instance of CMConfigGen for data generation
        return new CMConfigGen(gen);
    }

}
