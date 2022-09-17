package dev.xkmc.modulargolems.compat.materials.twilightforest;

import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.CarminiteModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.FieryModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.TFDamageModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.TFHealingModifier;
import dev.xkmc.modulargolems.content.upgrades.UpgradeItem;

import static dev.xkmc.modulargolems.init.registrate.GolemItemRegistry.regUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifierRegistry.reg;

public class TFCompatRegistry {

	public static final RegistryEntry<FieryModifier> FIERY;
	public static final RegistryEntry<TFDamageModifier> TF_DAMAGE;
	public static final RegistryEntry<TFHealingModifier> TF_HEALING;
	public static final RegistryEntry<CarminiteModifier> CARMINITE;

	public static final RegistryEntry<UpgradeItem> UP_CARMINITE;

	static {
		FIERY = reg("fiery", FieryModifier::new, "Deal %s%% fire damage to mobs not immune to fire");
		TF_DAMAGE = reg("tf_damage", TFDamageModifier::new, "TF Damage Bonus", "Deal %s%% damage in twilight forest");
		TF_HEALING = reg("tf_healing", TFHealingModifier::new, "TF Healing Bonus", "Healing becomes %s%% in twilight forest");
		CARMINITE = reg("carminite", CarminiteModifier::new, "After being hurt, turn invisible and invinsible for %s seconds");

		UP_CARMINITE = regUpgrade("carminite", () -> CARMINITE).lang("Carminite Upgrade").register();
	}

	public static void register() {

	}

}
