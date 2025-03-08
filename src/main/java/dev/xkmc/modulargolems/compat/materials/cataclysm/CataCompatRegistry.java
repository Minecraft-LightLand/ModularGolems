package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.xkmc.l2complements.init.L2Complements;
import dev.xkmc.l2complements.init.data.TagGen;
import dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers.*;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.fml.ModList;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class CataCompatRegistry {

	public static final RegistryEntry<IgnisFireballModifier> IGNIS_FIREBALL;
	public static final RegistryEntry<IgnisAttackModifier> IGNIS_ATTACK;
	public static final RegistryEntry<HarbingerDeathBeamModifier> HARBINGER_BEAM;
	public static final RegistryEntry<HarbingerHomingMissileModifier> HARBINGER_MISSILE;

	public static final RegistryEntry<LeviathanBlastPortalModifier> PORTAL;
	public static final RegistryEntry<EnderGuardianVoidRuneModifier> RUNE;
	public static final RegistryEntry<NetheriteMonstrosityEarthquakeModifier> EARTHQUAKE;
	public static final RegistryEntry<AncientRemnantSandstormModifier> SANDSTORM;
	public static final RegistryEntry<MaledictusEarthquakeModifier> EARTHQUAKE_SPEAR;
	public static final RegistryEntry<MaledictusAttackModifier> MALEDICTUS_ATTACK;

	public static final RegistryEntry<RageEffect> EFF_FORCE;

	public static final RegistryEntry<SimpleUpgradeItem> LEVIATHAN, ENDER_GUARDIAN, MONSTROSITY, ANCIENT_REMNANT;

	static {
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

	private static <T extends MobEffect> RegistryEntry<T> genEffect(String name, NonNullSupplier<T> sup, String desc) {
		return ModularGolems.REGISTRATE.effect(name, sup, desc).lang(MobEffect::getDescriptionId).register();
	}

	public static void register() {
		if (ModList.get().isLoaded(L2Complements.MODID)) {
			MGTagGen.OPTIONAL_EFF.add(e -> e.addTag(TagGen.SKILL_EFFECT)
					.addOptional(EFF_FORCE.getId()));
		}
	}

}
