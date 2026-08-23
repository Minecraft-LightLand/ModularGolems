package dev.xkmc.modulargolems.compat.materials.eeeab;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.eeeab.annihilator.AnnihilatorElectromagneticModifier;
import dev.xkmc.modulargolems.compat.materials.eeeab.annihilator.AnnihilatorLaserModifier;
import dev.xkmc.modulargolems.compat.materials.eeeab.annihilator.AnnihilatorMissileModifier;
import dev.xkmc.modulargolems.content.item.upgrade.CraftMaterialItem;
import dev.xkmc.modulargolems.content.item.upgrade.RepairMaterialItem;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.Item;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class EEEABCompatRegistry {

	public static final ItemEntry<RepairMaterialItem> REALM_CUBE;
	public static final ItemEntry<CraftMaterialItem> REALM_CONSTRUCT;

	public static final RegistryEntry<AnnihilatorMissileModifier> ANNIHILATOR_MISSILE;
	public static final RegistryEntry<AnnihilatorLaserModifier> ANNIHILATOR_LASER;
	public static final RegistryEntry<AnnihilatorElectromagneticModifier> ANNIHILATOR_ELECTROMAGNETIC;

	/*
	public static final RegistryEntry<SimpleUpgradeItem> UPGRADE_ANNIHILATOR_MISSILE;
	public static final RegistryEntry<SimpleUpgradeItem> UPGRADE_ANNIHILATOR_LASER;
	public static final RegistryEntry<SimpleUpgradeItem> UPGRADE_ANNIHILATOR_ELECTROMAGNETIC;

	 */

	static {
		REALM_CUBE = GolemItems.item(EEEABDispatch.MODID, "realm_cube", RepairMaterialItem::new);
		REALM_CONSTRUCT = GolemItems.item(EEEABDispatch.MODID, "realm_construct", CraftMaterialItem::new);

		ANNIHILATOR_MISSILE = reg("annihilator_missile", AnnihilatorMissileModifier::new,
				"Annihilator Missile", "Shoot homing missiles at multiple targets. Missiles track and apply electrified effect; low health has chance to become sparkferno");
		ANNIHILATOR_LASER = reg("annihilator_laser", AnnihilatorLaserModifier::new,
				"Annihilator Laser", "Fire infrared-guided laser at single target. Requires line of sight and health below 80%. Deals continuous beam damage");
		ANNIHILATOR_ELECTROMAGNETIC = reg("annihilator_electromagnetic", AnnihilatorElectromagneticModifier::new,
				"Electromagnetic Burst", "Ground pound spawns 6 electromagnetic fields that chase targets and shock nearby entities. Always triggers, ignoring original health condition");
/*
		UPGRADE_ANNIHILATOR_MISSILE = regModUpgrade("annihilator_missile", () -> ANNIHILATOR_MISSILE, EEEABDispatch.MODID)
				.lang("Annihilator Missile Upgrade").register();
		UPGRADE_ANNIHILATOR_LASER = regModUpgrade("annihilator_laser", () -> ANNIHILATOR_LASER, EEEABDispatch.MODID)
				.lang("Annihilator Laser Upgrade").register();
		UPGRADE_ANNIHILATOR_ELECTROMAGNETIC = regModUpgrade("annihilator_electromagnetic", () -> ANNIHILATOR_ELECTROMAGNETIC, EEEABDispatch.MODID)
				.lang("Annihilator Electromagnetic Upgrade").register();

 */
	}

	public static void register() {
	}

}
