package dev.xkmc.modulargolems.compat.materials.botania;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.events.event.GolemInfoEvent;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BotDispatch extends ModDispatch {

	public static final String MODID = "botania";

	public BotDispatch() {
		BotCompatRegistry.register();
		MinecraftForge.EVENT_BUS.register(BotDispatch.class);

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

	@SubscribeEvent
	public static void onGolemInfo(GolemInfoEvent event) {
		if (event.getGolem().hasFlag(GolemFlags.BOTANIA)) {
			event.addLine(BotUtils.getDesc(event.getGolem()));
		}
	}

}
