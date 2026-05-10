package dev.xkmc.modulargolems.compat.materials.l2hostility;

import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class LHConfigGen extends ConfigDataProvider {

	public LHConfigGen(DataGenerator generator, CompletableFuture<HolderLookup.Provider> pvd) {
		super(generator, pvd, "L2Hostility config provider");
	}

	@Override
	public void add(Collector collector) {

		collector.add(ModularGolems.MATERIALS, Identifier.fromNamespaceAndPath(LHDispatch.MODID, LHDispatch.MODID), new GolemMaterialConfig()
				.addMaterial(Identifier.fromNamespaceAndPath(LHDispatch.MODID, "chaotic"), Ingredient.of(LHItems.CHAOS_INGOT))
				.addStat(GolemTypes.STAT_HEALTH.get(), 400)
				.addStat(GolemTypes.STAT_ATTACK.get(), 25)
				.addStat(GolemTypes.STAT_REGEN.get(), 5)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addModifier(GolemModifiers.ADD_SLOT.get(), 1)
				.addModifier(GolemModifiers.PLAYER_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.ARMOR_BYPASS.get(), 1)
				.addModifier(LHCompatRegistry.LH_CORE.get(), 1)
				.end()

				.addMaterial(Identifier.fromNamespaceAndPath(LHDispatch.MODID, "miraculous"), Ingredient.of(LHItems.MIRACLE_INGOT))
				.addStat(GolemTypes.STAT_HEALTH.get(), 600)
				.addStat(GolemTypes.STAT_ATTACK.get(), 35)
				.addStat(GolemTypes.STAT_REGEN.get(), 10)
				.addStat(GolemTypes.STAT_SWEEP.get(), 3)
				.addModifier(GolemModifiers.ADD_SLOT.get(), 2)
				.addModifier(GolemModifiers.PLAYER_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.ARMOR_BYPASS.get(), 2)
				.addModifier(LHCompatRegistry.LH_CORE.get(), 1)
				.addModifier(LHCompatRegistry.LH_ADAPTIVE.get(), 1)
				.addModifier(LHCompatRegistry.LH_DISPELL.get(), 1)
				.end()

		);

	}

}
