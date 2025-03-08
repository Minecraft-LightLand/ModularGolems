package dev.xkmc.modulargolems.compat.materials.tinker;

import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class TinkerConfigGen extends ConfigDataProvider {

	public TinkerConfigGen(DataGenerator generator) {
		super(generator, "Golem config for Tinker Construct");
	}

	@Override
	public void add(Collector collector) {

		collector.add(ModularGolems.MATERIALS, new ResourceLocation(TCDispatch.MODID, TCDispatch.MODID), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(TCDispatch.MODID, "amethyst_bronze"), Ingredient.of(TCDispatch.AMETHYST_BRONZE))
				.addStat(GolemTypes.STAT_HEALTH.get(), 160)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.end()

				.addMaterial(new ResourceLocation(TCDispatch.MODID, "manyullyn"), Ingredient.of(TCDispatch.MANYULLYN))
				.addStat(GolemTypes.STAT_HEALTH.get(), 200)
				.addStat(GolemTypes.STAT_ATTACK.get(), 30)
				.end()

				.addMaterial(new ResourceLocation(TCDispatch.MODID, "hepatizon"), Ingredient.of(TCDispatch.HEPATIZON))
				.addStat(GolemTypes.STAT_HEALTH.get(), 200)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.end()

				.addMaterial(new ResourceLocation(TCDispatch.MODID, "cobalt"), Ingredient.of(TCDispatch.COBALT))
				.addStat(GolemTypes.STAT_HEALTH.get(), 200)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.end()
		);
	}
}
