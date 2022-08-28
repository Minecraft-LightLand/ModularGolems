package dev.xkmc.modulargolems.init.registrate;

import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.repack.registrate.providers.ProviderType;
import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2library.repack.registrate.util.nullness.NonNullSupplier;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.common.AttributeGolemModifier;
import dev.xkmc.modulargolems.content.modifier.common.RecycleModifier;
import dev.xkmc.modulargolems.content.modifier.immunes.FireImmuneModifier;
import dev.xkmc.modulargolems.content.modifier.immunes.MagicImmuneModifier;
import dev.xkmc.modulargolems.content.modifier.immunes.ThunderImmuneModifier;
import dev.xkmc.modulargolems.content.modifier.twilightforest.FieryModifier;
import dev.xkmc.modulargolems.content.modifier.twilightforest.ThornModifier;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemModifierRegistry {

	public static final RegistryEntry<FireImmuneModifier> FIRE_IMMUNE;
	public static final RegistryEntry<ThunderImmuneModifier> THUNDER_IMMUNE;
	public static final RegistryEntry<MagicImmuneModifier> MAGIC_IMMUNE;
	public static final RegistryEntry<RecycleModifier> RECYCLE;
	public static final RegistryEntry<AttributeGolemModifier> DIAMOND, NETHERITE, QUARTZ, GOLD;

	public static final RegistryEntry<ThornModifier> THORN;
	public static final RegistryEntry<FieryModifier> FIERY;

	static {
		FIRE_IMMUNE = reg("fire_immune", FireImmuneModifier::new, "Immune to fire damage");
		THUNDER_IMMUNE = reg("thunder_immune", ThunderImmuneModifier::new, "Immune to lightning bolt damage");
		MAGIC_IMMUNE = reg("magic_immune", MagicImmuneModifier::new, "Immune to magic damage");
		RECYCLE = reg("recycle", RecycleModifier::new, "Drop golem holder of 0 health when killed");
		DIAMOND = reg("armor_up", () -> new AttributeGolemModifier(2,
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_ARMOR, 10)
		)).register();
		NETHERITE = reg("toughness_up", () -> new AttributeGolemModifier(2,
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_ARMOR, 10),
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_TOUGH, 6)
		)).register();
		QUARTZ = reg("damage_up", () -> new AttributeGolemModifier(GolemModifier.MAX_LEVEL,
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_ATTACK, 2)
		)).register();
		GOLD = reg("regeneration_up", () -> new AttributeGolemModifier(GolemModifier.MAX_LEVEL,
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_REGEN, 1)
		)).register();

		THORN = reg("thorn", ThornModifier::new, "Reflect %s%% damage");
		FIERY = reg("fiery", FieryModifier::new, "Deal %s%% fire damage to mobs not immune to fire");
	}

	private static <T extends GolemModifier> RegistryEntry<T> reg(String id, NonNullSupplier<T> sup, String def) {
		Mutable<RegistryEntry<T>> holder = new MutableObject<>();
		var ans = REGISTRATE.generic(GolemTypeRegistry.MODIFIERS, id, sup).defaultLang();
		ans.addMiscData(ProviderType.LANG, pvd -> pvd.add(holder.getValue().get().getDescriptionId() + ".desc", def));
		var result = ans.register();
		holder.setValue(result);
		return result;
	}

	private static <T extends GolemModifier> L2Registrate.GenericBuilder<GolemModifier, T> reg(String id, NonNullSupplier<T> sup) {
		return REGISTRATE.generic(GolemTypeRegistry.MODIFIERS, id, sup).defaultLang();
	}

	public static void register() {

	}

}
