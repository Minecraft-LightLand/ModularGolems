package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.miauczel.legendary_monsters.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class LMConfigGen extends ConfigDataProvider {

	public LMConfigGen(DataGenerator generator) {
		super(generator, "Golem Config for Legendary Monsters");
	}

	@Override
	public void add(Collector map) {
		map.add(ModularGolems.MATERIALS, new ResourceLocation(LMDispatch.MODID, "molten_metal"), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(LMDispatch.MODID, "molten_metal"),
						Ingredient.of(ModItems.MOLTEN_METAL_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 200)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addStat(GolemTypes.STAT_ATKKB.get(), 1)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.PROJECTILE_REJECT.get(), 1)
				.addModifier(GolemModifiers.ARMOR_BYPASS.get(), 2)
				.addModifier(LMCompatRegistry.ANCHOR.get(), 1)
				.end()
		);
		map.add(ModularGolems.MATERIALS, new ResourceLocation(LMDispatch.MODID, "cloud"), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(LMDispatch.MODID, "cloud"),
						Ingredient.of(LMCompatRegistry.CLOUD_CUBE.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 600)
				.addStat(GolemTypes.STAT_ATTACK.get(), 40)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addStat(GolemTypes.STAT_ATKKB.get(), 1)
				.addStat(GolemTypes.STAT_DR.get(), 2)
				.addModifier(GolemModifiers.DYNAMIC_REDUCTION.get(), 1)
				.addModifier(GolemModifiers.ADD_SLOT.get(), 1)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.DAMAGE_CAP.get(), 2)
				.addModifier(LMCompatRegistry.THUNDER.get(), 1)
				.addModifier(LMCompatRegistry.PERC.get(), 1)
				.end()
		);
		map.add(ModularGolems.MATERIALS, new ResourceLocation(LMDispatch.MODID, "obliterator"), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(LMDispatch.MODID, "obliterator"),
						Ingredient.of(LMCompatRegistry.ANNIHILATION_CUBE.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 600)
				.addStat(GolemTypes.STAT_ATTACK.get(), 40)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addStat(GolemTypes.STAT_ATKKB.get(), 1)
				.addStat(GolemTypes.STAT_DR.get(), 2)
				.addModifier(GolemModifiers.DYNAMIC_REDUCTION.get(), 1)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.DAMAGE_CAP.get(), 2)
				.addModifier(LMCompatRegistry.OBLITERATOR_LASER.get(), 1)
				.addModifier(LMCompatRegistry.OBLITERATOR_PLASMA_ORB.get(), 1)
				.addModifier(LMCompatRegistry.OBLITERATOR_LARGE_BOMB.get(), 2)
				.addModifier(LMCompatRegistry.OBLITERATOR_SMALL_BOMB.get(), 2)
				.addModifier(LMCompatRegistry.OBLITERATOR_JUMP.get(), 1)
				.addModifier(LMCompatRegistry.OBLITERATOR_ULTIMATE.get(), 1)
				.end()
		);
		map.add(ModularGolems.MATERIALS, new ResourceLocation(LMDispatch.MODID, "paladin"), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(LMDispatch.MODID, "paladin"),
						Ingredient.of(LMCompatRegistry.POSESSED_SOUL_CUBE.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 400)
				.addStat(GolemTypes.STAT_ATTACK.get(), 40)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addStat(GolemTypes.STAT_ATKKB.get(), 1)
				.addStat(GolemTypes.STAT_DR.get(), 2)
				.addModifier(GolemModifiers.DYNAMIC_REDUCTION.get(), 1)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.DAMAGE_CAP.get(), 2)
				.addModifier(LMCompatRegistry.PHANTOM_DAGGER.get(), 1)
				.addModifier(LMCompatRegistry.SOUL_SPIKE.get(), 1)
				.addModifier(LMCompatRegistry.PALADIN_SOUL_BLADE_LEAP.get(), 1)
				.end()
		);
	}

}
