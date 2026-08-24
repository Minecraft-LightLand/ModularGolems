package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorJumpGroundChargeModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorLargeBombModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorLaserModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorPlasmaOrbModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorSmallBombModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorUltimateEarthquakeModifier;
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
	public static final ItemEntry<Item> CLOUD_CUBE, ANNIHILATION_CUBE, POSESSED_SOUL_CUBE;
	public static final RegistryEntry<SimpleUpgradeItem> UPGRADE_THUNDER;

	static {
		ANCHOR = reg("ancient_anchor", () -> new AncientAnchorModifier(StatFilterType.MASS, 4),
				"Ancient Anchor", "Deal fall attack, cause shockwaves, and stun targets");

		THUNDER = reg("thunderstorm", () -> new ThunderAttackModifier(StatFilterType.MASS, 4),
				"Thunderstorm", "Summon thunderstorm to attack multiple targets. When attacked, summon electric bursts around the golem.");

		PERC = reg("cloud_forming", RootPercAttackModifier::new,
				"Cloud Forming", "Deal more damage to targets with higher health");

		OBLITERATOR_LARGE_BOMB = reg("obliterator_large_bomb", ObliteratorLargeBombModifier::new,
				"Obliterator Large Bomb", "Launch a large annihilation bomb at a single target");
		OBLITERATOR_SMALL_BOMB = reg("obliterator_small_bomb", ObliteratorSmallBombModifier::new,
				"Obliterator Small Bomb", "Launch small annihilation bombs at multiple targets");
		OBLITERATOR_PLASMA_ORB = reg("obliterator_plasma_orb", ObliteratorPlasmaOrbModifier::new,
				"Obliterator Plasma Orb", "Launch plasma orbs at multiple targets");
		OBLITERATOR_LASER = reg("obliterator_laser", ObliteratorLaserModifier::new,
				"Obliterator Laser", "Fire an annihilation beam at a single target, following golem rotation");
		OBLITERATOR_JUMP = reg("obliterator_jump", ObliteratorJumpGroundChargeModifier::new,
				"Obliterator Jump", "Cause earthquake with jump ground charge visuals and proxies");
		OBLITERATOR_ULTIMATE = reg("obliterator_ultimate", ObliteratorUltimateEarthquakeModifier::new,
				"Obliterator Ultimate", "Cause ultimate earthquake with portal and flame visuals");

		UPGRADE_THUNDER = regModUpgrade("thunderstorm", () -> THUNDER, LMDispatch.MODID)
				.lang("Thunderstorm Upgrade").register();

		CLOUD_CUBE = GolemItems.item(LMDispatch.MODID, "cloud_cube", Item::new);
		ANNIHILATION_CUBE = GolemItems.item(LMDispatch.MODID, "annihilation_cube", Item::new);
		POSESSED_SOUL_CUBE = GolemItems.item(LMDispatch.MODID, "posessed_soul_cube", Item::new);
	}

	public static void register() {
	}

}
