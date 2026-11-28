package dev.xkmc.modulargolems.compat.materials.eeeab;

import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.eeeab.annihilator.AnnihilatorLaserModifier;
import dev.xkmc.modulargolems.compat.materials.eeeab.realm.AnnihilationMissileModifier;
import dev.xkmc.modulargolems.compat.materials.eeeab.realm.RealmWardenBladeModifier;
import dev.xkmc.modulargolems.compat.materials.eeeab.realm.RealmWardenJumpSmashModifier;
import dev.xkmc.modulargolems.content.item.upgrade.CraftMaterialItem;
import dev.xkmc.modulargolems.content.item.upgrade.RepairMaterialItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class EEEABCompatRegistry {

	/**
	 * The relic's beam, reimplemented so the golem owns its emitter. Registered here rather than in
	 * GolemMiscEntities because it only exists to carry EEEAB's visuals; MG's own beam is the beacon laser.
	 */
	public static final EntityEntry<MGGuardianLaserEntity> GUARDIAN_LASER = ModularGolems.REGISTRATE
			.<MGGuardianLaserEntity>entity("eeeab_guardian_laser", MGGuardianLaserEntity::new, MobCategory.MISC)
			.properties(p -> p.fireImmune().noSave().noSummon().sized(0, 0))
			.renderer(() -> MGGuardianLaserRenderer::new)
			.register();

	public static final ItemEntry<RepairMaterialItem> REALM_CUBE;
	public static final ItemEntry<CraftMaterialItem> REALM_CONSTRUCT;

	public static final RegistryEntry<AnnihilationMissileModifier> ANNIHILATION_MISSILE;
	public static final RegistryEntry<AnnihilatorLaserModifier> ANNIHILATOR_LASER;
	public static final RegistryEntry<RealmWardenJumpSmashModifier> REALM_WARDEN_JUMP_SMASH;
	public static final RegistryEntry<RealmWardenBladeModifier> REALM_WARDEN_BLADE;

	/*
	public static final RegistryEntry<SimpleUpgradeItem> UPGRADE_ANNIHILATOR_MISSILE;
	public static final RegistryEntry<SimpleUpgradeItem> UPGRADE_ANNIHILATOR_LASER;

	 */

	static {
		REALM_CUBE = GolemItems.item(EEEABDispatch.MODID, "realm_cube", RepairMaterialItem::new);
		REALM_CONSTRUCT = GolemItems.item(EEEABDispatch.MODID, "realm_construct", CraftMaterialItem::new);

		ANNIHILATION_MISSILE = reg("annihilation_missile", AnnihilationMissileModifier::new,
				"Annihilation Missile", "Leap back and fire a barrage of homing annihilation missiles that explode and apply electrified effect");
		ANNIHILATOR_LASER = reg("annihilator_laser", AnnihilatorLaserModifier::new,
				"Annihilator Laser", "Fire infrared-guided laser at single target. Deals continuous beam damage");
		REALM_WARDEN_BLADE = reg("realm_warden_blade", RealmWardenBladeModifier::new,
				"Realm Warden's Blade", "When health is below %s of max, gain %s attack damage. Melee hits stack electrified on the target");
		REALM_WARDEN_JUMP_SMASH = reg("realm_warden_jump_smash", RealmWardenJumpSmashModifier::new,
				"Realm Warden Leap Smash", "Leap to the target and hover, marking the landing spot before crashing down. The landing deals shieldable damage and disables blocking, then armor and shield ignoring damage follows shortly after. Damaged while leaping is reduced by %s");
/*
		UPGRADE_ANNIHILATOR_MISSILE = regModUpgrade("annihilator_missile", () -> ANNIHILATOR_MISSILE, EEEABDispatch.MODID)
				.lang("Annihilator Missile Upgrade").register();
		UPGRADE_ANNIHILATOR_LASER = regModUpgrade("annihilator_laser", () -> ANNIHILATOR_LASER, EEEABDispatch.MODID)
				.lang("Annihilator Laser Upgrade").register();

 */
	}

	public static void register() {
	}

}