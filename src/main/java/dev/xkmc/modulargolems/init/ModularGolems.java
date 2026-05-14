package dev.xkmc.modulargolems.init;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2core.init.L2TagGen;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.simple.Reg;
import dev.xkmc.l2core.serial.config.ConfigTypeEntry;
import dev.xkmc.l2core.serial.config.PacketHandlerWithConfig;
import dev.xkmc.l2damagetracker.contents.attack.AttackEventHandler;
import dev.xkmc.l2menustacker.click.quickaccess.DefaultQuickAccessActions;
import dev.xkmc.l2serial.network.PacketHandler;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.content.capability.*;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.config.GolemPartConfig;
import dev.xkmc.modulargolems.content.entity.common.GuardedEntity;
import dev.xkmc.modulargolems.content.entity.common.ReforgeUpdatePacket;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.entity.weapon.GolemWeaponRegistry;
import dev.xkmc.modulargolems.content.menu.ghost.SetItemFilterToServer;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.registry.OpenConfigMenuToServer;
import dev.xkmc.modulargolems.content.menu.registry.OpenEquipmentMenuToServer;
import dev.xkmc.modulargolems.content.menu.table.GolemUpgradeMenu;
import dev.xkmc.modulargolems.content.menu.table.OpenTableMenuToServer;
import dev.xkmc.modulargolems.events.GolemAttackListener;
import dev.xkmc.modulargolems.events.GolemDispenserBehaviors;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.*;
import dev.xkmc.modulargolems.init.loot.MGGLMGen;
import dev.xkmc.modulargolems.init.registrate.*;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModularGolems.MODID)
@EventBusSubscriber(modid = ModularGolems.MODID)
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
			e -> e.create(OpenEquipmentMenuToServer.class, PacketHandler.NetDir.PLAY_TO_SERVER),
			e -> e.create(OpenTableMenuToServer.class, PacketHandler.NetDir.PLAY_TO_SERVER),
			e -> e.create(TrackerSyncToClient.class, PacketHandler.NetDir.PLAY_TO_CLIENT),
			e -> e.create(TrackerHeartBeatToServer.class, PacketHandler.NetDir.PLAY_TO_SERVER),
			e -> e.create(TrackerDeleteToServer.class, PacketHandler.NetDir.PLAY_TO_SERVER),
			e -> e.create(ReforgeUpdatePacket.class, PacketHandler.NetDir.PLAY_TO_CLIENT),
			e -> e.create(GuardedEntity.GuardedDataToClient.class, PacketHandler.NetDir.PLAY_TO_CLIENT)

	);

	public static final ConfigTypeEntry<GolemPartConfig> PARTS =
			new ConfigTypeEntry<>(HANDLER, "parts", GolemPartConfig.class);
	public static final ConfigTypeEntry<GolemMaterialConfig> MATERIALS =
			new ConfigTypeEntry<>(HANDLER, "materials", GolemMaterialConfig.class);

	public ModularGolems() {
		GolemItems.register();
		GolemTypes.register();
		GolemMiscs.register();
		GolemMiscEntities.register();
		GolemModifiers.register();
		GolemTabRegistry.register();
		MGConfig.init();
		GolemTriggers.register();
		GolemModes.register();
		CurioCompatRegistry.register();
		AttackEventHandler.register(3500, new GolemAttackListener());
		/*
		if (ModList.get().isLoaded(PatchouliAPI.MOD_ID)) {
			new PatchouliHelper(REGISTRATE, "golem_guide")
					.buildModel().buildShapelessRecipe(e -> e
									.requires(Items.BOOK).requires(GolemItems.GOLEM_TEMPLATE),
							() -> Items.BOOK)
					.buildBook("Modular Golem Guide",
							"Welcome to Tinker-like golem assembly and upgrade mod",
							1, GolemItems.ITEMS.key());
		}
		if (ModList.get().isLoaded(CEICommon.ID)) {
			CEICompat.register();
		}

		 */
	}

	public static Identifier loc(String id) {
		return Identifier.fromNamespaceAndPath(MODID, id);
	}

	@SubscribeEvent
	public static void modifyAttributes(EntityAttributeModificationEvent event) {
	}

	@SubscribeEvent
	public static void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			GolemWeaponRegistry.init();
			GolemDispenserBehaviors.registerDispenseBehaviors();
			CompatManager.commonSetup();
			DefaultQuickAccessActions.quickAccess(GolemMiscs.DISINTEGRATE.get(),
					GolemItems.TABLE.asItem(),
					GolemUpgradeMenu::createFloating, MGLangData.TAB_UPGRADES.key());
		});
	}

	@SubscribeEvent
	public static void attachCap(RegisterCapabilitiesEvent event) {
		event.registerEntity(Capabilities.Item.ENTITY, GolemTypes.ENTITY_GOLEM.get(), (e, c) -> e.getItemHandler());
		event.registerEntity(Capabilities.Item.ENTITY, GolemTypes.ENTITY_HUMANOID.get(), (e, c) -> e.getItemHandler());
		event.registerEntity(Capabilities.Item.ENTITY, GolemTypes.ENTITY_DOG.get(), (e, c) -> e.getItemHandler());
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void gatherData(GatherDataEvent.Client event) {

		REGISTRATE.addDataGenerator(ProviderType.LANG, MGLangData::genLang);
		REGISTRATE.addDataGenerator(ProviderType.RECIPE, RecipeGen::genRecipe);
		REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, MGTagGen::onBlockTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, MGTagGen::onItemTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ENTITY_TAGS, MGTagGen::onEntityTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ADVANCEMENT, MGAdvGen::genAdvancements);
		REGISTRATE.addDataGenerator(ProviderType.DATA_MAP, MGDataMapGen::genDataMap);


		var gen = event.getGenerator();
		var pvd = event.getLookupProvider();
		new MGDamageTypes(REGISTRATE).generate();
		gen.addProvider(true, new MGConfigGen(gen, pvd));
		CompatManager.gatherData(event);
		gen.addProvider(true, new SlotGen(gen.getPackOutput(), pvd));
		gen.addProvider(true, new MGGLMGen(gen.getPackOutput(), pvd, MODID));
		var init = REGISTRATE.getDataGenInitializer();
		/*if (ModList.get().isLoaded(L2Complements.MODID)) {
			REGISTRATE.addDataGenerator(L2TagGen.EFF_TAGS, MGTagGen::onEffTagGen);
			init.add(Registries.ENCHANTMENT, LCEnchantments.REG::build);// fill registry
		}*/
		init.addDependency(L2TagGen.ENCH_TAGS, ProviderType.DYNAMIC);
		init.addDependency(ProviderType.RECIPE_RUNNER, L2TagGen.ENCH_TAGS);
	}

}
