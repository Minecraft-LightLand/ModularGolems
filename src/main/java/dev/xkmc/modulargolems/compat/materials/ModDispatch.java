package dev.xkmc.modulargolems.compat.materials;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import net.minecraft.data.DataGenerator;

public abstract class ModDispatch {

	protected abstract void genLang(RegistrateLangProvider pvd);

	protected abstract ConfigDataProvider getDataGen(DataGenerator gen);

}
