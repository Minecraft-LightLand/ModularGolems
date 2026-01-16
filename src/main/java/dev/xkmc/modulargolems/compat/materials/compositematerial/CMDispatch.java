package dev.xkmc.modulargolems.compat.materials.compositematerial;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import net.minecraft.data.DataGenerator;

public class CMDispatch extends ModDispatch {

    public static final String MODID = "composite-material";

    public CMDispatch() {
    }

    @Override
    protected void genLang(RegistrateLangProvider pvd) {
        pvd.add("golem_material." + MODID + ".allay", "Allay");
        pvd.add("golem_material." + MODID + ".dungeon", "Dungeon");
        pvd.add("golem_material." + MODID + ".etherite", "Etherite");
        pvd.add("golem_material." + MODID + ".primitive", "Primitive");
    }

    @Override
    public void genRecipe(RegistrateRecipeProvider pvd) {
    }

    @Override
    public ConfigDataProvider getDataGen(DataGenerator gen) {
        return new CMConfigGen(gen);
    }

}
