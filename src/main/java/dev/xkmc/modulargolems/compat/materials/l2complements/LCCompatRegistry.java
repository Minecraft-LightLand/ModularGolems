package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.l2complements.modifiers.ConduitModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.CarminiteModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.FieryModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.TFDamageModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.TFHealingModifier;
import dev.xkmc.modulargolems.content.item.UpgradeItem;
import dev.xkmc.modulargolems.content.modifier.common.AttributeGolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;

import static dev.xkmc.modulargolems.init.registrate.GolemItemRegistry.regUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifierRegistry.THORN;
import static dev.xkmc.modulargolems.init.registrate.GolemModifierRegistry.reg;

public class LCCompatRegistry {

	public static final RegistryEntry<ConduitModifier> CONDUIT;

	static {
		CONDUIT =  reg("conduit", ConduitModifier::new, "Boosts all stats in water, attacks mobs in water like conduit.");
	}

	public static void register() {

	}

}
