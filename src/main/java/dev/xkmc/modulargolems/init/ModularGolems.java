package dev.xkmc.modulargolems.init;

import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.repack.registrate.providers.ProviderType;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.content.entity.common.mode.GolemModes;
import dev.xkmc.modulargolems.events.CraftEventListeners;
import dev.xkmc.modulargolems.events.GolemEventListeners;
import dev.xkmc.modulargolems.events.ModifierEventListeners;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.*;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModularGolems.MODID)
public class ModularGolems {

	public static final String MODID = "modulargolems";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);

	private static void registerRegistrates(IEventBus bus) {
		GolemItems.register();
		GolemTypes.register();
		GolemMiscs.register();
		GolemModifiers.register();
		ModConfig.init();
		NetworkManager.register();
		CompatManager.register();
		GolemTriggers.register();
		GolemModes.register();
		REGISTRATE.addDataGenerator(ProviderType.LANG, LangData::genLang);
		REGISTRATE.addDataGenerator(ProviderType.RECIPE, RecipeGen::genRecipe);
		REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, TagGen::onBlockTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, TagGen::onItemTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ENTITY_TAGS, TagGen::onEntityTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ADVANCEMENT, AdvGen::genAdvancements);
	}

	private static void registerForgeEvents() {
		MinecraftForge.EVENT_BUS.register(ModifierEventListeners.class);
		MinecraftForge.EVENT_BUS.register(CraftEventListeners.class);
		MinecraftForge.EVENT_BUS.register(GolemEventListeners.class);
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
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> GolemClient.onCtorClient(bus));
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
		CompatManager.gatherData(event);
	}

	private static void sendMessage(final InterModEnqueueEvent event) {

	}

}
