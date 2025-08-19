package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.xkmc.l2complements.init.L2Complements;
import dev.xkmc.l2complements.init.data.TagGen;
import dev.xkmc.modulargolems.compat.materials.alexscaves.ACDispatch;
import dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers.*;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.CraftMaterialItem;
import dev.xkmc.modulargolems.content.item.upgrade.RepairMaterialItem;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;
import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class CataCompatRegistry {

	public static final ItemEntry<Item> HARBINGER_TEMPLATE, MONSTROSITY_TEMPLATE;
	public static final ItemEntry<HarbingerArmorItem> HARBINGER_HELMET, HARBINGER_CHESTPLATE, HARBINGER_SHINGUARD;
	public static final ItemEntry<MonstrosityArmorItem> MONSTROSITY_HELMET, MONSTROSITY_CHESTPLATE, MONSTROSITY_SHINGUARD;

	public static final RegistryEntry<IgnisFireballModifier> IGNIS_FIREBALL;
	public static final RegistryEntry<IgnisAttackModifier> IGNIS_ATTACK;
	public static final RegistryEntry<HarbingerDeathBeamModifier> HARBINGER_BEAM;
	public static final RegistryEntry<HarbingerHomingMissileModifier> HARBINGER_MISSILE;

	public static final RegistryEntry<LeviathanBlastPortalModifier> PORTAL;
	public static final RegistryEntry<EnderGuardianVoidRuneModifier> RUNE;
	public static final RegistryEntry<NetheriteMonstrosityEarthquakeModifier> EARTHQUAKE;
	public static final RegistryEntry<AncientRemnantSandstormModifier> SANDSTORM;
	public static final RegistryEntry<AncientMeltdownModifier> ANCIENT_MELTDOWN;
	public static final RegistryEntry<MaledictusEarthquakeModifier> EARTHQUAKE_SPEAR;
	public static final RegistryEntry<MaledictusAttackModifier> MALEDICTUS_ATTACK;
	public static final RegistryEntry<ScyllaLightningAttackModifier> SCYLLA_LIGHTNING;
	public static final RegistryEntry<ScyllaWaveAttackModifier> SCYLLA_WAVE;

	public static final RegistryEntry<RageEffect> EFF_FORCE;

	public static final RegistryEntry<SimpleUpgradeItem> LEVIATHAN, ENDER_GUARDIAN, MONSTROSITY, ANCIENT_REMNANT, SCYLLA;
	public static final RegistryEntry<RepairMaterialItem> VOID_CUBE, AZURE_CUBE;
	public static final RegistryEntry<CraftMaterialItem> VOID_CONSTRUCT, STORM_CONSTRUCT;

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
				.defaultLang().register();
		HARBINGER_CHESTPLATE = REGISTRATE.item("harbinger_chestplate", p -> new HarbingerArmorItem(p.stacksTo(1),
						ArmorItem.Type.CHESTPLATE, 18, 8, GolemModelPaths.WITHERITE_CHESTPLATES))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		HARBINGER_SHINGUARD = REGISTRATE.item("harbinger_shinguard", p -> new HarbingerArmorItem(p.stacksTo(1),
						ArmorItem.Type.LEGGINGS, 10, 8, GolemModelPaths.WITHERITE_LEGGINGS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();

		MONSTROSITY_HELMET = REGISTRATE.item("monstrosity_helmet", p -> new MonstrosityArmorItem(p.stacksTo(1),
						ArmorItem.Type.HELMET, 14, 8, GolemModelPaths.MONSTROSITY_HELMETS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		MONSTROSITY_CHESTPLATE = REGISTRATE.item("monstrosity_chestplate", p -> new MonstrosityArmorItem(p.stacksTo(1),
						ArmorItem.Type.CHESTPLATE, 18, 8, GolemModelPaths.MONSTROSITY_CHESTPLATES))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		MONSTROSITY_SHINGUARD = REGISTRATE.item("monstrosity_shinguard", p -> new MonstrosityArmorItem(p.stacksTo(1),
						ArmorItem.Type.LEGGINGS, 10, 8, GolemModelPaths.MONSTROSITY_LEGGINGS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();

		VOID_CUBE = GolemItems.item(CataDispatch.MODID, "void_cube", RepairMaterialItem::new);
		VOID_CONSTRUCT = GolemItems.item(CataDispatch.MODID, "void_construct", CraftMaterialItem::new);

		AZURE_CUBE = GolemItems.item(CataDispatch.MODID, "azure_cube", RepairMaterialItem::new);
		STORM_CONSTRUCT =GolemItems.item(CataDispatch.MODID, "storm_construct", CraftMaterialItem::new);

		IGNIS_FIREBALL = reg("ignis_fireball", () -> new IgnisFireballModifier(StatFilterType.HEAD, 2),
				"When target is faraway, shoot Ignis fireballs toward target.");

		IGNIS_ATTACK = reg("ignis_attack", () -> new IgnisAttackModifier(StatFilterType.ATTACK, 2),
				"Stack Blazing Brande effect and regenerate health when hit target. When health is lower than half, direct damage bypasses armor.");

		HARBINGER_BEAM = reg("harbinger_death_beam", () -> new HarbingerDeathBeamModifier(StatFilterType.HEAD, 1),
				"When target is faraway, shoot Death Beam toward target.");

		HARBINGER_MISSILE = reg("harbinger_missile", () -> new HarbingerHomingMissileModifier(StatFilterType.ATTACK, 2),
				"When target is faraway, shoot Homing Missile toward target.");

		PORTAL = reg("leviathan_blast_portal", LeviathanBlastPortalModifier::new, "Ccreate blast portal at target position. Attacks multiple targets");
		RUNE = reg("ender_guardian_void_rune", EnderGuardianVoidRuneModifier::new, "Summon vortex and void rune toward multiple targets");
		EARTHQUAKE = reg("netherite_monstrosity_earthquake", NetheriteMonstrosityEarthquakeModifier::new, "Jump and cause earthquake on landing");
		SANDSTORM = reg("ancient_remnant_sandstorm", AncientRemnantSandstormModifier::new, "Summon sandstorm at target position. Attacks multiple targets");
		ANCIENT_MELTDOWN = reg("ancient_remnant_reformation", AncientMeltdownModifier::new, "Reforge: Consumes body material to repair itself at the cost of max health. Consumption be restored with ingot.");
		EARTHQUAKE_SPEAR = reg("maledictus_earthquake", MaledictusEarthquakeModifier::new, "Jump and cause earthquake on landing, summoning halberds");
		MALEDICTUS_ATTACK = reg("maledictus_attack", MaledictusAttackModifier::new,
				"Golem melee damage bypass armor. Stack rage counter after dealing damage, up to %s layers");
		SCYLLA_LIGHTNING = reg("scylla_lightning", ScyllaLightningAttackModifier::new,
				"Shoot lightning spear at multiple targets");
		SCYLLA_WAVE = reg("scylla_wave", ScyllaWaveAttackModifier::new,
				"When attacked, summon waves to push attackers away");

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
		SCYLLA = regModUpgrade("scylla_lightning_upgrade", () -> SCYLLA_LIGHTNING, CataDispatch.MODID)
				.lang("Scylla Upgrade").register();

	}

	private static <T extends MobEffect> RegistryEntry<T> genEffect(String name, NonNullSupplier<T> sup, String desc) {
		return ModularGolems.REGISTRATE.effect(name, sup, desc).lang(MobEffect::getDescriptionId).register();
	}

	public static void register() {
		if (ModList.get().isLoaded(L2Complements.MODID)) {
			MGTagGen.OPTIONAL_EFF.add(e -> e.addTag(TagGen.SKILL_EFFECT)
					.addOptional(EFF_FORCE.getId()));
		}
	}

	private static ResourceLocation cataLoc(String id) {
		return new ResourceLocation(CataDispatch.MODID, id);
	}

}
