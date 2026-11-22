package dev.xkmc.modulargolems.compat.materials.iceandfire;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.iceandfire.proxy.IAFProxy;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class IAFConfigGen extends ConfigDataProvider {

	public IAFConfigGen(DataGenerator generator, CompletableFuture<HolderLookup.Provider> pvd) {
		super(generator, pvd, "Golem Config for Ice and Fire");
	}

	@Override
	public void add(Collector collector) {
		collector.add(ModularGolems.MATERIALS, ResourceLocation.fromNamespaceAndPath(IAFDispatch.MODID, "fire_dragonsteel"), new GolemMaterialConfig()
				.addMaterial(ResourceLocation.fromNamespaceAndPath(IAFDispatch.MODID, "fire_dragonsteel"), Ingredient.of(IAFProxy.get().ingotFireSteel().get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 400)
				.addStat(GolemTypes.STAT_ATTACK.get(), 40)
				.addModifier(IAFCompatRegistry.FIRE_ATK.get(), 1)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.end()
		);
		collector.add(ModularGolems.MATERIALS, ResourceLocation.fromNamespaceAndPath(IAFDispatch.MODID, "ice_dragonsteel"), new GolemMaterialConfig()
				.addMaterial(ResourceLocation.fromNamespaceAndPath(IAFDispatch.MODID, "ice_dragonsteel"), Ingredient.of(IAFProxy.get().ingotIceSteel().get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 400)
				.addStat(GolemTypes.STAT_ATTACK.get(), 40)
				.addModifier(IAFCompatRegistry.ICE_ATK.get(), 1)
				.addModifier(IAFCompatRegistry.ICE_DEF.get(), 1)
				.end()
		);
		collector.add(ModularGolems.MATERIALS, ResourceLocation.fromNamespaceAndPath(IAFDispatch.MODID, "lightning_dragonsteel"), new GolemMaterialConfig()
				.addMaterial(ResourceLocation.fromNamespaceAndPath(IAFDispatch.MODID, "lightning_dragonsteel"), Ingredient.of(IAFProxy.get().ingotLightningSteel().get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 400)
				.addStat(GolemTypes.STAT_ATTACK.get(), 40)
				.addModifier(IAFCompatRegistry.LIGHTNING_ATK.get(), 1)
				.addModifier(GolemModifiers.THUNDER_IMMUNE.get(), 1)
				.end()
		);
	}

}
