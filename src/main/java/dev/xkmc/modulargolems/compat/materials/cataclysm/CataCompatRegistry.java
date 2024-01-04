package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.content.core.StatFilterType;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class CataCompatRegistry {

	public static final RegistryEntry<IgnisFireballModifier> IGNIS_FIREBALL;
	public static final RegistryEntry<IgnisAttackModifier> IGNIS_ATTACK;
	public static final RegistryEntry<HarbingerDeathBeamModifier> HARBINGER_BEAM;
	public static final RegistryEntry<HarbingerHomingMissileModifier> HARBINGER_MISSILE;

	static {
		IGNIS_FIREBALL = reg("ignis_fireball", () -> new IgnisFireballModifier(StatFilterType.HEAD, 2),
				"Shoot Ignis fireballs toward target.");

		IGNIS_ATTACK = reg("ignis_attack", () -> new IgnisAttackModifier(StatFilterType.ATTACK, 2),
				"Stack Blazing Brande effect and regenerate health when hit target. When health is lower than half, direct damage bypasses armor.");

		HARBINGER_BEAM = reg("harbinger_death_beam", () -> new HarbingerDeathBeamModifier(StatFilterType.HEAD, 1),
				"Shoot Death Beam toward target.");

		HARBINGER_MISSILE = reg("harbinger_missile", () -> new HarbingerHomingMissileModifier(StatFilterType.ATTACK, 2),
				"Shoot Homing Missile toward target.");

	}

	public static void register() {

	}

}
