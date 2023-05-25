package dev.xkmc.modulargolems.compat.materials.common;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.modulargolems.compat.materials.blazegear.BGDispatch;
import dev.xkmc.modulargolems.compat.materials.create.CreateDispatch;
import dev.xkmc.modulargolems.compat.materials.l2complements.LCDispatch;
import dev.xkmc.modulargolems.compat.materials.twilightforest.TFDispatch;
import dev.xkmc.modulargolems.compat.misc.CEICompat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public abstract class CompatManager {

	public static final List<ModDispatch> LIST = new ArrayList<>();

	public static void register() {
		if (ModList.get().isLoaded(TFDispatch.MODID)) LIST.add(new TFDispatch());
		if (ModList.get().isLoaded(CreateDispatch.MODID)) LIST.add(new CreateDispatch());
		if (ModList.get().isLoaded(LCDispatch.MODID)) LIST.add(new LCDispatch());
		if (ModList.get().isLoaded(BGDispatch.MODID)) LIST.add(new BGDispatch());
		if (ModList.get().isLoaded("create_enchantment_industry")) CEICompat.register();
	}

	public static void dispatchGenLang(RegistrateLangProvider pvd) {
		for (ModDispatch dispatch : LIST) {
			dispatch.genLang(pvd);
		}
	}

	public static void gatherData(GatherDataEvent event) {
		for (ModDispatch dispatch : LIST) {
			var gen = dispatch.getDataGen(event.getGenerator());
			if (gen != null) {
				event.getGenerator().addProvider(event.includeServer(), gen);
			}
		}
	}

	public static void dispatchGenRecipe(RegistrateRecipeProvider pvd) {
		for (ModDispatch dispatch : LIST) {
			dispatch.genRecipe(pvd);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void dispatchClientSetup(IEventBus bus) {
		for (ModDispatch dispatch : LIST) {
			dispatch.dispatchClientSetup(bus);
		}
	}

}
