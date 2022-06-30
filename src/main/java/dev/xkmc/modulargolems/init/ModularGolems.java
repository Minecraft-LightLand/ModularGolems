package dev.xkmc.modulargolems.init;

import dev.xkmc.l2library.repack.registrate.providers.ProviderType;
import dev.xkmc.modulargolems.events.GolemEvents;
import dev.xkmc.modulargolems.init.data.*;
import dev.xkmc.modulargolems.init.registrate.GolemItemRegistry;
import dev.xkmc.modulargolems.init.registrate.GolemMiscRegistry;
import dev.xkmc.modulargolems.init.registrate.GolemRegistrate;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModularGolems.MODID)
public class ModularGolems {

	public static final String MODID = "modulargolems";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final GolemRegistrate REGISTRATE = new GolemRegistrate(MODID);

	private static void registerRegistrates(IEventBus bus) {
		GolemTypeRegistry.register();
		GolemMiscRegistry.register();
		GolemItemRegistry.register();
		ModConfig.init();
		NetworkManager.register();
		REGISTRATE.addDataGenerator(ProviderType.LANG, LangData::genLang);
		REGISTRATE.addDataGenerator(ProviderType.RECIPE, RecipeGen::genRecipe);
	}

	private static void registerForgeEvents() {
		MinecraftForge.EVENT_BUS.register(GolemEvents.class);
	}

	private static void registerModBusEvents(IEventBus bus) {
		bus.addListener(ModularGolems::modifyAttributes);
		bus.addListener(ModularGolems::setup);
		bus.addListener(ModularGolems::gatherData);
		bus.addListener(ModularGolems::sendMessage);
	}

	public ModularGolems() {
		FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
		IEventBus bus = ctx.getModEventBus();
		registerModBusEvents(bus);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> GolemClient.onCtorClient(bus, MinecraftForge.EVENT_BUS));
		registerRegistrates(bus);
		registerForgeEvents();
	}

	private static void modifyAttributes(EntityAttributeModificationEvent event) {
	}

	private static void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
		});
	}

	public static void gatherData(GatherDataEvent event) {
		event.getGenerator().addProvider(event.includeServer(), new ConfigGen(event.getGenerator()));
		MaterialGen.onGatherData(event);
		//event.getGenerator().addProvider(event.includeServer(), new ArtifactGLMProvider(event.getGenerator()));
	}

	private static void sendMessage(final InterModEnqueueEvent event) {

	}

}
