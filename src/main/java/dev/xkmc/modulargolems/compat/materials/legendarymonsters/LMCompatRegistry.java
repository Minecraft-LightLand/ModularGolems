package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.RootPercAttackModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.ThunderAttackModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorJumpGroundChargeModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorLargeBombModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorLaserModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorPlasmaOrbModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorSmallBombModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorUltimateEarthquakeModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin.PhantomDaggerModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin.PaladinSoulBladeLeapModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin.SoulSpikeModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.Item;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class LMCompatRegistry {

	public static final RegistryEntry<AncientAnchorModifier> ANCHOR;
	public static final RegistryEntry<ThunderAttackModifier> THUNDER;
	public static final RegistryEntry<RootPercAttackModifier> PERC;
	public static final RegistryEntry<ObliteratorLargeBombModifier> OBLITERATOR_LARGE_BOMB;
	public static final RegistryEntry<ObliteratorSmallBombModifier> OBLITERATOR_SMALL_BOMB;
	public static final RegistryEntry<ObliteratorPlasmaOrbModifier> OBLITERATOR_PLASMA_ORB;
	public static final RegistryEntry<ObliteratorLaserModifier> OBLITERATOR_LASER;
	public static final RegistryEntry<ObliteratorJumpGroundChargeModifier> OBLITERATOR_JUMP;
	public static final RegistryEntry<ObliteratorUltimateEarthquakeModifier> OBLITERATOR_ULTIMATE;
	public static final RegistryEntry<PhantomDaggerModifier> PHANTOM_DAGGER;
	public static final RegistryEntry<SoulSpikeModifier> SOUL_SPIKE;
	public static final RegistryEntry<PaladinSoulBladeLeapModifier> PALADIN_SOUL_BLADE_LEAP;
	public static final ItemEntry<Item> CLOUD_CUBE, ANNIHILATION_CUBE, POSESSED_SOUL_CUBE;
	public static final RegistryEntry<SimpleUpgradeItem> UPGRADE_THUNDER, UPGRADE_ANNIHILATION_BOMB, UPGRADE_ANNIHILATION_PLASMA;

	static {
		ANCHOR = reg("ancient_anchor", () -> new AncientAnchorModifier(StatFilterType.MASS, 4),
				"Ancient Anchor", "Deal fall attack, cause shockwaves, and stun targets");

		THUNDER = reg("thunderstorm", () -> new ThunderAttackModifier(StatFilterType.MASS, 4),
				"Thunderstorm", "Summon thunderstorm to attack multiple targets. When attacked, summon electric bursts around the golem.");

		PERC = reg("cloud_forming", RootPercAttackModifier::new,
				"Cloud Forming", "Deal more damage to targets with higher health");

		OBLITERATOR_LARGE_BOMB = reg("obliterator_large_bomb", ObliteratorLargeBombModifier::new,
				"Obliterator Cluster Bomb", "Launch a annihilation cluster bomb at a single target");
		OBLITERATOR_SMALL_BOMB = reg("obliterator_small_bomb", ObliteratorSmallBombModifier::new,
				"Obliterator Bomb", "Launch annihilation bombs at multiple targets");
		OBLITERATOR_PLASMA_ORB = reg("obliterator_plasma_orb", ObliteratorPlasmaOrbModifier::new,
				"Obliterator Plasma Orb", "Launch plasma orbs at multiple targets");
		OBLITERATOR_LASER = reg("obliterator_laser", ObliteratorLaserModifier::new,
				"Obliterator Laser", "Fire an annihilation beam at a single target, following golem rotation");
		OBLITERATOR_JUMP = reg("obliterator_jump", ObliteratorJumpGroundChargeModifier::new,
				"Obliterator Jump", "Performs jump attack with plasma orbs");
		OBLITERATOR_ULTIMATE = reg("obliterator_ultimate", ObliteratorUltimateEarthquakeModifier::new,
				"Obliterator Ultimate", "Performs jump attack with plasma portals and plasma flames");

		PHANTOM_DAGGER = reg("phantom_dagger", PhantomDaggerModifier::new,
				"Phantom Dagger", "Throw three homing phantom daggers that track a single target");

		SOUL_SPIKE = reg("soul_spike", SoulSpikeModifier::new,
				"Soul Spike", "Summon soul spikes in front that damage enemies and heal the golem");

		PALADIN_SOUL_BLADE_LEAP = reg("paladin_soul_blade_leap", PaladinSoulBladeLeapModifier::new,
				"Paladin Soul Blade Leap", "Leap toward target and summon fallen soul blades that damage enemies and heal the golem");

		UPGRADE_THUNDER = regModUpgrade("thunderstorm", () -> THUNDER, LMDispatch.MODID)
				.lang("Thunderstorm Upgrade").register();

		UPGRADE_ANNIHILATION_BOMB = regModUpgrade("annihilation_bomb", () -> OBLITERATOR_SMALL_BOMB, LMDispatch.MODID)
				.lang("Annihilation Bomb Upgrade").register();

		UPGRADE_ANNIHILATION_PLASMA = regModUpgrade("annihilation_plasma", () -> OBLITERATOR_PLASMA_ORB, LMDispatch.MODID)
				.lang("Annihilation Plasma Upgrade").register();

		CLOUD_CUBE = GolemItems.item(LMDispatch.MODID, "cloud_cube", Item::new);
		ANNIHILATION_CUBE = GolemItems.item(LMDispatch.MODID, "annihilation_cube", Item::new);
		POSESSED_SOUL_CUBE = GolemItems.item(LMDispatch.MODID, "posessed_soul_cube", Item::new);
	}

	public static void register() {
	}

}
