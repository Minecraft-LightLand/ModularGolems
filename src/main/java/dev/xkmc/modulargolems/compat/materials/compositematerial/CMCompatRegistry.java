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
                "Dungeon Absorption", "Reply half of the damage received by the target when attacking directly");
        DUNGEON_LINK = reg("dungeon_link", ()-> new DungeonHealModifier(StatFilterType.HEALTH, 5),
                "Dungeon Link", "Feed 20% back to the player during actual healing without repair");
        OBSIDIAN = reg("obsidian", ObsidianModifier::new,
                "Obsidian", "Reduce the final damage received by 2 for each level");
        PRIMITIVE_BLAST = reg("primitive_blast", ()-> new PrimitiveBlastModifier(StatFilterType.ATTACK, 5),
                "Primitive Blast", "Extra damage of 5% of maximum health added per episode");
        PRIMITIVE_CURSE = reg("primitive_curse", ()-> new PrimitiveCurseModifier(StatFilterType.MASS, 5),
                "Primitive Curse", "Reduce damage by 20% per level, upgrade slot-1");
        PRIMITIVE_SUSTAIN = reg("primitive_sustain", ()-> new PrimitiveSustainModifier(StatFilterType.HEALTH, 1),
                "Primitive Sustain", "Infinite extension of the positive effect time that can be purified, with each such effect halving the health recovery");
        RESONANT_ATTACK = reg("resonant_attack", ()-> new ResonantAttackModifier(StatFilterType.ATTACK, 5),
                "Resonant Attack", "Within an 8-grid range, each level adds an additional 10% of magical damage to the same type of creature, triggered up to once per second");
        RESONANT_HEAL = reg("resonant_heal", ()-> new ResonantHealModifier(StatFilterType.HEALTH, 5),
                "Resonant Heal", "Reply to 5% of the healing amount received by friendly golems with the same entry within the 32 grid range * level of the main entry * level of the opponent's entry");
    }

    public static void register() {
        MGTagGen.OPTIONAL_ITEM.add(e -> e.addTag(MGTagGen.GOLEM_UPGRADES)
                .addOptional(DUNGEON_ABSORPTION.getId())
                .addOptional(DUNGEON_LINK.getId())
                .addOptional(OBSIDIAN.getId())
                .addOptional(PRIMITIVE_BLAST.getId())
                .addOptional(PRIMITIVE_CURSE.getId())
                .addOptional(PRIMITIVE_SUSTAIN.getId())
                .addOptional(RESONANT_ATTACK.getId())
                .addOptional(RESONANT_HEAL.getId()));
    }
}
