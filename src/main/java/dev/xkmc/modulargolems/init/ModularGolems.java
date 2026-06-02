package dev.xkmc.modulargolems.init;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2complements.init.L2Complements;
import dev.xkmc.l2complements.init.data.TagGen;
import dev.xkmc.l2damagetracker.contents.attack.AttackEventHandler;
import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.serial.config.ConfigTypeEntry;
import dev.xkmc.l2library.serial.config.PacketHandlerWithConfig;
import dev.xkmc.l2screentracker.click.quickaccess.DefaultQuickAccessActions;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.content.capability.*;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.config.GolemPartConfig;
import dev.xkmc.modulargolems.content.entity.common.GuardedEntity;
import dev.xkmc.modulargolems.content.entity.common.ReforgeUpdatePacket;
import dev.xkmc.modulargolems.content.entity.dog.DogSkillToServer;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.menu.ghost.SetItemFilterToServer;
import dev.xkmc.modulargolems.content.menu.registry.OpenConfigMenuToServer;
import dev.xkmc.modulargolems.content.menu.registry.OpenEquipmentMenuToServer;
import dev.xkmc.modulargolems.content.menu.table.GolemUpgradeMenu;
import dev.xkmc.modulargolems.content.menu.table.OpenTableMenuToServer;
import dev.xkmc.modulargolems.content.menu.wheel.GolemSetModeToServer;
import dev.xkmc.modulargolems.events.GolemAttackListener;
import dev.xkmc.modulargolems.events.GolemDispenserBehaviors;
import dev.xkmc.modulargolems.events.WeaponAttackListener;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.*;
import dev.xkmc.modulargolems.init.loot.MGGLMGen;
import dev.xkmc.modulargolems.init.registrate.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModularGolems.MODID)
@Mod.EventBusSubscriber(modid = ModularGolems.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModularGolems {

	public static final String MODID = "modulargolems";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);
	public static final IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();

	public static final PacketHandlerWithConfig HANDLER = new PacketHandlerWithConfig(
			new ResourceLocation(ModularGolems.MODID, "main"), 4,
			e -> e.create(ConfigSyncToClient.class, NetworkDirection.PLAY_TO_CLIENT),
			e -> e.create(ConfigUpdateToServer.class, NetworkDirection.PLAY_TO_SERVER),
			e -> e.create(ConfigHeartBeatToServer.class, NetworkDirection.PLAY_TO_SERVER),
			e -> e.create(SetItemFilterToServer.class, NetworkDirection.PLAY_TO_SERVER),
			e -> e.create(OpenConfigMenuToServer.class, NetworkDirection.PLAY_TO_SERVER),
			e -> e.create(OpenEquipmentMenuToServer.class, NetworkDirection.PLAY_TO_SERVER),
			e -> e.create(OpenTableMenuToServer.class, NetworkDirection.PLAY_TO_SERVER),
			e -> e.create(TrackerSyncToClient.class, NetworkDirection.PLAY_TO_CLIENT),
			e -> e.create(TrackerHeartBeatToServer.class, NetworkDirection.PLAY_TO_SERVER),
			e -> e.create(TrackerDeleteToServer.class, NetworkDirection.PLAY_TO_SERVER),
			e -> e.create(ReforgeUpdatePacket.class, NetworkDirection.PLAY_TO_CLIENT),
			e -> e.create(GuardedEntity.GuardedDataToClient.class, NetworkDirection.PLAY_TO_CLIENT),
			e -> e.create(DogSkillToServer.class, NetworkDirection.PLAY_TO_SERVER),
			e -> e.create(GolemSetModeToServer.class, NetworkDirection.PLAY_TO_SERVER)
	);

	public static final ConfigTypeEntry<GolemPartConfig> PARTS =
			new ConfigTypeEntry<>(HANDLER, "parts", GolemPartConfig.class);
	public static final ConfigTypeEntry<GolemMaterialConfig> MATERIALS =
			new ConfigTypeEntry<>(HANDLER, "materials", GolemMaterialConfig.class);

	private static void registerRegistrates() {
		GolemItems.register();
		GolemTypes.register();
		GolemMiscs.register();
		GolemMiscEntities.register();
		GolemModifiers.register();
		MGConfig.init();
		GolemTriggers.register();
		GolemModes.register();
		GolemConfigStorage.register();
		CurioCompatRegistry.register();
		AttackEventHandler.register(3500, new GolemAttackListener());
		AttackEventHandler.register(7900, new WeaponAttackListener());

	}

	public ModularGolems() {
		registerRegistrates();
	}

	@SubscribeEvent
	public static void modifyAttributes(EntityAttributeModificationEvent event) {
	}

	@SubscribeEvent
	public static void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			GolemDispenserBehaviors.registerDispenseBehaviors();
			CompatManager.commonSetup();
			GolemWeaponRegistry.init();
			DefaultQuickAccessActions.quickAccess(GolemMiscs.DISINTEGRATE.get(),
					GolemItems.TABLE.asItem(),
					GolemUpgradeMenu::createFloating, MGLangData.TAB_UPGRADES.key());
		});
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void gatherData(GatherDataEvent event) {
		REGISTRATE.addDataGenerator(ProviderType.LANG, MGLangData::genLang);
		REGISTRATE.addDataGenerator(ProviderType.RECIPE, RecipeGen::genRecipe);
		REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, MGTagGen::onBlockTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, MGTagGen::onItemTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ENTITY_TAGS, MGTagGen::onEntityTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ADVANCEMENT, MGAdvGen::genAdvancements);

		var gen = event.getGenerator();
		var server = event.includeServer();
		var pvd = event.getLookupProvider();
		var out = gen.getPackOutput();
		var helper = event.getExistingFileHelper();
		new MGDamageTypes(out, pvd, helper).generate(server, gen);
		gen.addProvider(event.includeServer(), new MGConfigGen(gen));
		CompatManager.gatherData(event);
		gen.addProvider(event.includeServer(), new SlotGen(gen));
		gen.addProvider(event.includeServer(), new MGGLMGen(gen.getPackOutput(), MODID));
		if (ModList.get().isLoaded(L2Complements.MODID)) {
			REGISTRATE.addDataGenerator(TagGen.EFF_TAGS, MGTagGen::onEffTagGen);
		}
	}

	@SubscribeEvent
	public static void sendMessage(final InterModEnqueueEvent event) {

	}

	public static ResourceLocation loc(String id) {
		return new ResourceLocation(MODID, id);
	}

}
