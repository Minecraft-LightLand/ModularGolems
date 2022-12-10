package dev.xkmc.modulargolems.init.registrate;

import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.l2library.repack.registrate.providers.ProviderType;
import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2library.repack.registrate.util.nullness.NonNullSupplier;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.common.AttributeGolemModifier;
import dev.xkmc.modulargolems.content.modifier.common.FloatModifier;
import dev.xkmc.modulargolems.content.modifier.common.RecycleModifier;
import dev.xkmc.modulargolems.content.modifier.common.ThornModifier;
import dev.xkmc.modulargolems.content.modifier.immunes.*;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemModifierRegistry {

	public static final RegistryEntry<FireImmuneModifier> FIRE_IMMUNE;
	public static final RegistryEntry<ThunderImmuneModifier> THUNDER_IMMUNE;
	public static final RegistryEntry<MagicImmuneModifier> MAGIC_IMMUNE;
	public static final RegistryEntry<MagicResistanceModifier> MAGIC_RES;
	public static final RegistryEntry<RecycleModifier> RECYCLE;
	public static final RegistryEntry<ThornModifier> THORN;
	public static final RegistryEntry<FloatModifier> FLOAT;
	public static final RegistryEntry<ExplosionResistanceModifier> EXPLOSION_RES;
	public static final RegistryEntry<DamageCapModifier> DAMAGE_CAP;
	public static final RegistryEntry<ProjectileRejectModifier> PROJECTILE_REJECT;
	public static final RegistryEntry<ImmunityModifier> IMMUNITY;
	public static final RegistryEntry<AttributeGolemModifier> ARMOR, TOUGH, DAMAGE, REGEN;

	static {
		FIRE_IMMUNE = reg("fire_immune", FireImmuneModifier::new, "Immune to fire damage");
		THUNDER_IMMUNE = reg("thunder_immune", ThunderImmuneModifier::new, "Immune to lightning bolt damage");
		MAGIC_IMMUNE = reg("magic_immune", MagicImmuneModifier::new, "Immune to magic damage");
		MAGIC_RES = reg("magic_resistant", MagicResistanceModifier::new, "Magic damage taken reduced to %s%% of original");
		RECYCLE = reg("recycle", RecycleModifier::new, "Drop golem holder of 0 health when killed");
		ARMOR = reg("armor_up", () -> new AttributeGolemModifier(2,
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_ARMOR, 10)
		)).register();
		TOUGH = reg("toughness_up", () -> new AttributeGolemModifier(2,
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_ARMOR, 10),
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_TOUGH, 6)
		)).register();
		DAMAGE = reg("damage_up", () -> new AttributeGolemModifier(GolemModifier.MAX_LEVEL,
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_ATTACK, 2)
		)).register();
		REGEN = reg("regeneration_up", () -> new AttributeGolemModifier(GolemModifier.MAX_LEVEL,
				new AttributeGolemModifier.AttrEntry(GolemTypeRegistry.STAT_REGEN, 1)
		)).register();

		THORN = reg("thorn", ThornModifier::new, "Reflect %s%% damage");
		FLOAT = reg("float", FloatModifier::new, "Golem floats in water and lava instead of sinking");
		EXPLOSION_RES = reg("explosion_resistant", ExplosionResistanceModifier::new, "Explosion damage taken reduced to %s%% of original, and will not break blocks.");
		DAMAGE_CAP = reg("damage_cap",DamageCapModifier::new, "Damage taken are limited within %s%% of max health.");
		PROJECTILE_REJECT = reg("projectile_reject",ProjectileRejectModifier::new, "Deflect projectiles. Takes no projectile damage.");
		IMMUNITY = reg("immunity",ImmunityModifier::new, "Immune to all damage, except void damage.");
	}

	public static <T extends GolemModifier> RegistryEntry<T> reg(String id, NonNullSupplier<T> sup, String name, String def) {
		Mutable<RegistryEntry<T>> holder = new MutableObject<>();
		var ans = REGISTRATE.generic(GolemTypeRegistry.MODIFIERS, id, sup).defaultLang();
		ans.lang(NamedEntry::getDescriptionId, name);
		ans.addMiscData(ProviderType.LANG, pvd -> pvd.add(holder.getValue().get().getDescriptionId() + ".desc", def));
		var result = ans.register();
		holder.setValue(result);
		return result;
	}

	public static <T extends GolemModifier> RegistryEntry<T> reg(String id, NonNullSupplier<T> sup, String def) {
		Mutable<RegistryEntry<T>> holder = new MutableObject<>();
		var ans = REGISTRATE.generic(GolemTypeRegistry.MODIFIERS, id, sup).defaultLang();
		ans.addMiscData(ProviderType.LANG, pvd -> pvd.add(holder.getValue().get().getDescriptionId() + ".desc", def));
		var result = ans.register();
		holder.setValue(result);
		return result;
	}

	public static <T extends GolemModifier> L2Registrate.GenericBuilder<GolemModifier, T> reg(String id, NonNullSupplier<T> sup) {
		return REGISTRATE.generic(GolemTypeRegistry.MODIFIERS, id, sup).defaultLang();
	}

	public static void register() {

	}

}
