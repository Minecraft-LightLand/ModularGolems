package dev.xkmc.modulargolems.compat.materials.compositematerial;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.compositematerial.modifier.*;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.init.data.MGTagGen;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class CMCompatRegistry {
    public static final RegistryEntry<DungeonAttackModifier> DUNGEON_ABSORPTION;
    public static final RegistryEntry<DungeonHealModifier> DUNGEON_LINK;
    public static final RegistryEntry<ObsidianModifier> OBSIDIAN;
    public static final RegistryEntry<PrimitiveBlastModifier> PRIMITIVE_BLAST;
    public static final RegistryEntry<PrimitiveCurseModifier> PRIMITIVE_CURSE;
    public static final RegistryEntry<PrimitiveSustainModifier> PRIMITIVE_SUSTAIN;
    public static final RegistryEntry<ResonantAttackModifier> RESONANT_ATTACK;
    public static final RegistryEntry<ResonantHealModifier> RESONANT_HEAL;

    static {
        DUNGEON_ABSORPTION = reg("dungeon_absorption", ()-> new DungeonAttackModifier(StatFilterType.ATTACK, 4),
                "Dungeon Absorption", "Direct attacks heal for 25% of damage dealt");
        DUNGEON_LINK = reg("dungeon_link", ()-> new DungeonHealModifier(StatFilterType.HEALTH, 5),
                "Dungeon Link", "Non-repair healing effects also restore 20% of the amount to the player");
        OBSIDIAN = reg("obsidian", ObsidianModifier::new,
                "Obsidian", "Reduces final damage taken by 2 per level");
        PRIMITIVE_BLAST = reg("primitive_blast", ()-> new PrimitiveBlastModifier(StatFilterType.ATTACK, 5),
                "Primitive Blast", "Increases damage by 5% of max health per level");
        PRIMITIVE_CURSE = reg("primitive_curse", ()-> new PrimitiveCurseModifier(StatFilterType.MASS, 5),
                "Primitive Curse", "Reduces upgrade slots by 1 and damage taken by 20% per level.");
        PRIMITIVE_SUSTAIN = reg("primitive_sustain", ()-> new PrimitiveSustainModifier(StatFilterType.HEALTH, 1),
                "Primitive Sustain", "Infinite extension of the positive effect time that can be purified, with each such effect halving the health recovery");
        RESONANT_ATTACK = reg("resonant_attack", ()-> new ResonantAttackModifier(StatFilterType.ATTACK, 5),
                "Resonant Attack", "When dealing damage, chains magic damage to all same-type creatures within 8 blocks. Damage increases by 10% per level. 1 second cooldown.");
        RESONANT_HEAL = reg("resonant_heal", ()-> new ResonantHealModifier(StatFilterType.HEALTH, 5),
                "Resonant Heal", "Non-repair healing also heals allied golems with the same modifier within 32 blocks for 25% of the heal amount multiplied by both your modifier level and their level.");
    }

    public static void register() {
    }
}
