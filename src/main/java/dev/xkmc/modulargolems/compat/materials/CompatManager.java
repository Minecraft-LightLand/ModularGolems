package dev.xkmc.modulargolems.compat.materials;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.modulargolems.compat.materials.twilightforest.TFDispatch;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public abstract class CompatManager {

	public static final List<ModDispatch> LIST = new ArrayList<>();

	public static void register() {
		if (ModList.get().isLoaded(TFDispatch.MODID)) LIST.add(new TFDispatch());
	}

	public static void dispatchGenLang(RegistrateLangProvider pvd) {
		for (ModDispatch dispatch : LIST) {
			dispatch.genLang(pvd);
		}
	}

	public static void gatherData(GatherDataEvent event) {
		for (ModDispatch dispatch : LIST) {
			event.getGenerator().addProvider(event.includeServer(), dispatch.getDataGen(event.getGenerator()));
		}
	}

}
