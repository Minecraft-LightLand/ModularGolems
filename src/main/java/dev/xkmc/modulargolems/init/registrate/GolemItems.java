package dev.xkmc.modulargolems.init.registrate;

import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.repack.registrate.builders.ItemBuilder;
import dev.xkmc.l2library.repack.registrate.providers.ProviderType;
import dev.xkmc.l2library.repack.registrate.util.entry.ItemEntry;
import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumaniodGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.CommandWandItem;
import dev.xkmc.modulargolems.content.item.DispenseWand;
import dev.xkmc.modulargolems.content.item.RetrievalWandItem;
import dev.xkmc.modulargolems.content.item.SimpleUpgradeItem;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemWeaponItem;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.TagGen;
import dev.xkmc.modulargolems.init.material.GolemWeaponType;
import dev.xkmc.modulargolems.init.material.VanillaGolemWeaponMaterial;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.function.Supplier;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemItems {

	public static final Tab GOLEMS = new Tab("golems");

	static {
		REGISTRATE.creativeModeTab(() -> GOLEMS);
	}

	public static final ItemEntry<Item> GOLEM_TEMPLATE, EMPTY_UPGRADE;

	public static final ItemEntry<GolemPart<MetalGolemEntity, MetalGolemPartType>> GOLEM_BODY, GOLEM_ARM, GOLEM_LEGS;
	public static final ItemEntry<GolemHolder<MetalGolemEntity, MetalGolemPartType>> HOLDER_GOLEM;

	public static final ItemEntry<GolemPart<HumanoidGolemEntity, HumaniodGolemPartType>> HUMANOID_BODY, HUMANOID_ARMS, HUMANOID_LEGS;
	public static final ItemEntry<GolemHolder<HumanoidGolemEntity, HumaniodGolemPartType>> HOLDER_HUMANOID;

	public static final ItemEntry<GolemPart<DogGolemEntity, DogGolemPartType>> DOG_BODY, DOG_LEGS;
	public static final ItemEntry<GolemHolder<DogGolemEntity, DogGolemPartType>> HOLDER_DOG;

	public static final ItemEntry<SimpleUpgradeItem> FIRE_IMMUNE, THUNDER_IMMUNE, RECYCLE, DIAMOND, NETHERITE, QUARTZ,
			GOLD, ENCHANTED_GOLD, FLOAT, SPONGE, SWIM, PLAYER_IMMUNE, ENDER_SIGHT, BELL, SPEED, SLOW, WEAK, WITHER,
			EMERALD, PICKUP, PICKUP_MENDING, PICKUP_NO_DESTROY, TALENTED, CAULDRON;

	public static final ItemEntry<RetrievalWandItem> RETRIEVAL_WAND;
	public static final ItemEntry<CommandWandItem> COMMAND_WAND;
	public static final ItemEntry<DispenseWand> DISPENSE_WAND;


	public static final ItemEntry<MetalGolemArmorItem> GOLEMGUARD_HELMET, WINDSPIRIT_HELMET, BARBARICFLAMEVANGUARD_HELMET;
	public static final ItemEntry<MetalGolemArmorItem> GOLEMGUARD_CHESTPLATE, WINDSPIRIT_CHESTPLATE, BARBARICFLAMEVANGUARD_CHESTPLATE;
	public static final ItemEntry<MetalGolemArmorItem> GOLEMGUARD_SHINGUARD, WINDSPIRIT_SHINGUARD, BARBARICFLAMEVANGUARD_SHINGUARD;
	public static final ItemEntry<MetalGolemWeaponItem>[][] METALGOLEM_WEAPON;

	static {

		GOLEM_TEMPLATE = REGISTRATE.item("metal_golem_template", Item::new).defaultModel().defaultLang().register();
		RETRIEVAL_WAND = REGISTRATE.item("retrieval_wand", p -> new RetrievalWandItem(p.stacksTo(1))).defaultModel().defaultLang().register();
		COMMAND_WAND = REGISTRATE.item("command_wand", p -> new CommandWandItem(p.stacksTo(1))).defaultModel().defaultLang().register();
		DISPENSE_WAND = REGISTRATE.item("summon_wand", p -> new DispenseWand(p.stacksTo(1))).defaultModel().defaultLang().register();

		// upgrades
		{
			EMPTY_UPGRADE = REGISTRATE.item("empty_upgrade", Item::new).defaultModel().defaultLang().register();
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
		}


		// golemguard armor
		{
			GOLEMGUARD_HELMET = REGISTRATE.item("roman_guard_helmet", p -> new MetalGolemArmorItem(p.stacksTo(1),
							EquipmentSlot.HEAD, 8, 4, GolemModelPaths.HELMETS))
					.model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/equipments/" + ctx.getName())))
					.defaultLang().register();
			GOLEMGUARD_CHESTPLATE = REGISTRATE.item("roman_guard_chestplate", p -> new MetalGolemArmorItem(p.stacksTo(1),
							EquipmentSlot.CHEST, 10, 4, GolemModelPaths.CHESTPLATES))
					.model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/equipments/" + ctx.getName())))
					.defaultLang().register();
			GOLEMGUARD_SHINGUARD = REGISTRATE.item("roman_guard_shinguard", p -> new MetalGolemArmorItem(p.stacksTo(1),
							EquipmentSlot.LEGS, 6, 4, GolemModelPaths.LEGGINGS))
					.model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/equipments/" + ctx.getName())))
					.defaultLang().register();

			WINDSPIRIT_HELMET = REGISTRATE.item("wind_spirit_helmet", p -> new MetalGolemArmorItem(p.stacksTo(1),
							EquipmentSlot.HEAD, 11, 6, GolemModelPaths.HELMETS))
					.model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/equipments/" + ctx.getName())))
					.defaultLang().register();
			WINDSPIRIT_CHESTPLATE = REGISTRATE.item("wind_spirit_chestplate", p -> new MetalGolemArmorItem(p.stacksTo(1),
							EquipmentSlot.CHEST, 14, 6, GolemModelPaths.CHESTPLATES))
					.model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/equipments/" + ctx.getName())))
					.defaultLang().register();
			WINDSPIRIT_SHINGUARD = REGISTRATE.item("wind_spirit_shinguard", p -> new MetalGolemArmorItem(p.stacksTo(1),
							EquipmentSlot.LEGS, 8, 6, GolemModelPaths.LEGGINGS))
					.model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/equipments/" + ctx.getName())))
					.defaultLang().register();

			BARBARICFLAMEVANGUARD_HELMET = REGISTRATE.item("barbaric_vanguard_helmet", p -> new MetalGolemArmorItem(p.stacksTo(1).fireResistant(),
							EquipmentSlot.HEAD, 14, 8, GolemModelPaths.HELMETS))
					.model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/equipments/" + ctx.getName())))
					.defaultLang().register();
			BARBARICFLAMEVANGUARD_CHESTPLATE = REGISTRATE.item("barbaric_vanguard_chestplate", p -> new MetalGolemArmorItem(p.stacksTo(1).fireResistant(),
							EquipmentSlot.CHEST, 18, 8, GolemModelPaths.CHESTPLATES))
					.model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/equipments/" + ctx.getName())))
					.defaultLang().register();
			BARBARICFLAMEVANGUARD_SHINGUARD = REGISTRATE.item("barbaric_vanguard_shinguard", p -> new MetalGolemArmorItem(p.stacksTo(1).fireResistant(),
							EquipmentSlot.LEGS, 10, 8, GolemModelPaths.LEGGINGS))
					.model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/equipments/" + ctx.getName())))
					.defaultLang().register();
		}

		//metalgolem weapon
		{
			METALGOLEM_WEAPON = GolemWeaponType.build(VanillaGolemWeaponMaterial.values());
		}

		// holders
		{
			HOLDER_GOLEM = REGISTRATE.item("metal_golem_holder", p ->
							new GolemHolder<>(p.fireResistant(), GolemTypes.TYPE_GOLEM))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")).texture("particle", "minecraft:block/clay"))
					.tag(TagGen.GOLEM_HOLDERS).defaultLang().register();

			HOLDER_HUMANOID = REGISTRATE.item("humanoid_golem_holder", p ->
							new GolemHolder<>(p.fireResistant(), GolemTypes.TYPE_HUMANOID))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")).texture("particle", "minecraft:block/clay"))
					.tag(TagGen.GOLEM_HOLDERS).defaultLang().register();

			HOLDER_DOG = REGISTRATE.item("dog_golem_holder", p ->
							new GolemHolder<>(p.fireResistant(), GolemTypes.TYPE_DOG))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")).texture("particle", "minecraft:block/clay"))
					.tag(TagGen.GOLEM_HOLDERS).defaultLang().register();
		}

		// metal golem
		{
			GOLEM_BODY = REGISTRATE.item("metal_golem_body", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_GOLEM, MetalGolemPartType.BODY, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")).texture("particle", "minecraft:block/clay"))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
			GOLEM_ARM = REGISTRATE.item("metal_golem_arm", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_GOLEM, MetalGolemPartType.LEFT, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")).texture("particle", "minecraft:block/clay"))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
			GOLEM_LEGS = REGISTRATE.item("metal_golem_legs", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_GOLEM, MetalGolemPartType.LEG, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")).texture("particle", "minecraft:block/clay"))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
		}

		// humanoid golem
		{
			HUMANOID_BODY = REGISTRATE.item("humanoid_golem_body", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_HUMANOID, HumaniodGolemPartType.BODY, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")).texture("particle", "minecraft:block/clay"))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
			HUMANOID_ARMS = REGISTRATE.item("humanoid_golem_arms", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_HUMANOID, HumaniodGolemPartType.ARMS, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")).texture("particle", "minecraft:block/clay"))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
			HUMANOID_LEGS = REGISTRATE.item("humanoid_golem_legs", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_HUMANOID, HumaniodGolemPartType.LEGS, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")).texture("particle", "minecraft:block/clay"))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
		}

		// dog golem
		{
			DOG_BODY = REGISTRATE.item("dog_golem_body", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_DOG, DogGolemPartType.BODY, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")).texture("particle", "minecraft:block/clay"))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();

			DOG_LEGS = REGISTRATE.item("dog_golem_legs", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_DOG, DogGolemPartType.LEGS, 3))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")).texture("particle", "minecraft:block/clay"))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
		}

	}

	public static ItemBuilder<SimpleUpgradeItem, L2Registrate> regModUpgrade(String id, Supplier<RegistryEntry<? extends GolemModifier>> mod, int lv, boolean foil, String modid) {
		var reg = regUpgradeImpl(id, mod, lv, foil, modid);
		reg.setData(ProviderType.ITEM_TAGS, (a, b) -> b.tag(TagGen.GOLEM_UPGRADES).addOptional(reg.get().getId()));
		return reg;
	}

	public static ItemBuilder<SimpleUpgradeItem, L2Registrate> regModUpgrade(String id, Supplier<RegistryEntry<? extends GolemModifier>> mod, String modid) {
		return regModUpgrade(id, mod, 1, false, modid);
	}

	private static ItemBuilder<SimpleUpgradeItem, L2Registrate> regUpgrade(String id, Supplier<RegistryEntry<? extends GolemModifier>> mod) {
		return regUpgrade(id, mod, 1, false);
	}

	private static ItemBuilder<SimpleUpgradeItem, L2Registrate> regUpgrade(String id, Supplier<RegistryEntry<? extends GolemModifier>> mod, int level, boolean foil) {
		return regUpgradeImpl(id, mod, level, foil, ModularGolems.MODID).tag(TagGen.GOLEM_UPGRADES);
	}

	private static ItemBuilder<SimpleUpgradeItem, L2Registrate> regUpgradeImpl(String id, Supplier<RegistryEntry<? extends GolemModifier>> mod, int level, boolean foil, String modid) {
		return REGISTRATE.item(id, p -> new SimpleUpgradeItem(p, mod.get()::get, level, foil))
				.model((ctx, pvd) -> pvd.generated(ctx, new ResourceLocation(modid, "item/upgrades/" + id))
						.override().predicate(new ResourceLocation(ModularGolems.MODID, "blue_arrow"), 0.5f)
						.model(pvd.getBuilder(pvd.name(ctx) + "_purple")
								.parent(new ModelFile.UncheckedModelFile("item/generated"))
								.texture("layer0", new ResourceLocation(modid, "item/upgrades/" + id))
								.texture("layer1", new ResourceLocation(ModularGolems.MODID, "item/purple_arrow")))
						.end().override().predicate(new ResourceLocation(ModularGolems.MODID, "blue_arrow"), 1)
						.model(pvd.getBuilder(pvd.name(ctx) + "_blue")
								.parent(new ModelFile.UncheckedModelFile("item/generated"))
								.texture("layer0", new ResourceLocation(modid, "item/upgrades/" + id))
								.texture("layer1", new ResourceLocation(ModularGolems.MODID, "item/blue_arrow")))
						.end());
	}

	public static void register() {
	}

	public static class Tab extends CreativeModeTab {

		public Tab(String label) {
			super(ModularGolems.MODID + "." + label);
		}

		@Override
		public ItemStack makeIcon() {
			return HOLDER_GOLEM.asStack();
		}
	}

}
