package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.xkmc.l2complements.init.L2Complements;
import dev.xkmc.l2complements.init.data.TagGen;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.*;
import dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers.*;
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

import java.util.List;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;
import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemItems.regMultiModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class CataCompatRegistry {

	public static final ItemEntry<Item> HARBINGER_TEMPLATE, MONSTROSITY_TEMPLATE;
	public static final ItemEntry<HarbingerArmorItem> HARBINGER_HELMET, HARBINGER_CHESTPLATE, HARBINGER_SHINGUARD;
	public static final ItemEntry<MonstrosityArmorItem> MONSTROSITY_HELMET, MONSTROSITY_CHESTPLATE, MONSTROSITY_SHINGUARD;
	public static final ItemEntry<IgnisArmorItem> IGNIS_HELMET, IGNIS_CHESTPLATE, IGNIS_SHINGUARD;
	public static final ItemEntry<MaledictusArmorItem> MALEDICTUS_HELMET, MALEDICTUS_CHESTPLATE, MALEDICTUS_SHINGUARD;

	public static final RegistryEntry<IgnisFireballModifier> IGNIS_FIREBALL;
	public static final RegistryEntry<IgnisJumpModifier> IGNIS_JUMP;
	public static final RegistryEntry<IgnisAttackModifier> IGNIS_ATTACK;
	public static final RegistryEntry<IgnisChargeModifier> IGNIS_CHARGE;
	public static final RegistryEntry<HarbingerDeathBeamModifier> HARBINGER_BEAM;
	public static final RegistryEntry<HarbingerHomingMissileModifier> HARBINGER_MISSILE;
	public static final RegistryEntry<HarbingerHowitzerModifier> HARBINGER_HOWITZER;
	public static final RegistryEntry<HarbingerMissileModifier> HARBINGER_WITHER_MISSILE;
	public static final RegistryEntry<HarbingerChargeModifier> HARBINGER_CHARGE;

	public static final RegistryEntry<LeviathanBlastPortalModifier> PORTAL;
	public static final RegistryEntry<EnderGuardianVoidRuneModifier> RUNE;
	public static final RegistryEntry<EnderGuardianTeleportSlamModifier> GUARDIAN_TELEPORT_SLAM;
	public static final RegistryEntry<EnderGuardianRocketPunchModifier> GUARDIAN_ROCKET;
	public static final RegistryEntry<NetheriteMonstrosityEarthquakeModifier> EARTHQUAKE;
	public static final RegistryEntry<AncientRemnantSandstormModifier> SANDSTORM;
	public static final RegistryEntry<AncientRemnantSteleModifier> ANCIENT_RENANT_STELE;
	public static final RegistryEntry<AncientMeltdownModifier> ANCIENT_MELTDOWN;
	public static final RegistryEntry<MaledictusEarthquakeModifier> EARTHQUAKE_SPEAR;
	public static final RegistryEntry<MaledictusAttackModifier> MALEDICTUS_ATTACK;
	public static final RegistryEntry<MaledictusArrowModifier> MALEDICTUS_ARROW;
	public static final RegistryEntry<ScyllaLightningAttackModifier> SCYLLA_LIGHTNING;
	public static final RegistryEntry<ScyllaWaveAttackModifier> SCYLLA_WAVE;
	public static final RegistryEntry<ScyllaLightningStormModifier> SCYLLA_STORM;
	public static final RegistryEntry<ScyllaWaterBoltModifier> SCYLLA_WATER_BOLT;
	public static final RegistryEntry<ScyllaLineLightningModifier> SCYLLA_LINE;
	public static final RegistryEntry<EnderGuardianHugModifier> GUARDIAN_HUG;
	public static final RegistryEntry<AncientRemnantLeapModifier> ANCIENT_RENANT_LEAP;
	public static final RegistryEntry<AncientRemnantChargeModifier> ANCIENT_RENANT_CHARGE;
	public static final RegistryEntry<MaledictusChargeModifier> MALEDICTUS_CHARGE;
	public static final RegistryEntry<ScyllaAnchorSlamModifier> SCYLLA_ANCHOR_SLAM;
	public static final RegistryEntry<ScyllaSpinChargeModifier> SCYLLA_SPIN;

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
						ArmorItem.Type.HELMET, 14, 8, 0.2F, CataArmorPaths.WITHERITE_HELMETS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		HARBINGER_CHESTPLATE = REGISTRATE.item("harbinger_chestplate", p -> new HarbingerArmorItem(p.stacksTo(1),
						ArmorItem.Type.CHESTPLATE, 18, 8, 0.2F, CataArmorPaths.WITHERITE_CHESTPLATES))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		HARBINGER_SHINGUARD = REGISTRATE.item("harbinger_shinguard", p -> new HarbingerArmorItem(p.stacksTo(1),
						ArmorItem.Type.LEGGINGS, 10, 8, 0.2F, CataArmorPaths.WITHERITE_LEGGINGS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();

		MONSTROSITY_HELMET = REGISTRATE.item("monstrosity_helmet", p -> new MonstrosityArmorItem(p.stacksTo(1),
						ArmorItem.Type.HELMET, 14, 8, 0.3F, CataArmorPaths.MONSTROSITY_HELMETS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		MONSTROSITY_CHESTPLATE = REGISTRATE.item("monstrosity_chestplate", p -> new MonstrosityArmorItem(p.stacksTo(1),
						ArmorItem.Type.CHESTPLATE, 18, 8, 0.3F, CataArmorPaths.MONSTROSITY_CHESTPLATES))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();
		MONSTROSITY_SHINGUARD = REGISTRATE.item("monstrosity_shinguard", p -> new MonstrosityArmorItem(p.stacksTo(1),
						ArmorItem.Type.LEGGINGS, 10, 8, 0.3F, CataArmorPaths.MONSTROSITY_LEGGINGS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.defaultLang().register();

		IGNIS_HELMET = REGISTRATE.item("ignis_helmet", p -> new IgnisArmorItem(p.stacksTo(1),
						ArmorItem.Type.HELMET, 14, 8, 0.3F, CataArmorPaths.IGNIS_HELMETS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.lang("Ignitium Golem Helmet").register();
		IGNIS_CHESTPLATE = REGISTRATE.item("ignis_chestplate", p -> new IgnisArmorItem(p.stacksTo(1),
						ArmorItem.Type.CHESTPLATE, 18, 8, 0.3F, CataArmorPaths.IGNIS_CHESTPLATES))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.lang("Ignitium Golem Chestplate").register();
		IGNIS_SHINGUARD = REGISTRATE.item("ignis_shinguard", p -> new IgnisArmorItem(p.stacksTo(1),
						ArmorItem.Type.LEGGINGS, 10, 8, 0.3F, CataArmorPaths.IGNIS_LEGGINGS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.lang("Ignitium Golem Shinguard").register();

		MALEDICTUS_HELMET = REGISTRATE.item("maledictus_helmet", p -> new MaledictusArmorItem(p.stacksTo(1),
						ArmorItem.Type.HELMET, 14, 8, 0.1F, CataArmorPaths.MALEDICTUS_HELMETS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.lang("Cursium Golem Helmet").register();
		MALEDICTUS_CHESTPLATE = REGISTRATE.item("maledictus_chestplate", p -> new MaledictusArmorItem(p.stacksTo(1),
						ArmorItem.Type.CHESTPLATE, 18, 8, 0.1F, CataArmorPaths.MALEDICTUS_CHESTPLATES))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.lang("Cursium Golem Chestplate").register();
		MALEDICTUS_SHINGUARD = REGISTRATE.item("maledictus_shinguard", p -> new MaledictusArmorItem(p.stacksTo(1),
						ArmorItem.Type.LEGGINGS, 10, 8, 0.1F, CataArmorPaths.MALEDICTUS_LEGGINGS))
				.model((ctx, pvd) -> pvd.generated(ctx, cataLoc("item/equipments/" + ctx.getName())))
				.lang("Cursium Golem Shinguard").register();

		VOID_CUBE = GolemItems.item(CataDispatch.MODID, "void_cube", RepairMaterialItem::new);
		VOID_CONSTRUCT = GolemItems.item(CataDispatch.MODID, "void_construct", CraftMaterialItem::new);

		AZURE_CUBE = GolemItems.item(CataDispatch.MODID, "azure_cube", RepairMaterialItem::new);
		STORM_CONSTRUCT = GolemItems.item(CataDispatch.MODID, "storm_construct", CraftMaterialItem::new);

		IGNIS_FIREBALL = reg("ignis_fireball", () -> new IgnisFireballModifier(StatFilterType.HEAD, 2),
				"Blazing Fireball", "Summon ignis fireballs and shoot them toward the target.");

		IGNIS_JUMP = reg("ignis_jump", () -> new IgnisJumpModifier(StatFilterType.MOVEMENT, 1),
				"Blazing Strike", "Jump toward the target. On landing, perform a blazing strike at all nearby targets. Damage taken is reduced by %s while leaping.");

		IGNIS_ATTACK = reg("ignis_attack", () -> new IgnisAttackModifier(StatFilterType.ATTACK, 2),
				"Ignis Blade", "Stack Blazing Brand effect and regenerate health when hitting the target. When health is lower than %s, damage bypasses armor.");

		IGNIS_CHARGE = reg("ignis_charge", () -> new IgnisChargeModifier(StatFilterType.HEAD, 1),
				"Raging Charge", "Charge toward a distant target, damaging and stunning all targets in the path. Damage taken is reduced by %s while charging.");


		HARBINGER_BEAM = reg("harbinger_death_beam", () -> new HarbingerDeathBeamModifier(StatFilterType.HEAD, 1),
				"Death Beam", "Shoot a death beam toward the target.");

		HARBINGER_MISSILE = reg("harbinger_missile", () -> new HarbingerHomingMissileModifier(StatFilterType.ATTACK, 2),
				"Harbinger Homing Missile", "Shoot homing missiles toward the target, continuously tracking the target.");		HARBINGER_HOWITZER = reg("harbinger_howitzer", () -> new HarbingerHowitzerModifier(StatFilterType.ATTACK, 2),
				"Harbinger Howitzer", "Launch wither howitzers toward the target, leaving wither smoke.");
		HARBINGER_WITHER_MISSILE = reg("harbinger_wither_missile", () -> new HarbingerMissileModifier(StatFilterType.ATTACK, 2),
				"Harbinger Wither Missile", "Shoot wither missiles toward the target, inflicting wither on hit.");

		PORTAL = reg("leviathan_blast_portal", LeviathanBlastPortalModifier::new,
				"Abyssal Blast Portal", "Unleash abyssal blast portals at multiple target positions.");
		RUNE = reg("ender_guardian_void_rune", EnderGuardianVoidRuneModifier::new,
				"Void Rune", "Summon void runes around itself and toward the target, and spawn a void vortex at the target's feet.");
		EARTHQUAKE = reg("netherite_monstrosity_earthquake", NetheriteMonstrosityEarthquakeModifier::new,
				"Jump and cause earthquake on landing. Damage taken is reduced by %s while leaping.");
		SANDSTORM = reg("ancient_remnant_sandstorm", AncientRemnantSandstormModifier::new,
				"Ancient Sandstorm", "Summon a sandstorm around itself in melee mode, or at the target position in ranged mode. Deal more damage to targets afflicted with the Curse of Desert.");
		ANCIENT_RENANT_STELE = reg("ancient_remnant_desert_stele", () -> new AncientRemnantSteleModifier(StatFilterType.HEAD, 1),
				"Desert Stele", "Summon many desert steles from above in front of and around itself, smashing them into the ground.");
		ANCIENT_MELTDOWN = reg("ancient_remnant_reformation", AncientMeltdownModifier::new,
				"Remnant Reformation", "Reforge: consume body material to restore health, at the cost of reduced max health and smaller size. Restorable with repair material.");
		EARTHQUAKE_SPEAR = reg("maledictus_earthquake", MaledictusEarthquakeModifier::new,
				"Phantom Halberd Jump Attack", "Jump and cause area damage on landing, summoning phantom halberds. Damage taken is reduced by %s while leaping.");
		MALEDICTUS_ATTACK = reg("maledictus_attack", MaledictusAttackModifier::new,
				"Cursed Blade", "Golem melee damage bypasses armor. Deal increased damage after dealing damage, stacking up to %s layers.");
		MALEDICTUS_ARROW = reg("maledictus_arrow", () -> new MaledictusArrowModifier(StatFilterType.ATTACK, 2),
				"Cursed Arrow", "When the target is faraway, shoot homing phantom arrows toward it.");

		HARBINGER_CHARGE = reg("harbinger_charge", () -> new HarbingerChargeModifier(StatFilterType.HEAD, 1),
				"Harbinger Charge", "Charge toward a distant target, damaging all targets in the path. Damage taken is reduced by %s while charging, up to %s dashes.");


		ANCIENT_RENANT_LEAP = reg("ancient_remnant_leap", AncientRemnantLeapModifier::new,
				"Ancient Leap", "Jump toward the target. On landing, deal area damage to nearby targets and release earthquake shock waves. Damage taken is reduced by %s while leaping.");
		ANCIENT_RENANT_CHARGE = reg("ancient_remnant_charge", () -> new AncientRemnantChargeModifier(StatFilterType.HEAD, 1),
				"Rage Charge", "Charge toward a distant target, damaging all targets in the path. Damage taken is reduced by %s while charging.");
		MALEDICTUS_CHARGE = reg("maledictus_charge", () -> new MaledictusChargeModifier(StatFilterType.HEAD, 1),
				"Cursed Charge", "Charge toward a distant target, damaging all targets in the path. Damage taken is reduced by %s while charging, up to %s dashes.");
		SCYLLA_LIGHTNING = reg("scylla_lightning", ScyllaLightningAttackModifier::new,
				"Thunder Lance", "Every 5 seconds, shoot a lightning spear at the target, followed by two more after a short delay.");
		SCYLLA_WAVE = reg("scylla_wave", ScyllaWaveAttackModifier::new,
				"Tidal Anchor", "When attacked, summon waves to push attackers away.");
		SCYLLA_STORM = reg("scylla_lightning_storm", ScyllaLightningStormModifier::new,
				"Storm Fury", "Gather storm clouds to release many lightning spears and water spears at the target");
		SCYLLA_WATER_BOLT = reg("scylla_water_bolt", ScyllaWaterBoltModifier::new,
				"Tidal Spike", "Every 5 seconds, shoot a water spike at the target, followed by two more after a short delay.");
		SCYLLA_LINE = reg("scylla_line_lightning", ScyllaLineLightningModifier::new,
				"Line Lightning", "Summon a row of lightning strikes at the target position.");
		GUARDIAN_HUG = reg("ender_guardian_hug", () -> new EnderGuardianHugModifier(StatFilterType.ATTACK, 2),
				"Rocket Fist", "Teleport to the predicted target position and unleash heavy damage.");
		SCYLLA_ANCHOR_SLAM = reg("scylla_anchor_slam", ScyllaAnchorSlamModifier::new,
				"Thunder Slam", "Jump toward the target. On landing, deal area damage and create multiple sparks. Damage taken is reduced by %s while leaping.");
		GUARDIAN_TELEPORT_SLAM = reg("ender_guardian_teleport_slam", EnderGuardianTeleportSlamModifier::new,
				"Teleport Slam", "Teleport above the target and slam down, dealing area damage and summoning void runes. Damage taken is reduced by %s while leaping.");
		SCYLLA_SPIN = reg("scylla_spin_charge", () -> new ScyllaSpinChargeModifier(StatFilterType.HEAD, 1),
				"Storm Spin Charge", "Charge toward a distant target while spinning, periodically releasing storm strikes around itself. Damage taken is reduced by %s while charging.");

		GUARDIAN_ROCKET = reg("ender_guardian_rocket_punch", () -> new EnderGuardianRocketPunchModifier(StatFilterType.HEAD, 1),
				"Gravity Charge", "Charge toward a distant target while firing a row of ender bullets, damaging all targets in the path, drawing nearby targets in, and reducing damage taken by %s while charging.");

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
		SCYLLA = regMultiModUpgrade("scylla_lightning_upgrade",
				List.of(() -> SCYLLA_LIGHTNING, () -> SCYLLA_WATER_BOLT), CataDispatch.MODID)
				.lang("Scylla Upgrade").register();

	}

	private static <T extends MobEffect> RegistryEntry<T> genEffect(String name, NonNullSupplier<T> sup, String desc) {
		return ModularGolems.REGISTRATE.effect(name, sup, desc).lang(MobEffect::getDescriptionId).register();
	}

	public static void register() {
		if (ModList.get().isLoaded(L2Complements.MODID)) {
			MGTagGen.OPTIONAL_EFF.add(e -> e.addTag(TagGen.SKILL_EFFECT)
					.addOptional(EFF_FORCE.getId()));
			MGTagGen.OPTIONAL_ITEM.add(e -> e.addTag(MGTagGen.TOUGH_ITEM)
					.addOptional(IGNIS_HELMET.getId())
					.addOptional(IGNIS_CHESTPLATE.getId())
					.addOptional(IGNIS_SHINGUARD.getId())
					.addOptional(HARBINGER_HELMET.getId())
					.addOptional(HARBINGER_CHESTPLATE.getId())
					.addOptional(HARBINGER_SHINGUARD.getId())
					.addOptional(MONSTROSITY_HELMET.getId())
					.addOptional(MONSTROSITY_CHESTPLATE.getId())
					.addOptional(MONSTROSITY_SHINGUARD.getId())
			);
			MGTagGen.OPTIONAL_ITEM.add(e -> e.addTag(MGTagGen.RED_UPGRADES)
					.addOptional(LEVIATHAN.getId())
					.addOptional(ENDER_GUARDIAN.getId())
					.addOptional(MONSTROSITY.getId())
					.addOptional(ANCIENT_REMNANT.getId())
					.addOptional(SCYLLA.getId()));
		}
	}

	public static ResourceLocation cataLoc(String id) {
		return new ResourceLocation(CataDispatch.MODID, id);
	}

}