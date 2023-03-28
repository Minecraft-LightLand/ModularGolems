package dev.xkmc.modulargolems.compat.materials.blazegear;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;

public class BGDispatch extends ModDispatch {

	public static final String MODID = "blazegear";

	public BGDispatch() {
		BGCompatRegistry.register();
	}

	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".brimsteel", "Brimsteel");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
	}

	@Override
	protected ConfigDataProvider getDataGen(DataGenerator gen) {
		return new BGConfigGen(gen);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void dispatchClientSetup(IEventBus bus) {
		DuplicateBlazeArmsLayer.registerLayer();
		bus.addListener(BGClientEvents::registerLayer);
	}
}
