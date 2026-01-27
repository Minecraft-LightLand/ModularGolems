package dev.xkmc.modulargolems.compat.materials.compositematerial;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.compositematerial.modifier.*;
import dev.xkmc.modulargolems.content.core.StatFilterType;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class CMCompatRegistry {
    public static final RegistryEntry<DungeonAttackModifier> DUNGEON_ABSORPTION;
    public static final RegistryEntry<DungeonHealModifier> DUNGEON_LINK;
    public static final RegistryEntry<ObsidianModifier> OBSIDIAN;
    public static final RegistryEntry<PrimitiveBlastModifier> PRIMITIVE_BLAST;
    public static final RegistryEntry<PrimitiveCurseModifier> PRIMITIVE_CURSE;
    public static final RegistryEntry<ResonantAttackModifier> RESONANT_ATTACK;
    public static final RegistryEntry<ResonantHealModifier> RESONANT_HEAL;

    static {
        DUNGEON_ABSORPTION = reg("dungeon_absorption", ()-> new DungeonAttackModifier(StatFilterType.ATTACK, 4),
                "Dungeon Absorption", "Direct attacks heal for %s%% of damage dealt");
        DUNGEON_LINK = reg("dungeon_link", ()-> new DungeonHealModifier(StatFilterType.HEALTH, 5),
                "Dungeon Link", "Non-repair healing effects also restore %s%% of the amount to the player");
        OBSIDIAN = reg("obsidian", ObsidianModifier::new,
                "Obsidian", "Reduces final damage taken by %s");
        PRIMITIVE_BLAST = reg("primitive_blast", ()-> new PrimitiveBlastModifier(StatFilterType.ATTACK, 5),
                "Primitive Blast", "Increases damage by %s of max health");
        PRIMITIVE_CURSE = reg("primitive_curse", ()-> new PrimitiveCurseModifier(StatFilterType.MASS, 5),
                "Primitive Curse", "Reduces upgrade slots by %2$s and damage taken by %1$s%%");
        RESONANT_ATTACK = reg("resonant_attack", ()-> new ResonantAttackModifier(StatFilterType.ATTACK, 5),
                "Resonant Attack", "Deals chain magic damage to all same-type creatures within %1$s blocks, increasing the damage by %2$s%%. Cooldown: %3$s seconds.");
        RESONANT_HEAL = reg("resonant_heal", ()-> new ResonantHealModifier(StatFilterType.HEALTH, 5),
                "Resonant Heal", "Non-repair Therapy heals allies with the same trait within %2$s tiles for %1$s%% HP per target's trait level.");
    }

    public static void register() {
    }
}
