package dev.xkmc.modulargolems.compat.materials.compositematerial;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.compositematerial.modifier.*;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import io.github.rcneg.compositematerial.common.init.ItemRegistry;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.multilinereg;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class CMCompatRegistry {
	public static final RegistryEntry<DungeonAttackModifier> DUNGEON_ABSORPTION;
	public static final RegistryEntry<DungeonHealModifier> DUNGEON_LINK;
	public static final RegistryEntry<ObsidianModifier> OBSIDIAN;
	public static final RegistryEntry<PrimitiveBlastModifier> PRIMITIVE_BLAST;
	public static final RegistryEntry<PrimitiveCurseModifier> PRIMITIVE_CURSE;
	public static final RegistryEntry<ResonantAttackModifier> RESONANT_ATTACK;
	public static final RegistryEntry<ResonantHealModifier> RESONANT_HEAL;
	public static final RegistryEntry<EtheriteModifier> ETHERTITE_PLATING;

	static {
		DUNGEON_ABSORPTION = reg("dungeon_absorption", () -> new DungeonAttackModifier(StatFilterType.ATTACK, 4),
				"Dungeon Absorption", "Direct attacks heal for %s%% of damage dealt");
		DUNGEON_LINK = reg("dungeon_link", () -> new DungeonHealModifier(StatFilterType.HEALTH, 5),
				"Dungeon Link", "Non-repair healing effects also restore %s%% of the amount to the player");
		OBSIDIAN = reg("obsidian", ObsidianModifier::new,
				"Obsidian", "Reduces final damage taken by %s");
		PRIMITIVE_BLAST = reg("primitive_blast", () -> new PrimitiveBlastModifier(StatFilterType.ATTACK, 5),
				"Primitive Blast", "Increases damage by %s of max health");
		PRIMITIVE_CURSE = reg("primitive_curse", () -> new PrimitiveCurseModifier(StatFilterType.MASS, 5),
				"Primitive Curse", "Reduces upgrade slots by %2$s and damage taken by %1$s%%");
		RESONANT_ATTACK = reg("resonant_attack", () -> new ResonantAttackModifier(StatFilterType.ATTACK, 5),
				"Resonant Attack", "Golem damage deals additional %2$s%% magic damage to all other targets of same type within %1$s blocks.");
		RESONANT_HEAL = reg("resonant_heal", () -> new ResonantHealModifier(StatFilterType.HEALTH, 5),
				"Resonant Heal", "Non-repair Therapy heals allies with the same trait within %2$s tiles for %1$s%% HP per target's trait level.");
		ETHERTITE_PLATING = multilinereg("etherite_plating", EtheriteModifier::new,
				"Etherite Plating", "According to the level, the following effects are provided:",
				"- Immune to projectile damage",
				"- Immune to conventional debuffs",
				"- Immune to environmental damage",
				"- Periodic self-repair");
	}

	public static void register() {
		MGTagGen.OPTIONAL_ITEM.add(pvd -> pvd.addTag(MGTagGen.LARGE_GOLEM_WEAPONS)
				.addOptional(ItemRegistry.ETHERITE_SWORD.getId())
				.addOptional(ItemRegistry.ETHERITE_SWORD_REINFORCED.getId())
		);
	}

}
