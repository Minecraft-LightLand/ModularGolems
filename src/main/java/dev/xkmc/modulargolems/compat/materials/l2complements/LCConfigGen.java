package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.l2complements.init.materials.LCMats;
import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class LCConfigGen extends ConfigDataProvider {

	public LCConfigGen(DataGenerator generator) {
		super(generator, "Golem Config for L2Complements");
	}


	@Override
	public void add(Collector collector) {
		collector.add(ModularGolems.MATERIALS, ResourceLocation.fromNamespaceAndPath(LCDispatch.MODID, LCDispatch.MODID), new GolemMaterialConfig()
				.addMaterial(ResourceLocation.fromNamespaceAndPath(LCDispatch.MODID, "totemic_gold"), Ingredient.of(LCMats.TOTEMIC_GOLD.getIngot()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 100)
				.addStat(GolemTypes.STAT_ATTACK.get(), 10)
				.addStat(GolemTypes.STAT_WEIGHT.get(), -0.4)
				.addStat(GolemTypes.STAT_REGEN.get(), 10)
				.addModifier(GolemModifiers.RECYCLE.get(), 1)
				.addModifier(LCCompatRegistry.TOTEMIC_GOLD.get(), 1).end()

				.addMaterial(ResourceLocation.fromNamespaceAndPath(LCDispatch.MODID, "poseidite"), Ingredient.of(LCMats.POSEIDITE.getIngot()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 200)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addModifier(GolemModifiers.SWIM.get(), 1)
				.addModifier(LCCompatRegistry.CONDUIT.get(), 1)
				.addModifier(LCCompatRegistry.POSEIDITE.get(), 1).end()

				.addMaterial(ResourceLocation.fromNamespaceAndPath(LCDispatch.MODID, "shulkerate"), Ingredient.of(LCMats.SHULKERATE.getIngot()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 1000)
				.addStat(GolemTypes.STAT_ATTACK.get(), 10)
				.addStat(GolemTypes.STAT_WEIGHT.get(), -0.4)
				.addStat(GolemTypes.STAT_KBRES.get(), 1)
				.addModifier(GolemModifiers.DAMAGE_CAP.get(), 2)
				.addModifier(GolemModifiers.PROJECTILE_REJECT.get(), 1).end()

				.addMaterial(ResourceLocation.fromNamespaceAndPath(LCDispatch.MODID, "eternium"), Ingredient.of(LCMats.ETERNIUM.getIngot()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 1000)
				.addStat(GolemTypes.STAT_ATTACK.get(), 10)
				.addStat(GolemTypes.STAT_REGEN.get(), 1000)
				.addStat(GolemTypes.STAT_KBRES.get(), 1)
				.addModifier(GolemModifiers.IMMUNITY.get(), 1).end()

		);
	}
}
