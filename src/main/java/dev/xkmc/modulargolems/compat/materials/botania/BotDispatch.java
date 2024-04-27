package dev.xkmc.modulargolems.compat.materials.botania;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

public class BotDispatch extends ModDispatch {

    public static final String MODID = "botania";

    public BotDispatch() {
        BotCompatRegistry.register();
    }

    public void genLang(RegistrateLangProvider pvd) {
        pvd.add("golem_material." + MODID + ".manasteel", "Manasteel");
        pvd.add("golem_material." + MODID + ".terrasteel", "Terrasteel");
        pvd.add("golem_material." + MODID + ".elementium", "Elementium");
    }

    @Override
    public void genRecipe(RegistrateRecipeProvider pvd) {

    }

    @Override
    public ConfigDataProvider getDataGen(DataGenerator gen) {
        return new BotConfigGen(gen);
    }

}
