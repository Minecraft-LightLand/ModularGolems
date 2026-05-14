package dev.xkmc.modulargolems.init.registrate;

import com.mojang.serialization.Codec;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.registrate.SimpleEntry;
import dev.xkmc.l2core.init.reg.simple.DCReg;
import dev.xkmc.l2core.init.reg.simple.DCVal;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.l2core.util.DCStack;
import dev.xkmc.l2menustacker.init.L2MSTagGen;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.content.block.TableBlock;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemPartType;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.card.*;
import dev.xkmc.modulargolems.content.item.data.*;
import dev.xkmc.modulargolems.content.item.equipments.*;
import dev.xkmc.modulargolems.content.item.golem.GolemFacade;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.item.ranged.*;
import dev.xkmc.modulargolems.content.item.render.GolemFacadeRenderer;
import dev.xkmc.modulargolems.content.item.render.GolemHolderRenderer;
import dev.xkmc.modulargolems.content.item.render.GolemPartRenderer;
import dev.xkmc.modulargolems.content.item.render.IsInTag;
import dev.xkmc.modulargolems.content.item.upgrade.AddSlotItem;
import dev.xkmc.modulargolems.content.item.upgrade.AddSlotTemplate;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.content.item.wand.*;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGModelGen;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.material.GolemWeaponType;
import dev.xkmc.modulargolems.init.material.VanillaGolemWeaponMaterial;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemItems {

	public static final SimpleEntry<CreativeModeTab> ITEMS;
	public static final SimpleEntry<CreativeModeTab> UPGRADES;
	public static final SimpleEntry<CreativeModeTab> GOLEMS;

	static {
		ITEMS = REGISTRATE.buildL2CreativeTab("golem_items", "Modular Golems - Items", b -> b
				.icon(GolemItems.GOLEM_TEMPLATE::asStack));
		UPGRADES = REGISTRATE.buildL2CreativeTab("golem_upgrades", "Modular Golems - Upgrades", b -> b
				.icon(GolemItems.RECYCLE::asStack));
		REGISTRATE.defaultCreativeTab(ITEMS.getKey());
	}

	public static final BlockEntry<TableBlock> TABLE;

	public static final ItemEntry<Item> GOLEM_TEMPLATE, EMPTY_UPGRADE;

	public static final ItemEntry<GolemPart<MetalGolemEntity, MetalGolemPartType>> GOLEM_BODY, GOLEM_ARM, GOLEM_LEGS;
	public static final ItemEntry<GolemHolder<MetalGolemEntity, MetalGolemPartType>> HOLDER_GOLEM;

	public static final ItemEntry<GolemPart<HumanoidGolemEntity, HumanoidGolemPartType>> HUMANOID_BODY, HUMANOID_ARMS, HUMANOID_LEGS;
	public static final ItemEntry<GolemHolder<HumanoidGolemEntity, HumanoidGolemPartType>> HOLDER_HUMANOID;

	public static final ItemEntry<GolemPart<DogGolemEntity, DogGolemPartType>> DOG_BODY, DOG_LEGS;
	public static final ItemEntry<GolemHolder<DogGolemEntity, DogGolemPartType>> HOLDER_DOG;

	public static final ItemEntry<SimpleUpgradeItem> FIRE_IMMUNE, THUNDER_IMMUNE, RECYCLE, DIAMOND, NETHERITE, QUARTZ,
			GOLD, ENCHANTED_GOLD, FLOAT, SPONGE, SWIM, PLAYER_IMMUNE, ENDER_SIGHT, BELL, SPEED, SLOW, WEAK, WITHER,
			EMERALD, PICKUP, PICKUP_MENDING, PICKUP_NO_DESTROY, TALENTED, CAULDRON, MOUNT_UPGRADE, SIZE_UPGRADE;

	public static final ItemEntry<RetrievalWandItem> RETRIEVAL_WAND, OMNI_RETRIVAL;
	public static final ItemEntry<CommandWandItem> COMMAND_WAND, OMNI_COMMAND;
	public static final ItemEntry<DispenseWand> DISPENSE_WAND, OMNI_DISPENSE;
	public static final ItemEntry<RiderWandItem> RIDER_WAND, OMNI_RIDER;
	public static final ItemEntry<SquadWandItem> SQUAD_WAND, OMNI_SQUAD;
	public static final ItemEntry<HostileWandItem> HOSTILE_WAND;

	public static final ItemEntry<MetalGolemArmorItem> GOLEMGUARD_HELMET, WINDSPIRIT_HELMET, BARBARICFLAMEVANGUARD_HELMET;
	public static final ItemEntry<MetalGolemArmorItem> GOLEMGUARD_CHESTPLATE, WINDSPIRIT_CHESTPLATE, BARBARICFLAMEVANGUARD_CHESTPLATE;
	public static final ItemEntry<MetalGolemArmorItem> GOLEMGUARD_SHINGUARD, WINDSPIRIT_SHINGUARD, BARBARICFLAMEVANGUARD_SHINGUARD;
	public static final ItemEntry<MetalGolemArmorItem> WINDSPIRIT_BOOTS;
	public static final ItemEntry<NetheriteBootItem> BARBARICFLAMEVANGUARD_BOOTS;
	public static final ItemEntry<MetalGolemWeaponItem>[][] METALGOLEM_WEAPON;
	public static final ItemEntry<MetalGolemBowItem> IRON_BOW;
	public static final ItemEntry<MetalGolemMechaBowItem> NETHERITE_BOW;
	public static final ItemEntry<SonicCannonItem> SONIC_CANNON;
	public static final ItemEntry<BeaconCannonItem> BEACON_CANNON;
	public static final ItemEntry<FlameThrowerItem> FLAME_THROWER;
	public static final ItemEntry<SlicingAxe> SLICING_AXE;
	public static final ItemEntry<HeavySpearItem> HEAVY_SPEAR;
	public static final ItemEntry<MetalGolemBeaconItem> BEACON_BOOTS;
	public static final ItemEntry<ConfigCard>[] CARD;
	public static final ItemEntry<PathRecordCard> CARD_PATH;
	public static final ItemEntry<NameFilterCard> CARD_NAME;
	public static final ItemEntry<EntityTypeFilterCard> CARD_TYPE;
	public static final ItemEntry<UuidFilterCard> CARD_UUID;
	public static final ItemEntry<DefaultFilterCard> CARD_DEF;
	public static final ItemEntry<AddSlotItem> ADD_SLOT, INF_SLOT;
	public static final ItemEntry<AddSlotTemplate> ADD_DIAMOND, ADD_NETHERITE;
	public static final ItemEntry<GolemFacade> FACADE;


	private static final DCReg DC = DCReg.of(ModularGolems.REG);
	public static final DCVal<Identifier> DC_PART_MAT = DC.loc("part_material");
	public static final DCVal<CustomData> ENTITY = DC.reg("golem_entity", CustomData.CODEC, CustomData.STREAM_CODEC, true);
	public static final DCVal<GolemHolderMaterial> HOLDER_MAT = DC.reg("golem_materials", GolemHolderMaterial.class, true);
	public static final DCVal<GolemUpgrade> UPGRADE = DC.reg("upgrades", GolemUpgrade.class, true);
	public static final DCVal<GolemConfigKey> CONFIG_KEY = DC.reg("config_key", GolemConfigKey.class, true);
	public static final DCVal<GolemEquipments> EQUIPMENTS = DC.reg("equipments", GolemEquipments.class, true);
	public static final DCVal<GolemIcon> DC_ICON = DC.reg("golem_as_icon", GolemIcon.class, true);
	public static final DCVal<Double> DC_DISP_HP = DC.doubleVal("display_health");
	public static final DCVal<UUID> DC_OWNER = DC.uuid("owner");
	public static final DCVal<Set<UUID>> DC_FILTER_UUID = DC.uuidSet("filter_uuid");
	public static final DCVal<List<String>> DC_FILTER_NAME = DC.list("filter_name", Codec.STRING, ByteBufCodecs.STRING_UTF8, true);
	public static final DCVal<List<EntityType<?>>> DC_FILTER_ENTITY = DC.list("filter_entity", Wrappers.cast(EntityType.class), true);
	public static final DCVal<PathRecordCard.Pos> DC_PATH = DC.reg("path", PathRecordCard.Pos.class, true);
	public static final DCVal<Integer> DC_CHARGE = DC.intVal("charge");
	public static final DCVal<Long> DC_TIMESTAMP = DC.longVal("time_stamp");
	public static final DCVal<Float> DC_USE_SPEED = DC.floatVal("use_speed");
	public static final DCVal<DCStack> DC_DISPLAY_ITEM = DC.stack("display_item");

	static {

		TABLE = REGISTRATE.block("golem_workbench", TableBlock::new)
				.initialProperties(() -> Blocks.ANVIL)
				.blockstate(() -> MGModelGen::genTable)
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.item().tag(L2MSTagGen.QUICK_ACCESS_VANILLA).build()
				.register();

		GOLEM_TEMPLATE = REGISTRATE.item("metal_golem_template", Item::new).defaultModel().defaultLang().register();

		{
			RETRIEVAL_WAND = REGISTRATE.item("retrieval_wand", p -> new RetrievalWandItem(p.stacksTo(1), null))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_HANDHELD_ITEM))
					.defaultLang().tag(MGTagGen.GOLEM_INTERACT).register();
			COMMAND_WAND = REGISTRATE.item("command_wand", p -> new CommandWandItem(p.stacksTo(1), null))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_HANDHELD_ITEM))
					.defaultLang().tag(MGTagGen.GOLEM_INTERACT).register();
			DISPENSE_WAND = REGISTRATE.item("summon_wand", p -> new DispenseWand(p.stacksTo(1), null))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_HANDHELD_ITEM))
					.defaultLang().tag(MGTagGen.GOLEM_INTERACT).register();
			RIDER_WAND = REGISTRATE.item("rider_wand", p -> new RiderWandItem(p.stacksTo(1), null))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_HANDHELD_ITEM))
					.defaultLang().tag(MGTagGen.GOLEM_INTERACT).register();
			SQUAD_WAND = REGISTRATE.item("squad_wand", p -> new SquadWandItem(p.stacksTo(1), null))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_HANDHELD_ITEM))
					.defaultLang().tag(MGTagGen.GOLEM_INTERACT).register();


			OMNI_COMMAND = REGISTRATE.item("omnipotent_wand_command", p -> new CommandWandItem(p.stacksTo(1), COMMAND_WAND))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_HANDHELD_ITEM, new Material(pvd.modLoc("item/omnipotent_wand"))))
					.lang("Omnipotent Wand: Command").tag(MGTagGen.GOLEM_OMNI_WAND)
					.register();
			OMNI_RETRIVAL = REGISTRATE.item("omnipotent_wand_retrieval", p -> new RetrievalWandItem(p.stacksTo(1), RETRIEVAL_WAND))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_HANDHELD_ITEM, new Material(pvd.modLoc("item/omnipotent_wand"))))
					.lang("Omnipotent Wand: Retrieval").tag(MGTagGen.GOLEM_OMNI_WAND)
					.removeTab(ITEMS.key()).register();
			OMNI_DISPENSE = REGISTRATE.item("omnipotent_wand_summon", p -> new DispenseWand(p.stacksTo(1), DISPENSE_WAND))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_HANDHELD_ITEM, new Material(pvd.modLoc("item/omnipotent_wand"))))
					.lang("Omnipotent Wand: Summon").tag(MGTagGen.GOLEM_OMNI_WAND)
					.removeTab(ITEMS.key()).register();
			OMNI_RIDER = REGISTRATE.item("omnipotent_wand_rider", p -> new RiderWandItem(p.stacksTo(1), RIDER_WAND))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_HANDHELD_ITEM, new Material(pvd.modLoc("item/omnipotent_wand"))))
					.lang("Omnipotent Wand: Rider").tag(MGTagGen.GOLEM_OMNI_WAND)
					.removeTab(ITEMS.key()).register();
			OMNI_SQUAD = REGISTRATE.item("omnipotent_wand_squad", p -> new SquadWandItem(p.stacksTo(1), SQUAD_WAND))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_HANDHELD_ITEM, new Material(pvd.modLoc("item/omnipotent_wand"))))
					.lang("Omnipotent Wand: Squad").tag(MGTagGen.GOLEM_OMNI_WAND)
					.removeTab(ITEMS.key()).register();

		}

		// golemguard armor
		{
			GOLEMGUARD_HELMET = REGISTRATE.item("roman_guard_helmet", p -> new MetalGolemArmorItem(p.stacksTo(1),
							ArmorType.HELMET, 8, 4, GolemModelPaths.HELMETS))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.HEAD_ARMOR_ENCHANTABLE)
					.defaultLang().register();
			GOLEMGUARD_CHESTPLATE = REGISTRATE.item("roman_guard_chestplate", p -> new MetalGolemArmorItem(p.stacksTo(1),
							ArmorType.CHESTPLATE, 10, 4, GolemModelPaths.CHESTPLATES))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR_ENCHANTABLE)
					.defaultLang().register();
			GOLEMGUARD_SHINGUARD = REGISTRATE.item("roman_guard_shinguard", p -> new MetalGolemArmorItem(p.stacksTo(1),
							ArmorType.LEGGINGS, 6, 4, GolemModelPaths.LEGGINGS))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.LEG_ARMOR_ENCHANTABLE)
					.defaultLang().register();

			WINDSPIRIT_HELMET = REGISTRATE.item("wind_spirit_helmet", p -> new MetalGolemArmorItem(p.stacksTo(1),
							ArmorType.HELMET, 11, 6, GolemModelPaths.HELMETS))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.HEAD_ARMOR_ENCHANTABLE)
					.defaultLang().register();
			WINDSPIRIT_CHESTPLATE = REGISTRATE.item("wind_spirit_chestplate", p -> new MetalGolemArmorItem(p.stacksTo(1),
							ArmorType.CHESTPLATE, 14, 6, GolemModelPaths.CHESTPLATES))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR_ENCHANTABLE)
					.defaultLang().register();
			WINDSPIRIT_SHINGUARD = REGISTRATE.item("wind_spirit_shinguard", p -> new MetalGolemArmorItem(p.stacksTo(1),
							ArmorType.LEGGINGS, 8, 6, GolemModelPaths.LEGGINGS))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.LEG_ARMOR_ENCHANTABLE)
					.defaultLang().register();
			WINDSPIRIT_BOOTS = REGISTRATE.item("wind_spirit_boots", p -> new MetalGolemArmorItem(p.stacksTo(1),
							ArmorType.BOOTS, 6, 6, GolemModelPaths.BOOTS_DIAMOND))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.FOOT_ARMOR_ENCHANTABLE)
					.defaultLang().register();

			BARBARICFLAMEVANGUARD_HELMET = REGISTRATE.item("barbaric_vanguard_helmet", p -> new MetalGolemArmorItem(p.stacksTo(1).fireResistant(),
							ArmorType.HELMET, 14, 8, GolemModelPaths.HELMETS))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.HEAD_ARMOR_ENCHANTABLE)
					.tag(MGTagGen.TOUGH_ITEM).defaultLang().register();
			BARBARICFLAMEVANGUARD_CHESTPLATE = REGISTRATE.item("barbaric_vanguard_chestplate", p -> new MetalGolemArmorItem(p.stacksTo(1).fireResistant(),
							ArmorType.CHESTPLATE, 18, 8, GolemModelPaths.CHESTPLATES))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR_ENCHANTABLE)
					.tag(MGTagGen.TOUGH_ITEM).defaultLang().register();
			BARBARICFLAMEVANGUARD_SHINGUARD = REGISTRATE.item("barbaric_vanguard_shinguard", p -> new MetalGolemArmorItem(p.stacksTo(1).fireResistant(),
							ArmorType.LEGGINGS, 10, 8, GolemModelPaths.LEGGINGS))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.LEG_ARMOR_ENCHANTABLE)
					.tag(MGTagGen.TOUGH_ITEM).defaultLang().register();
			BARBARICFLAMEVANGUARD_BOOTS = REGISTRATE.item("barbaric_vanguard_boots", p -> new NetheriteBootItem(p.stacksTo(1).fireResistant(),
							ArmorType.BOOTS, 10, 8, GolemModelPaths.BOOTS_NETHERITE))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.FOOT_ARMOR_ENCHANTABLE)
					.tag(MGTagGen.TOUGH_ITEM).defaultLang().register();

			BEACON_BOOTS = REGISTRATE.item("beacon_boots",
							p -> new MetalGolemBeaconItem(p.stacksTo(1), 4, 4, GolemModelPaths.BOOTS_BEACON))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.FOOT_ARMOR_ENCHANTABLE)
					.tag(MGTagGen.TOUGH_ITEM).register();

		}

		//metalgolem weapon
		{
			METALGOLEM_WEAPON = GolemWeaponType.build(VanillaGolemWeaponMaterial.values());
			SLICING_AXE = SlicingAxe.buildItem("golem_slicing_axe", VanillaGolemWeaponMaterial.DIAMOND);
			HEAVY_SPEAR = REGISTRATE.item("heavy_golem_spear",
							p -> new HeavySpearItem(p.stacksTo(1), 10, 0, 2, 2))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.createItem(pvd.modLoc("long_weapon").toString(), TextureSlot.LAYER0),
									new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.SWEEPING_ENCHANTABLE, ItemTags.SHARP_WEAPON_ENCHANTABLE, ItemTags.MACE_ENCHANTABLE)
					.defaultLang()
					.register();
			IRON_BOW = REGISTRATE.item("iron_mecha_bow", p -> new MetalGolemBowItem(p, 15, 0))
					.model(() -> (ctx, pvd) -> pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.BOW_ENCHANTABLE)
					.register();
			NETHERITE_BOW = REGISTRATE.item("netherite_mecha_bow", p -> new MetalGolemMechaBowItem(p, 30, 5))
					.model(() -> (ctx, pvd) -> pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(ItemTags.BOW_ENCHANTABLE)
					.tag(MGTagGen.TOUGH_ITEM).register();
			SONIC_CANNON = REGISTRATE.item("sonic_cannon", p -> new SonicCannonItem(p.stacksTo(1)))
					.model(() -> (ctx, pvd) -> pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(MGTagGen.TOUGH_ITEM).register();
			BEACON_CANNON = REGISTRATE.item("beacon_cannon", p -> new BeaconCannonItem(p.stacksTo(1)))
					.model(() -> (ctx, pvd) -> pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(MGTagGen.TOUGH_ITEM).register();
			FLAME_THROWER = REGISTRATE.item("flame_thrower", p -> new FlameThrowerItem(p.stacksTo(1)))
					.model(() -> (ctx, pvd) -> pvd.generateFlatItem(ctx.get(), new Material(pvd.modLoc("item/equipments/" + ctx.getName()))))
					.tag(MGTagGen.TOUGH_ITEM).register();
		}

		// cards
		{
			CARD = new ItemEntry[16];
			for (int i = 0; i < 16; i++) {
				DyeColor color = DyeColor.byId(i);
				String name = color.getName();
				CARD[i] = REGISTRATE.item(name + "_config_card", p -> new ConfigCard(p.stacksTo(1), color))
						.model(() -> (ctx, pvd) ->
								pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_ITEM, new Material(pvd.modLoc("item/card/" + name))))
						.tag(MGTagGen.CONFIG_CARD).defaultLang().register();
			}

			CARD_NAME = REGISTRATE.item("target_filter_name", p -> new NameFilterCard(p.stacksTo(1)))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_ITEM, new Material(pvd.modLoc("item/card/name"))))
					.tag(MGTagGen.GOLEM_INTERACT)
					.lang("Target Filter: Datapack").register();
			CARD_TYPE = REGISTRATE.item("target_filter_type", p -> new EntityTypeFilterCard(p.stacksTo(1)))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_ITEM, new Material(pvd.modLoc("item/card/type"))))
					.tag(MGTagGen.GOLEM_INTERACT)
					.lang("Target Filter: Entity Type").register();
			CARD_UUID = REGISTRATE.item("target_filter_uuid", p -> new UuidFilterCard(p.stacksTo(1)))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_ITEM, new Material(pvd.modLoc("item/card/uuid"))))
					.tag(MGTagGen.GOLEM_INTERACT)
					.lang("Target Filter: Entity UUID").register();
			CARD_DEF = REGISTRATE.item("target_filter_default", p -> new DefaultFilterCard(p.stacksTo(1)))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_ITEM, new Material(pvd.modLoc("item/card/default"))))
					.tag(MGTagGen.GOLEM_INTERACT)
					.lang("Target Filter: Default Target").register();
			CARD_PATH = REGISTRATE.item("patrol_path_recorder", p -> new PathRecordCard(p.stacksTo(1)))
					.model(() -> (ctx, pvd) ->
							pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_ITEM, new Material(pvd.modLoc("item/card/path"))))
					.tag(MGTagGen.GOLEM_INTERACT, MGTagGen.CURIO_PATH)
					.lang("Patrol Path Recorder").register();
		}

		// upgrades
		{
			EMPTY_UPGRADE = REGISTRATE.item("empty_upgrade", Item::new).defaultModel().defaultLang().tab(UPGRADES.getKey()).register();
			ADD_DIAMOND = REGISTRATE.item("diamond_expansion_template", p -> new AddSlotTemplate(p, GolemModifiers.DIAMOND_ADD))
					.tag(MGTagGen.EXPANSION).defaultModel().defaultLang().tab(UPGRADES.getKey()).register();
			ADD_NETHERITE = REGISTRATE.item("netherite_expansion_template", p -> new AddSlotTemplate(p, GolemModifiers.NETHERITE_ADD))
					.tag(MGTagGen.EXPANSION).defaultModel().defaultLang().tab(UPGRADES.getKey()).register();

			FIRE_IMMUNE = regUpgrade("fire_immune", () -> GolemModifiers.FIRE_IMMUNE).lang("Fire Immune Upgrade").register();
			THUNDER_IMMUNE = regUpgrade("thunder_immune", () -> GolemModifiers.THUNDER_IMMUNE).lang("Thunder Immune Upgrade").register();
			RECYCLE = regUpgrade("recycle", () -> GolemModifiers.RECYCLE).lang("Recycle Ugpgrade").register();
			DIAMOND = regUpgrade("diamond", () -> GolemModifiers.ARMOR).lang("Diamond Upgrade").register();
			NETHERITE = regUpgrade("netherite", () -> GolemModifiers.TOUGH).lang("Netherite Upgrade").register();
			QUARTZ = regUpgrade("quartz", () -> GolemModifiers.DAMAGE).lang("Quartz Upgrade").register();
			GOLD = regUpgrade("gold", () -> GolemModifiers.REGEN).lang("Golden Apple Upgrade").register();
			ENCHANTED_GOLD = regUpgrade("enchanted_gold", () -> GolemModifiers.REGEN, 2, true).lang("Enchanted Golden Apple Upgrade").register();
			FLOAT = regUpgrade("float", () -> GolemModifiers.FLOAT).lang("Float Upgrade").register();
			SPONGE = regUpgrade("sponge", () -> GolemModifiers.EXPLOSION_RES).lang("Sponge Upgrade").register();
			SWIM = regUpgrade("swim", () -> GolemModifiers.SWIM).lang("Swim Upgrade").register();
			PLAYER_IMMUNE = regUpgrade("player", () -> GolemModifiers.PLAYER_IMMUNE).lang("Player Immune Upgrade").register();
			ENDER_SIGHT = regUpgrade("ender_sight", () -> GolemModifiers.ENDER_SIGHT).lang("Ender Sight Upgrade").register();
			BELL = regUpgrade("bell", () -> GolemModifiers.BELL).lang("Bell Upgrade").register();
			SPEED = regUpgrade("speed", () -> GolemModifiers.SPEED).lang("Speed Upgrade").register();
			SLOW = regUpgrade("slow", () -> GolemModifiers.SLOW).lang("Potion Upgrade: Slowness").register();
			WEAK = regUpgrade("weak", () -> GolemModifiers.WEAK).lang("Potion Upgrade: Weakness").register();
			WITHER = regUpgrade("wither", () -> GolemModifiers.WITHER).lang("Potion Upgrade: Wither").register();
			EMERALD = regUpgrade("emerald", () -> GolemModifiers.EMERALD).lang("Emerald Upgrade").register();
			PICKUP = regUpgrade("pickup", () -> GolemModifiers.PICKUP).lang("Pickup Upgrade").register();
			PICKUP_MENDING = regUpgrade("pickup_mending", () -> GolemModifiers.PICKUP_MENDING).lang("Pickup Augment: Mending").register();
			PICKUP_NO_DESTROY = regUpgrade("pickup_no_destroy", () -> GolemModifiers.PICKUP_NODESTROY).lang("Pickup Augment: No Destroy").register();
			TALENTED = regUpgrade("talented", () -> GolemModifiers.TALENTED).lang("Meta Upgrade: Talented").register();
			CAULDRON = regUpgrade("cauldron", () -> GolemModifiers.CAULDRON).lang("Meta Upgrade: Cauldron").register();
			MOUNT_UPGRADE = regUpgrade("mount_upgrade", () -> GolemModifiers.MOUNT_UPGRADE).lang("Mount Upgrade").register();
			SIZE_UPGRADE = regUpgrade("size_upgrade", () -> GolemModifiers.SIZE_UPGRADE).lang("Size Upgrade").register();

		}

		CompatManager.register();

		ADD_SLOT = REGISTRATE.item("add_1_slot", p -> new AddSlotItem(p, 1)).defaultModel().lang("Add 1 Upgrade Slot").register();
		INF_SLOT = REGISTRATE.item("add_100_slot", p -> new AddSlotItem(p, 100)).defaultModel().lang("Add 100 Upgrade Slots").register();

		GOLEMS = REGISTRATE.buildL2CreativeTab("golems", "Modular Golems - Golems & Parts", b -> b
				.icon(GolemItems.HOLDER_GOLEM::asStack));


		// holders
		{
			HOLDER_GOLEM = regHolder("metal_golem_holder", GolemTypes.TYPE_GOLEM);
			HOLDER_HUMANOID = regHolder("humanoid_golem_holder", GolemTypes.TYPE_HUMANOID);
			HOLDER_DOG = regHolder("dog_golem_holder", GolemTypes.TYPE_DOG);
			GOLEM_BODY = regPart("metal_golem_body", GolemTypes.TYPE_GOLEM, MetalGolemPartType.BODY, 9);
			GOLEM_ARM = regPart("metal_golem_arm", GolemTypes.TYPE_GOLEM, MetalGolemPartType.LEFT, 9);
			GOLEM_LEGS = regPart("metal_golem_legs", GolemTypes.TYPE_GOLEM, MetalGolemPartType.LEG, 9);
			HUMANOID_BODY = regPart("humanoid_golem_body", GolemTypes.TYPE_HUMANOID, HumanoidGolemPartType.BODY, 6);
			HUMANOID_ARMS = regPart("humanoid_golem_arms", GolemTypes.TYPE_HUMANOID, HumanoidGolemPartType.ARMS, 6);
			HUMANOID_LEGS = regPart("humanoid_golem_legs", GolemTypes.TYPE_HUMANOID, HumanoidGolemPartType.LEGS, 6);
			DOG_BODY = regPart("dog_golem_body", GolemTypes.TYPE_DOG, DogGolemPartType.BODY, 6);
			DOG_LEGS = regPart("dog_golem_legs", GolemTypes.TYPE_DOG, DogGolemPartType.LEGS, 3);

		}

		FACADE = REGISTRATE.item("golem_facade", GolemFacade::new)
				.model(() -> (ctx, pvd) ->
						pvd.itemModelOutput.accept(ctx.get(), ItemModelUtils.specialModel(
								ModelLocationUtils.getModelLocation(ctx.get()), new GolemFacadeRenderer.Unbaked())))
				.removeTab(GOLEMS.key())
				.transform(e -> e.tab(ITEMS.key(),
						(x, m) -> e.getEntry().fillItemCategory(m)))
				.tag(MGTagGen.CURIO_SKIN).register();

		CompatManager.lateRegister();

		REGISTRATE.defaultCreativeTab(ITEMS.getKey());
		HOSTILE_WAND = REGISTRATE.item("hostile_wand", p -> new HostileWandItem(p.stacksTo(1)))
				.model(() -> (ctx, pvd) -> pvd.generateFlatItem(ctx.get(), ModelTemplates.FLAT_HANDHELD_ITEM)).defaultLang().tag(MGTagGen.GOLEM_INTERACT).register();

	}

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	ItemEntry<GolemHolder<T, P>> regHolder(String id, Val<GolemType<T, P>> type) {
		return REGISTRATE.item(id, p ->
						new GolemHolder<>(p.fireResistant(), type))
				.model(() -> (ctx, pvd) ->
						pvd.itemModelOutput.accept(ctx.get(), ItemModelUtils.specialModel(
								ModelLocationUtils.getModelLocation(ctx.get()), new GolemHolderRenderer.Unbaked(type.id()))))
				.transform(e -> e.tab(GOLEMS.key(),
						(x, m) -> e.getEntry().fillItemCategory(m)))
				.tag(MGTagGen.GOLEM_HOLDERS).defaultLang().register();
	}

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	ItemEntry<GolemPart<T, P>> regPart(String id, Val<GolemType<T, P>> type, P part, int count) {
		return REGISTRATE.item(id, p ->
						new GolemPart<>(p.fireResistant(), type, part, count))
				.model(() -> (ctx, pvd) ->
						pvd.itemModelOutput.accept(ctx.get(), ItemModelUtils.specialModel(
								ModelLocationUtils.getModelLocation(ctx.get()), new GolemPartRenderer.Unbaked(type.id()))))
				.tab(ITEMS.key())
				.transform(e -> e.tab(GOLEMS.key(),
						(x, m) -> e.getEntry().fillItemCategory(m)))
				.tag(MGTagGen.GOLEM_PARTS).defaultLang().register();
	}

	public static ItemBuilder<SimpleUpgradeItem, L2Registrate> regModUpgrade(String id, Supplier<Val<? extends GolemModifier>> mod, int lv, boolean foil, String modid) {
		return regUpgradeImpl(id, mod, lv, foil, modid).asOptional().tag(MGTagGen.GOLEM_UPGRADES);
	}

	public static ItemBuilder<SimpleUpgradeItem, L2Registrate> regModUpgrade(String id, Supplier<Val<? extends GolemModifier>> mod, String modid) {
		return regModUpgrade(id, mod, 1, false, modid);
	}

	private static ItemBuilder<SimpleUpgradeItem, L2Registrate> regUpgrade(String id, Supplier<Val<? extends GolemModifier>> mod) {
		return regUpgrade(id, mod, 1, false);
	}

	private static ItemBuilder<SimpleUpgradeItem, L2Registrate> regUpgrade(String id, Supplier<Val<? extends GolemModifier>> mod, int level, boolean foil) {
		return regUpgradeImpl(id, mod, level, foil, ModularGolems.MODID).tag(MGTagGen.GOLEM_UPGRADES);
	}

	private static ItemBuilder<SimpleUpgradeItem, L2Registrate> regUpgradeImpl(String id, Supplier<Val<? extends GolemModifier>> mod, int level, boolean foil, String modid) {
		return REGISTRATE.item(id, p -> new SimpleUpgradeItem(p, mod.get()::get, level, foil))
				.model(() -> (ctx, pvd) ->
						pvd.itemModelOutput.accept(ctx.get(), ItemModelUtils.conditional(new IsInTag(MGTagGen.BLUE_UPGRADES),
								ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(
										ModelLocationUtils.getModelLocation(ctx.get(), "_blue"),
										TextureMapping.layered(
												new Material(Identifier.fromNamespaceAndPath(modid, "item/upgrades/" + id)),
												new Material(pvd.modLoc("item/blue_arrow"))),
										pvd.modelOutput)),
								ItemModelUtils.conditional(new IsInTag(MGTagGen.POTION_UPGRADES),
										ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(
												ModelLocationUtils.getModelLocation(ctx.get(), "_purple"),
												TextureMapping.layered(
														new Material(Identifier.fromNamespaceAndPath(modid, "item/upgrades/" + id)),
														new Material(pvd.modLoc("item/purple_arrow"))),
												pvd.modelOutput)),
										ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ctx.get(), TextureMapping.layer0(
														new Material(Identifier.fromNamespaceAndPath(modid, "item/upgrades/" + id))),
												pvd.modelOutput))
								))))
				.removeTab(ITEMS.key()).tab(UPGRADES.key());
	}

	public static <T extends Item> ItemEntry<T> item(String modid, String id, NonNullFunction<Item.Properties, T> func) {
		return REGISTRATE.item(id, func)
				.model(() -> (ctx, pvd) ->
						pvd.generateFlatItem(ctx.get(), new Material(Identifier.fromNamespaceAndPath(modid, "item/" + id))))
				.register();
	}

	public static void register() {
	}

}
