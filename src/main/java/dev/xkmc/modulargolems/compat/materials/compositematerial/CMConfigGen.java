package dev.xkmc.modulargolems.compat.materials.compositematerial;

import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import io.github.rcneg.compositematerial.common.init.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class CMConfigGen extends ConfigDataProvider {

	public CMConfigGen(DataGenerator generator) {
		super(generator, "Golem Config for CompositeMaterial");
	}

	@Override
	public void add(ConfigDataProvider.Collector map) {
		map.add(ModularGolems.MATERIALS, new ResourceLocation(CMDispatch.MODID, CMDispatch.MODID), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(CMDispatch.MODID, "allay_steel"),
						Ingredient.of(ItemRegistry.ALLAY_STEEL_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 240)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addStat(GolemTypes.STAT_WEIGHT.get(), 0.4)
				.addStat(GolemTypes.STAT_REGEN.get(), 2)
                .addModifier(CMCompatRegistry.RESONANT_ATTACK.get(), 1)
                .addModifier(CMCompatRegistry.RESONANT_HEAL.get(), 1)
				.end()

				.addMaterial(new ResourceLocation(CMDispatch.MODID, "dungeon_steel"),
						Ingredient.of(ItemRegistry.DUNGEON_STEEL_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 200)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
                .addModifier(CMCompatRegistry.DUNGEON_ABSORPTION.get(), 1)
                .addModifier(CMCompatRegistry.DUNGEON_LINK.get(), 1)
				.end()

				.addMaterial(new ResourceLocation(CMDispatch.MODID, "etherite"),
						Ingredient.of(ItemRegistry.ETHERITE_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 500)
				.addStat(GolemTypes.STAT_ATTACK.get(), 35)
				.addStat(GolemTypes.STAT_WEIGHT.get(), 0.2)
				.addStat(GolemTypes.STAT_REGEN.get(), 4)
				.addStat(GolemTypes.STAT_SWEEP.get(), 1)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.ARMOR_BYPASS.get(), 1)
				.addModifier(GolemModifiers.RECYCLE.get(), 1)
				.addModifier(CMCompatRegistry.ETHERTITE_PLATING.get(),1)
				.end()

				.addMaterial(new ResourceLocation(CMDispatch.MODID, "primitive"),
						Ingredient.of(ItemRegistry.PRIMITIVE_TENACITY.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 500)
				.addStat(GolemTypes.STAT_ATTACK.get(), 5)
				.addStat(GolemTypes.STAT_WEIGHT.get(), -0.2)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.SWIM.get(), 1)
                .addModifier(GolemModifiers.DAMAGE_CAP.get(), 1)
				.addModifier(CMCompatRegistry.PRIMITIVE_BLAST.get(), 1)
                .addModifier(CMCompatRegistry.PRIMITIVE_CURSE.get(), 1)
				.end()

                .addMaterial(new ResourceLocation(CMDispatch.MODID, "obsidian_steel"),
                        Ingredient.of(ItemRegistry.OBSIDIAN_STEEL_INGOT.get()))
                .addStat(GolemTypes.STAT_HEALTH.get(), 150)
                .addStat(GolemTypes.STAT_ATTACK.get(), 15)
                .addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
                .addModifier(GolemModifiers.EXPLOSION_RES.get(), 2)
                .addModifier(CMCompatRegistry.OBSIDIAN.get(), 1)
                .end()

		);
	}
}