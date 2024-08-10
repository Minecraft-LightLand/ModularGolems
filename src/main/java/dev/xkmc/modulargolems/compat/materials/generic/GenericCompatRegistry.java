package dev.xkmc.modulargolems.compat.materials.generic;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.modifier.base.PotionAttackModifier;
import dev.xkmc.modulargolems.content.modifier.base.TargetBonusModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobType;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class GenericCompatRegistry {
    public static final RegistryEntry<TargetBonusModifier> DIVINE;
    public static final RegistryEntry<PotionAttackModifier> POISONOUS;
    public static final RegistryEntry<TinPlagueModifier> TIN_PLAGUE;
    public static final RegistryEntry<WeidingModifier> WEIDING;

    static {
        DIVINE = reg("divine", () -> new TargetBonusModifier(e -> e.getMobType() == MobType.UNDEAD),
                "Deal %s%% more damage to undead mobs");
        POISONOUS = reg("poisonous",
                () -> new PotionAttackModifier(StatFilterType.MASS, 5, i -> new MobEffectInstance(MobEffects.POISON, 100, i - 1)),
                "Inflict %s to enemies attacking or attacked");
        TIN_PLAGUE = reg("tin_plague", TinPlagueModifier::new,
                "Takes +%s%% damage in cold areas");
        WEIDING = reg("weiding", WeidingModifier::new,
                "Inflict %s to enemies attacking or attacked when the golem is on fired");
    }

    public static void register() {

    }
}
