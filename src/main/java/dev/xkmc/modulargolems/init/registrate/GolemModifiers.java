package dev.xkmc.modulargolems.init.registrate;

import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.l2library.repack.registrate.providers.ProviderType;
import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2library.repack.registrate.util.nullness.NonNullSupplier;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.common.*;
import dev.xkmc.modulargolems.content.modifier.immunes.*;
import dev.xkmc.modulargolems.content.modifier.special.SonicModifier;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemModifiers {

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
	public static final RegistryEntry<SwimModifier> SWIM;
	public static final RegistryEntry<PlayerImmuneModifier> PLAYER_IMMUNE;
	public static final RegistryEntry<SonicModifier> SONIC;
	public static final RegistryEntry<EnderSightModifier> ENDER_SIGHT;
	public static final RegistryEntry<BellModifier> BELL;
	public static final RegistryEntry<AttributeGolemModifier> ARMOR, TOUGH, DAMAGE, REGEN;

	static {
		FIRE_IMMUNE = reg("fire_immune", FireImmuneModifier::new, "Immune to fire damage. Floats in Lava.");
		THUNDER_IMMUNE = reg("thunder_immune", ThunderImmuneModifier::new, "Immune to lightning bolt damage. Attract Lightning. When struck, gain fire resistance for 10 seconds, and heal %s health.");
		MAGIC_IMMUNE = reg("magic_immune", MagicImmuneModifier::new, "Immune to magic damage");
		MAGIC_RES = reg("magic_resistant", MagicResistanceModifier::new, "Magic damage taken reduced to %s%% of original");
		RECYCLE = reg("recycle", RecycleModifier::new, "Drop golem holder of 0 health when killed");
		ARMOR = reg("armor_up", () -> new AttributeGolemModifier(2,
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ARMOR, 10)
		)).register();
		TOUGH = reg("toughness_up", () -> new AttributeGolemModifier(2,
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ARMOR, 10),
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_TOUGH, 6)
		)).register();
		DAMAGE = reg("damage_up", () -> new AttributeGolemModifier(GolemModifier.MAX_LEVEL,
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ATTACK, 2)
		)).register();
		REGEN = reg("regeneration_up", () -> new AttributeGolemModifier(GolemModifier.MAX_LEVEL,
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_REGEN, 1)
		)).register();

		THORN = reg("thorn", ThornModifier::new, "Reflect %s%% damage");
		FLOAT = reg("float", FloatModifier::new, "Golem floats in water and lava instead of sinking");
		EXPLOSION_RES = reg("explosion_resistant", ExplosionResistanceModifier::new, "Explosion damage taken reduced to %s%% of original, and will not break blocks.");
		DAMAGE_CAP = reg("damage_cap", DamageCapModifier::new, "Damage taken are limited within %s%% of max health.");
		PROJECTILE_REJECT = reg("projectile_reject", ProjectileRejectModifier::new, "Deflect projectiles. Takes no projectile damage.");
		IMMUNITY = reg("immunity", ImmunityModifier::new, "Immune to all damage, except void damage.");
		SWIM = reg("swim", SwimModifier::new, "Golem can swim");
		PLAYER_IMMUNE = reg("player_immune", PlayerImmuneModifier::new, "Immune to friendly fire.");
		SONIC = reg("sonic_boom", SonicModifier::new, "Golem can use Sonic Boom Attack. If the golem can perform area attack, then Sonic Boom can hit multiple targets.");
		ENDER_SIGHT = reg("ender_sight", EnderSightModifier::new, "Golem can see through wall and ceilings.");
		BELL = reg("bell", BellModifier::new, "When the golem wants to attack, it will ring its bell, attracting all enemies and light them up.");
	}

	public static <T extends GolemModifier> RegistryEntry<T> reg(String id, NonNullSupplier<T> sup, String name, String def) {
		Mutable<RegistryEntry<T>> holder = new MutableObject<>();
		var ans = REGISTRATE.generic(GolemTypes.MODIFIERS, id, sup).defaultLang();
		ans.lang(NamedEntry::getDescriptionId, name);
		ans.addMiscData(ProviderType.LANG, pvd -> pvd.add(holder.getValue().get().getDescriptionId() + ".desc", def));
		var result = ans.register();
		holder.setValue(result);
		return result;
	}

	public static <T extends GolemModifier> RegistryEntry<T> reg(String id, NonNullSupplier<T> sup, String def) {
		Mutable<RegistryEntry<T>> holder = new MutableObject<>();
		var ans = REGISTRATE.generic(GolemTypes.MODIFIERS, id, sup).defaultLang();
		ans.addMiscData(ProviderType.LANG, pvd -> pvd.add(holder.getValue().get().getDescriptionId() + ".desc", def));
		var result = ans.register();
		holder.setValue(result);
		return result;
	}

	public static <T extends GolemModifier> L2Registrate.GenericBuilder<GolemModifier, T> reg(String id, NonNullSupplier<T> sup) {
		return REGISTRATE.generic(GolemTypes.MODIFIERS, id, sup).defaultLang();
	}

	public static void register() {

	}

}
