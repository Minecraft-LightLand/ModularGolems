package dev.xkmc.modulargolems.compat.materials.compositematerial;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import net.minecraft.data.DataGenerator;

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
    }

    @Override
    public ConfigDataProvider getDataGen(DataGenerator gen) {
        // Returns a new instance of CMConfigGen for data generation
        return new CMConfigGen(gen);
    }

}
