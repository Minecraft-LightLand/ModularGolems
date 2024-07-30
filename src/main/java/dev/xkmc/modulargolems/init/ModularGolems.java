package dev.xkmc.modulargolems.init;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2complements.init.L2Complements;
import dev.xkmc.l2core.init.L2TagGen;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.simple.Reg;
import dev.xkmc.l2core.serial.config.ConfigTypeEntry;
import dev.xkmc.l2core.serial.config.PacketHandlerWithConfig;
import dev.xkmc.l2damagetracker.contents.attack.AttackEventHandler;
import dev.xkmc.l2serial.network.PacketHandler;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.content.capability.ConfigHeartBeatToServer;
import dev.xkmc.modulargolems.content.capability.ConfigSyncToClient;
import dev.xkmc.modulargolems.content.capability.ConfigUpdateToServer;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.config.GolemPartConfig;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.menu.ghost.SetItemFilterToServer;
import dev.xkmc.modulargolems.content.menu.registry.OpenConfigMenuToServer;
import dev.xkmc.modulargolems.content.menu.registry.OpenEquipmentMenuToServer;
import dev.xkmc.modulargolems.events.GolemAttackListener;
import dev.xkmc.modulargolems.events.GolemDispenserBehaviors;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.*;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModularGolems.MODID)
@EventBusSubscriber(modid = ModularGolems.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModularGolems {

	public static final String MODID = "modulargolems";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Reg REG = new Reg(MODID);
	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);

	public static final PacketHandlerWithConfig HANDLER = new PacketHandlerWithConfig(
			MODID, 1,
			e -> e.create(ConfigSyncToClient.class, PacketHandler.NetDir.PLAY_TO_CLIENT),
			e -> e.create(ConfigUpdateToServer.class, PacketHandler.NetDir.PLAY_TO_SERVER),
			e -> e.create(ConfigHeartBeatToServer.class, PacketHandler.NetDir.PLAY_TO_SERVER),
			e -> e.create(SetItemFilterToServer.class, PacketHandler.NetDir.PLAY_TO_SERVER),
			e -> e.create(OpenConfigMenuToServer.class, PacketHandler.NetDir.PLAY_TO_SERVER),
			e -> e.create(OpenEquipmentMenuToServer.class, PacketHandler.NetDir.PLAY_TO_SERVER)
	);

	public static final ConfigTypeEntry<GolemPartConfig> PARTS =
			new ConfigTypeEntry<>(HANDLER, "parts", GolemPartConfig.class);
	public static final ConfigTypeEntry<GolemMaterialConfig> MATERIALS =
			new ConfigTypeEntry<>(HANDLER, "materials", GolemMaterialConfig.class);

	public ModularGolems() {
		GolemItems.register();
		GolemTypes.register();
		GolemMiscs.register();
		GolemModifiers.register();
		MGConfig.init();
		GolemTriggers.register();
		GolemModes.register();
		CurioCompatRegistry.register();
		REGISTRATE.addDataGenerator(ProviderType.LANG, MGLangData::genLang);
		REGISTRATE.addDataGenerator(ProviderType.RECIPE, RecipeGen::genRecipe);
		REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, MGTagGen::onBlockTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, MGTagGen::onItemTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ENTITY_TAGS, MGTagGen::onEntityTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ADVANCEMENT, MGAdvGen::genAdvancements);
		AttackEventHandler.register(3500, new GolemAttackListener());
	}

	public static ResourceLocation loc(String id) {
		return ResourceLocation.fromNamespaceAndPath(MODID, id);
	}

	@SubscribeEvent
	public static void modifyAttributes(EntityAttributeModificationEvent event) {
	}

	@SubscribeEvent
	public static void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			GolemDispenserBehaviors.registerDispenseBehaviors();
		});
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		var gen = event.getGenerator();
		var pvd = event.getLookupProvider();
		event.getGenerator().addProvider(event.includeServer(), new MGConfigGen(gen, pvd));
		CompatManager.gatherData(event);
		event.getGenerator().addProvider(event.includeServer(), new SlotGen(gen.getPackOutput(), event.getExistingFileHelper(), pvd));
		if (ModList.get().isLoaded(L2Complements.MODID)) {
			REGISTRATE.addDataGenerator(L2TagGen.EFF_TAGS, MGTagGen::onEffTagGen);
		}
	}

}
