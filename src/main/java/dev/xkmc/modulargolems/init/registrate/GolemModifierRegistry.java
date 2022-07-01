package dev.xkmc.modulargolems.init.registrate;

import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2library.repack.registrate.util.nullness.NonNullSupplier;
import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.immunes.FireImmuneModifier;
import dev.xkmc.modulargolems.content.modifier.immunes.MagicImmuneModifier;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemModifierRegistry {

	public static final RegistryEntry<FireImmuneModifier> FIRE_IMMUNE = reg("fire_immune", FireImmuneModifier::new);
	public static final RegistryEntry<MagicImmuneModifier> MAGIC_IMMUNE = reg("magic_immune", MagicImmuneModifier::new);

	private static <T extends GolemModifier> RegistryEntry<T> reg(String id, NonNullSupplier<T> sup) {
		return REGISTRATE.generic(GolemTypeRegistry.MODIFIERS, id, sup).defaultLang().register();
	}

	public static void register() {

	}

}
