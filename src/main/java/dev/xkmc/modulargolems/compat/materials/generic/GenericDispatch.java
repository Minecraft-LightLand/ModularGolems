package dev.xkmc.modulargolems.compat.materials.generic;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import net.minecraft.data.DataGenerator;

public class GenericDispatch extends ModDispatch {

    public static final String MODID = "modulargolems";

    public GenericDispatch() {
        GenericCompatRegistry.register();
    }


    @Override
    protected void genLang(RegistrateLangProvider pvd) {
        pvd.add("golem_material." + MODID + ".lead", "Lead");
        pvd.add("golem_material." + MODID + ".tin", "Tin");
        pvd.add("golem_material." + MODID + ".silver", "Silver");
    }

    @Override
    public void genRecipe(RegistrateRecipeProvider pvd) {

    }

    @Override
    public ConfigDataProvider getDataGen(DataGenerator gen) {
        return new GenericConfigGen(gen);
    }
}
