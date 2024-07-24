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


    static {
        DIVINE = reg("divine", () -> new TargetBonusModifier(e -> e.getMobType() == MobType.UNDEAD),
                "Deal %s%% more damage to undead mobs");
        POISONOUS = reg("poisonous",
                () -> new PotionAttackModifier(StatFilterType.ATTACK, 5, i -> new MobEffectInstance(MobEffects.POISON, 100, i-1)),
                "The attacked target gains Poison %s for 5 seconds");
    }

}
