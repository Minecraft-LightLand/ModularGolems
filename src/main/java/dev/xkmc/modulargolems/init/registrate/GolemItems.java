package dev.xkmc.modulargolems.init.registrate;

import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumaniodGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.*;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.function.Supplier;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemItems {

	public static final RegistryEntry<CreativeModeTab> TAB;

	static {
		TAB = REGISTRATE.buildL2CreativeTab("golems", "Modular Golems", b -> b
				.icon(GolemItems.HOLDER_GOLEM::asStack));
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
			EMERALD, PICKUP, PICKUP_MENDING, PICKUP_NO_DESTROY, TALENTED, CAULDRON,MOUNT_UPGRADE;

	public static final ItemEntry<RetrievalWandItem> RETRIEVAL_WAND;
	public static final ItemEntry<CommandWandItem> COMMAND_WAND;
	public static final ItemEntry<DispenseWand> DISPENSE_WAND;
	public static final ItemEntry<RiderWandItem> RIDER_WAND;


	static {

		GOLEM_TEMPLATE = REGISTRATE.item("metal_golem_template", Item::new).defaultModel().defaultLang().register();
		RETRIEVAL_WAND = REGISTRATE.item("retrieval_wand", p -> new RetrievalWandItem(p.stacksTo(1))).defaultModel().defaultLang().register();
		COMMAND_WAND = REGISTRATE.item("command_wand", p -> new CommandWandItem(p.stacksTo(1))).defaultModel().defaultLang().register();
		DISPENSE_WAND = REGISTRATE.item("summon_wand", p -> new DispenseWand(p.stacksTo(1))).defaultModel().defaultLang().register();
		RIDER_WAND = REGISTRATE.item("rider_wand", p -> new RiderWandItem(p.stacksTo(1))).defaultModel().defaultLang().register();
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
			MOUNT_UPGRADE = regUpgrade("mount_upgrade",() -> GolemModifiers.MOUNT_UPGRADE).lang("Mount Upgrade").register();

		}


		CompatManager.register();

		// holders
		{
			HOLDER_GOLEM = REGISTRATE.item("metal_golem_holder", p ->
							new GolemHolder<>(p.fireResistant(), GolemTypes.TYPE_GOLEM))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
							.texture("particle", "minecraft:block/clay"))
					.transform(e -> e.tab(TAB.getKey(), x -> e.getEntry().fillItemCategory(x)))
					.tag(MGTagGen.GOLEM_HOLDERS).defaultLang().register();

			HOLDER_HUMANOID = REGISTRATE.item("humanoid_golem_holder", p ->
							new GolemHolder<>(p.fireResistant(), GolemTypes.TYPE_HUMANOID))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
							.texture("particle", "minecraft:block/clay"))
					.transform(e -> e.tab(TAB.getKey(), x -> e.getEntry().fillItemCategory(x)))
					.tag(MGTagGen.GOLEM_HOLDERS).defaultLang().register();

			HOLDER_DOG = REGISTRATE.item("dog_golem_holder", p ->
							new GolemHolder<>(p.fireResistant(), GolemTypes.TYPE_DOG))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
							.texture("particle", "minecraft:block/clay"))
					.transform(e -> e.tab(TAB.getKey(), x -> e.getEntry().fillItemCategory(x)))
					.tag(MGTagGen.GOLEM_HOLDERS).defaultLang().register();
		}

		// metal golem
		{
			GOLEM_BODY = REGISTRATE.item("metal_golem_body", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_GOLEM, MetalGolemPartType.BODY, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
							.texture("particle", "minecraft:block/clay"))
					.transform(e -> e.tab(TAB.getKey(), x -> e.getEntry().fillItemCategory(x)))
					.tag(MGTagGen.GOLEM_PARTS).defaultLang().register();
			GOLEM_ARM = REGISTRATE.item("metal_golem_arm", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_GOLEM, MetalGolemPartType.LEFT, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
							.texture("particle", "minecraft:block/clay"))
					.transform(e -> e.tab(TAB.getKey(), x -> e.getEntry().fillItemCategory(x)))
					.tag(MGTagGen.GOLEM_PARTS).defaultLang().register();
			GOLEM_LEGS = REGISTRATE.item("metal_golem_legs", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_GOLEM, MetalGolemPartType.LEG, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
							.texture("particle", "minecraft:block/clay"))
					.transform(e -> e.tab(TAB.getKey(), x -> e.getEntry().fillItemCategory(x)))
					.tag(MGTagGen.GOLEM_PARTS).defaultLang().register();
		}

		// humanoid golem
		{
			HUMANOID_BODY = REGISTRATE.item("humanoid_golem_body", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_HUMANOID, HumaniodGolemPartType.BODY, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
							.texture("particle", "minecraft:block/clay"))
					.transform(e -> e.tab(TAB.getKey(), x -> e.getEntry().fillItemCategory(x)))
					.tag(MGTagGen.GOLEM_PARTS).defaultLang().register();
			HUMANOID_ARMS = REGISTRATE.item("humanoid_golem_arms", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_HUMANOID, HumaniodGolemPartType.ARMS, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
							.texture("particle", "minecraft:block/clay"))
					.transform(e -> e.tab(TAB.getKey(), x -> e.getEntry().fillItemCategory(x)))
					.tag(MGTagGen.GOLEM_PARTS).defaultLang().register();
			HUMANOID_LEGS = REGISTRATE.item("humanoid_golem_legs", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_HUMANOID, HumaniodGolemPartType.LEGS, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
							.texture("particle", "minecraft:block/clay"))
					.transform(e -> e.tab(TAB.getKey(), x -> e.getEntry().fillItemCategory(x)))
					.tag(MGTagGen.GOLEM_PARTS).defaultLang().register();
		}

		// dog golem
		{
			DOG_BODY = REGISTRATE.item("dog_golem_body", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_DOG, DogGolemPartType.BODY, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
							.texture("particle", "minecraft:block/clay"))
					.transform(e -> e.tab(TAB.getKey(), x -> e.getEntry().fillItemCategory(x)))
					.tag(MGTagGen.GOLEM_PARTS).defaultLang().register();

			DOG_LEGS = REGISTRATE.item("dog_golem_legs", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_DOG, DogGolemPartType.LEGS, 3))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
							.texture("particle", "minecraft:block/clay"))
					.transform(e -> e.tab(TAB.getKey(), x -> e.getEntry().fillItemCategory(x)))
					.tag(MGTagGen.GOLEM_PARTS).defaultLang().register();
		}

	}

	public static ItemBuilder<SimpleUpgradeItem, L2Registrate> regModUpgrade(String id, Supplier<RegistryEntry<? extends GolemModifier>> mod, int lv, boolean foil, String modid) {
		var reg = regUpgradeImpl(id, mod, lv, foil, modid);
		reg.setData(ProviderType.ITEM_TAGS, (a, b) -> b.addTag(MGTagGen.GOLEM_UPGRADES).addOptional(reg.get().getId()));
		return reg;
	}

	public static ItemBuilder<SimpleUpgradeItem, L2Registrate> regModUpgrade(String id, Supplier<RegistryEntry<? extends GolemModifier>> mod, String modid) {
		return regModUpgrade(id, mod, 1, false, modid);
	}

	private static ItemBuilder<SimpleUpgradeItem, L2Registrate> regUpgrade(String id, Supplier<RegistryEntry<? extends GolemModifier>> mod) {
		return regUpgrade(id, mod, 1, false);
	}

	private static ItemBuilder<SimpleUpgradeItem, L2Registrate> regUpgrade(String id, Supplier<RegistryEntry<? extends GolemModifier>> mod, int level, boolean foil) {
		return regUpgradeImpl(id, mod, level, foil, ModularGolems.MODID).tag(MGTagGen.GOLEM_UPGRADES);
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

}
