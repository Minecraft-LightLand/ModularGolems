package dev.xkmc.modulargolems.init.registrate;

import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2library.repack.registrate.util.nullness.NonNullSupplier;
import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.AttributeGolemModifier;
import dev.xkmc.modulargolems.content.modifier.RecycleModifier;
import dev.xkmc.modulargolems.content.modifier.immunes.FireImmuneModifier;
import dev.xkmc.modulargolems.content.modifier.immunes.MagicImmuneModifier;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemModifierRegistry {

	public static final RegistryEntry<FireImmuneModifier> FIRE_IMMUNE = reg("fire_immune", FireImmuneModifier::new);
	public static final RegistryEntry<MagicImmuneModifier> MAGIC_IMMUNE = reg("magic_immune", MagicImmuneModifier::new);
	public static final RegistryEntry<RecycleModifier> RECYCLE = reg("recycle", RecycleModifier::new);
	public static final RegistryEntry<AttributeGolemModifier> DIAMOND, NETHERITE, QUARTZ;

	static {
		DIAMOND = reg("diamond", () -> new AttributeGolemModifier(2,
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_ARMOR, 10)
		));
		NETHERITE = reg("netherite", () -> new AttributeGolemModifier(2,
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_ARMOR, 10),
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_TOUGH, 6)
		));
		QUARTZ = reg("quartz", () -> new AttributeGolemModifier(5,
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_ATTACK, 2)
		));
	}

	private static <T extends GolemModifier> RegistryEntry<T> reg(String id, NonNullSupplier<T> sup) {
		return REGISTRATE.generic(GolemTypeRegistry.MODIFIERS, id, sup).defaultLang().register();
	}

	public static void register() {

	}

}
