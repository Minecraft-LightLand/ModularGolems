package dev.xkmc.modulargolems.compat.materials.tinker;

import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.xkmc.l2complements.init.L2Complements;
import dev.xkmc.l2complements.init.data.TagGen;
import dev.xkmc.modulargolems.compat.materials.tinker.effect.HepatizonDefenseEffect;
import dev.xkmc.modulargolems.compat.materials.tinker.effect.ManyullynEffect;
import dev.xkmc.modulargolems.compat.materials.tinker.modifier.HepatizonDefenseModifier;
import dev.xkmc.modulargolems.compat.materials.tinker.modifier.ManyullynAttackModifier;
import dev.xkmc.modulargolems.compat.materials.tinker.modifier.ManyullynDefenseModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.fml.ModList;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class TCCompatRegistry {

	public static final RegistryEntry<ManyullynAttackModifier> MANYULLYN_ATTACK;
	public static final RegistryEntry<ManyullynDefenseModifier> MANYULLYN_DEFENSE;
	public static final RegistryEntry<HepatizonDefenseModifier> HEPATIZON_DEFENSE;

	public static final RegistryEntry<ManyullynEffect> EFF_MANYULLYN;
	public static final RegistryEntry<HepatizonDefenseEffect> EFF_HEPATIZON;

	static {
		MANYULLYN_ATTACK = reg("manyullyn_attack", ManyullynAttackModifier::new, "Increase golem attack temporarily after inflicting damage");
		MANYULLYN_DEFENSE = reg("manyullyn_defense", ManyullynDefenseModifier::new, "Increase golem attack temporarily after taking damage");
		HEPATIZON_DEFENSE = reg("hepatizon_defense", HepatizonDefenseModifier::new, "Increase golem armor temporarily after taking damage");

		EFF_MANYULLYN = genEffect("manyullyn_effect", () -> new ManyullynEffect(MobEffectCategory.BENEFICIAL, 0xffffffff),
				"Increase golem attack damage");
		EFF_HEPATIZON = genEffect("hepatizon_effect", () -> new HepatizonDefenseEffect(MobEffectCategory.BENEFICIAL, 0xffffffff),
				"Increase golem armor");

	}

	private static <T extends MobEffect> RegistryEntry<T> genEffect(String name, NonNullSupplier<T> sup, String desc) {
		return ModularGolems.REGISTRATE.effect(name, sup, desc).lang(MobEffect::getDescriptionId).register();
	}

	public static void register() {
		MGTagGen.OPTIONAL_ITEM.add(e -> e.addTag(MGTagGen.SPECIAL_CRAFT)
				.addOptionalTag(TCDispatch.AMETHYST_BRONZE.location())
				.addOptionalTag(TCDispatch.COBALT.location())
				.addOptionalTag(TCDispatch.MANYULLYN.location())
				.addOptionalTag(TCDispatch.HEPATIZON.location())
				.addOptionalTag(TCDispatch.ROSE_GOLD.location())
		);

		if (ModList.get().isLoaded(L2Complements.MODID)) {
			MGTagGen.OPTIONAL_EFF.add(e -> e.addTag(TagGen.SKILL_EFFECT)
					.addOptional(EFF_MANYULLYN.getId())
					.addOptional(EFF_HEPATIZON.getId())
			);
		}
	}

}
