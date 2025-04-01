package dev.xkmc.modulargolems.compat.materials.geoty;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.modifier.base.PotionAttackModifier;
import net.minecraft.world.effect.MobEffectInstance;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class GoetyCompatRegistry {

	public static final RegistryEntry<PotionAttackModifier> BUSTED;
	public static final RegistryEntry<HauntedModifier> HAUNTED;
	public static final RegistryEntry<SoulRepairModifier> SOUL_REPAIR;

	static {
		BUSTED = reg("fallen_attack", () -> new PotionAttackModifier(StatFilterType.ATTACK, 2,
				i -> new MobEffectInstance(GoetyEffects.BUSTED.get(), 100 * i, 0)), null);
		HAUNTED = reg("haunted", HauntedModifier::new,
				"Might summon haunted armor servant when killing enemies. " +
						"Higher chance to summon when killed target is armored");
		SOUL_REPAIR = reg("soul_repair", SoulRepairModifier::new,
				"Repair golem equipments with player's soul energy. " +
						"Also heal golem with soul energy when health is low");
	}

	public static void register() {

	}

}
