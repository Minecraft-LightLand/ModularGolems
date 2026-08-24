package dev.xkmc.modulargolems.compat.materials.tinker;

import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
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

		collector.add(ModularGolems.MATERIALS, new ResourceLocation(TCDispatch.MODID, "amethyst_bronze"), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(TCDispatch.MODID, "amethyst_bronze"), Ingredient.of(TCDispatch.AMETHYST_BRONZE))
				.addStat(GolemTypes.STAT_HEALTH.get(), 200)
				.addStat(GolemTypes.STAT_ATTACK.get(), 15)
				.end()
		);
		collector.add(ModularGolems.MATERIALS, new ResourceLocation(TCDispatch.MODID, "manyullyn"), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(TCDispatch.MODID, "manyullyn"), Ingredient.of(TCDispatch.MANYULLYN))
				.addStat(GolemTypes.STAT_HEALTH.get(), 300)
				.addStat(GolemTypes.STAT_ATTACK.get(), 35)
				.addModifier(TCCompatRegistry.MANYULLYN_ATTACK.get(), 1)
				.addModifier(TCCompatRegistry.MANYULLYN_DEFENSE.get(), 1)
				.end()
		);
		collector.add(ModularGolems.MATERIALS, new ResourceLocation(TCDispatch.MODID, "hepatizon"), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(TCDispatch.MODID, "hepatizon"), Ingredient.of(TCDispatch.HEPATIZON))
				.addStat(GolemTypes.STAT_HEALTH.get(), 250)
				.addStat(GolemTypes.STAT_ATTACK.get(), 15)
				.addModifier(TCCompatRegistry.HEPATIZON_DEFENSE.get(), 1)
				.end()
		);
		collector.add(ModularGolems.MATERIALS, new ResourceLocation(TCDispatch.MODID, "cobalt"), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(TCDispatch.MODID, "cobalt"), Ingredient.of(TCDispatch.COBALT))
				.addStat(GolemTypes.STAT_HEALTH.get(), 220)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addStat(GolemTypes.STAT_SPEED.get(), 0.3)
				.end()
		);
		collector.add(ModularGolems.MATERIALS, new ResourceLocation(TCDispatch.MODID, "rose_gold"), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(TCDispatch.MODID, "rose_gold"), Ingredient.of(TCDispatch.ROSE_GOLD))
				.addStat(GolemTypes.STAT_HEALTH.get(), 40)
				.addStat(GolemTypes.STAT_ATTACK.get(), 10)
				.addModifier(GolemModifiers.ADD_SLOT.get(), 1)
				.end()
		);
	}
}
