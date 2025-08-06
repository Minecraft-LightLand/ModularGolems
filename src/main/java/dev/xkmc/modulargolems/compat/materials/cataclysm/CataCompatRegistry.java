package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.xkmc.l2complements.init.L2Complements;
import dev.xkmc.l2complements.init.data.LCTagGen;
import dev.xkmc.l2core.init.reg.registrate.SimpleEntry;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers.*;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;
import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class CataCompatRegistry {

	public static final ItemEntry<Item> HARBINGER_TEMPLATE, MONSTROSITY_TEMPLATE;
	public static final ItemEntry<HarbingerArmorItem> HARBINGER_HELMET, HARBINGER_CHESTPLATE, HARBINGER_SHINGUARD;
	public static final ItemEntry<MonstrosityArmorItem> MONSTROSITY_HELMET, MONSTROSITY_CHESTPLATE, MONSTROSITY_SHINGUARD;

	public static final Val<IgnisFireballModifier> IGNIS_FIREBALL;
	public static final Val<IgnisAttackModifier> IGNIS_ATTACK;
	public static final Val<HarbingerDeathBeamModifier> HARBINGER_BEAM;
	public static final Val<HarbingerHomingMissileModifier> HARBINGER_MISSILE;

	public static final Val<LeviathanBlastPortalModifier> PORTAL;
	public static final Val<EnderGuardianVoidRuneModifier> RUNE;
	public static final Val<NetheriteMonstrosityEarthquakeModifier> EARTHQUAKE;
	public static final Val<AncientRemnantSandstormModifier> SANDSTORM;
	public static final Val<MaledictusEarthquakeModifier> EARTHQUAKE_SPEAR;
	public static final Val<MaledictusAttackModifier> MALEDICTUS_ATTACK;

	public static final SimpleEntry<MobEffect> EFF_FORCE;

	public static final ItemEntry<SimpleUpgradeItem> LEVIATHAN, ENDER_GUARDIAN, MONSTROSITY, ANCIENT_REMNANT;

	static {

		HARBINGER_TEMPLATE = REGISTRATE.item("harbinger_upgrade_template", Item::new)
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/" + ctx.getName())))
				.register();

		MONSTROSITY_TEMPLATE = REGISTRATE.item("monstrosity_upgrade_template", Item::new)
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/" + ctx.getName())))
				.register();

		HARBINGER_HELMET = REGISTRATE.item("harbinger_helmet", p -> new HarbingerArmorItem(p.stacksTo(1),
						ArmorItem.Type.HELMET, 14, 8, GolemModelPaths.WITHERITE_HELMETS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.asOptional().tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.HEAD_ARMOR_ENCHANTABLE)
				.defaultLang().register();
		HARBINGER_CHESTPLATE = REGISTRATE.item("harbinger_chestplate", p -> new HarbingerArmorItem(p.stacksTo(1),
						ArmorItem.Type.CHESTPLATE, 18, 8, GolemModelPaths.WITHERITE_CHESTPLATES))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.asOptional().tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR_ENCHANTABLE)
				.defaultLang().register();
		HARBINGER_SHINGUARD = REGISTRATE.item("harbinger_shinguard", p -> new HarbingerArmorItem(p.stacksTo(1),
						ArmorItem.Type.LEGGINGS, 10, 8, GolemModelPaths.WITHERITE_LEGGINGS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.asOptional().tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.LEG_ARMOR_ENCHANTABLE)
				.defaultLang().register();

		MONSTROSITY_HELMET = REGISTRATE.item("monstrosity_helmet", p -> new MonstrosityArmorItem(p.stacksTo(1),
						ArmorItem.Type.HELMET, 14, 8, GolemModelPaths.MONSTROSITY_HELMETS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.asOptional().tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.HEAD_ARMOR_ENCHANTABLE)
				.defaultLang().register();
		MONSTROSITY_CHESTPLATE = REGISTRATE.item("monstrosity_chestplate", p -> new MonstrosityArmorItem(p.stacksTo(1),
						ArmorItem.Type.CHESTPLATE, 18, 8, GolemModelPaths.MONSTROSITY_CHESTPLATES))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.asOptional().tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR_ENCHANTABLE)
				.defaultLang().register();
		MONSTROSITY_SHINGUARD = REGISTRATE.item("monstrosity_shinguard", p -> new MonstrosityArmorItem(p.stacksTo(1),
						ArmorItem.Type.LEGGINGS, 10, 8, GolemModelPaths.MONSTROSITY_LEGGINGS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.asOptional().tag(ItemTags.ARMOR_ENCHANTABLE, ItemTags.LEG_ARMOR_ENCHANTABLE)
				.defaultLang().register();

		IGNIS_FIREBALL = reg("ignis_fireball", () -> new IgnisFireballModifier(StatFilterType.HEAD, 2),
				"When target is faraway, shoot Ignis fireballs toward target.");

		IGNIS_ATTACK = reg("ignis_attack", () -> new IgnisAttackModifier(StatFilterType.ATTACK, 2),
				"Stack Blazing Brande effect and regenerate health when hit target. When health is lower than half, direct damage bypasses armor.");

		HARBINGER_BEAM = reg("harbinger_death_beam", () -> new HarbingerDeathBeamModifier(StatFilterType.HEAD, 1),
				"When target is faraway, shoot Death Beam toward target.");

		HARBINGER_MISSILE = reg("harbinger_missile", () -> new HarbingerHomingMissileModifier(StatFilterType.ATTACK, 2),
				"When target is faraway, shoot Homing Missile toward target.");

		PORTAL = reg("leviathan_blast_portal", LeviathanBlastPortalModifier::new, "When target is faraway, create blast portal at target position");
		RUNE = reg("ender_guardian_void_rune", EnderGuardianVoidRuneModifier::new, "Summon void rune toward target");
		EARTHQUAKE = reg("netherite_monstrosity_earthquake", NetheriteMonstrosityEarthquakeModifier::new, "Jump and cause earthquake on landing");
		SANDSTORM = reg("ancient_remnant_sandstorm", AncientRemnantSandstormModifier::new, "When target is faraway, summon sandstorm at target position");
		EARTHQUAKE_SPEAR = reg("maledictus_earthquake", MaledictusEarthquakeModifier::new, "Jump and cause earthquake on landing, summoning halberds");
		MALEDICTUS_ATTACK = reg("maledictus_attack", MaledictusAttackModifier::new,
				"Golem melee damage bypass armor. Stack rage counter after dealing damage, up to %s layers");

		EFF_FORCE = genEffect("maledictus_rage", () -> new RageEffect(MobEffectCategory.BENEFICIAL, 0xffffffff),
				"Increase golem attack damage");

		LEVIATHAN = regModUpgrade("leviathan_blast_portal", () -> PORTAL, CataDispatch.MODID)
				.lang("Leviathan Upgrade").register();
		ENDER_GUARDIAN = regModUpgrade("ender_guardian_void_rune", () -> RUNE, CataDispatch.MODID)
				.lang("Ender Guardian Upgrade").register();
		MONSTROSITY = regModUpgrade("netherite_monstrosity_earthquake", () -> EARTHQUAKE, CataDispatch.MODID)
				.lang("Netherite Monstrosity Upgrade").register();
		ANCIENT_REMNANT = regModUpgrade("ancient_remnant_sandstorm", () -> SANDSTORM, CataDispatch.MODID)
				.lang("Ancient Remnant Upgrade").register();

	}

	private static <T extends MobEffect> SimpleEntry<MobEffect> genEffect(String name, NonNullSupplier<T> sup, String desc) {
		return new SimpleEntry<>(ModularGolems.REGISTRATE.effect(name, sup, desc).lang(MobEffect::getDescriptionId).register());
	}

	public static void register() {
		if (ModList.get().isLoaded(L2Complements.MODID)) {
			MGTagGen.OPTIONAL_EFF.add(e -> e.addTag(LCTagGen.SKILL_EFFECT)
					.addOptional(EFF_FORCE.val().getId()));
		}

	}

	private static ResourceLocation cataLoc(String id) {
		return  ResourceLocation.fromNamespaceAndPath(CataDispatch.MODID, id);
	}

}
