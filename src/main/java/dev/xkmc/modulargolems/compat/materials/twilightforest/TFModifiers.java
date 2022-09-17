package dev.xkmc.modulargolems.compat.materials.twilightforest;

import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.FieryModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.TFDamageModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.TFHealingModifier;

import static dev.xkmc.modulargolems.init.registrate.GolemModifierRegistry.reg;

public class TFModifiers {

	public static final RegistryEntry<FieryModifier> FIERY;
	public static final RegistryEntry<TFDamageModifier> TF_DAMAGE;
	public static final RegistryEntry<TFHealingModifier> TF_HEALING;

	static {
		FIERY = reg("fiery", FieryModifier::new, "Deal %s%% fire damage to mobs not immune to fire");
		TF_DAMAGE = reg("tf_damage", TFDamageModifier::new, "Deal %s%% damage in twilight forest");
		TF_HEALING = reg("tf_healing", TFHealingModifier::new, "Healing becomes %s%% in twilight forest");
	}

	public static void register() {

	}

}
