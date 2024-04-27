package dev.xkmc.modulargolems.compat.materials.botania;

import com.tterrag.registrate.util.entry.RegistryEntry;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class BotCompatRegistry {
    public static final RegistryEntry<ManaMendingModifier> MANA_MENDING;
    public static final RegistryEntry<ManaBoostModifier> MANA_BOOSTING;

    static {
        MANA_MENDING = reg("mana_mending", ManaMendingModifier::new, "Get %s regeneration at a %s mana/hp efficiency");
        MANA_BOOSTING = reg("mana_boosting", ManaBoostModifier::new, "Deal %s%% extra damage costing %s mana");
    }

    public static void register() {
    }

    }
