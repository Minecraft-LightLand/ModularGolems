package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers.*;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class CataCompatRegistry {

	public static final Val<IgnisFireballModifier> IGNIS_FIREBALL;
	public static final Val<IgnisAttackModifier> IGNIS_ATTACK;
	public static final Val<HarbingerDeathBeamModifier> HARBINGER_BEAM;
	public static final Val<HarbingerHomingMissileModifier> HARBINGER_MISSILE;

	public static final Val<LeviathanBlastPortalModifier> PORTAL;
	public static final Val<EnderGuardianVoidRuneModifier> RUNE;
	public static final Val<NetheriteMonstrosityEarthquakeModifier> EARTHQUAKE;
	public static final Val<AncientRemnantSandstormModifier> SANDSTORM;

	public static final ItemEntry<SimpleUpgradeItem> LEVIATHAN, ENDER_GUARDIAN, MONSTROSITY, ANCIENT_REMNANT;

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
		EARTHQUAKE = reg("netherite_monstrosity_earthquake", NetheriteMonstrosityEarthquakeModifier::new, "Jump and cause earthquake on land");
		SANDSTORM = reg("ancient_remnant_sandstorm", AncientRemnantSandstormModifier::new, "When target is faraway, summon sandstorm at target position");

		LEVIATHAN = regModUpgrade("leviathan_blast_portal", () -> PORTAL, CataDispatch.MODID)
				.lang("Leviathan Upgrade").register();
		ENDER_GUARDIAN = regModUpgrade("ender_guardian_void_rune", () -> RUNE, CataDispatch.MODID)
				.lang("Ender Guardian Upgrade").register();
		MONSTROSITY = regModUpgrade("netherite_monstrosity_earthquake", () -> EARTHQUAKE, CataDispatch.MODID)
				.lang("Netherite Monstrosity Upgrade").register();
		ANCIENT_REMNANT = regModUpgrade("ancient_remnant_sandstorm", () -> SANDSTORM, CataDispatch.MODID)
				.lang("Ancient Remnant Upgrade").register();

	}

	public static void register() {

	}

}
