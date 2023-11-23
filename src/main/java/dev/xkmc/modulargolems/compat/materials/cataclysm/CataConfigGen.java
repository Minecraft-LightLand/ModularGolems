package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.init.ModItems;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class CataConfigGen extends ConfigDataProvider {

	public CataConfigGen(DataGenerator generator) {
		super(generator, "Golem Config for Cataclysm");
	}

	@Override
	public void add(Collector collector) {
		collector.add(ModularGolems.MATERIALS, new ResourceLocation(CataDispatch.MODID, CataDispatch.MODID), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(CataDispatch.MODID, "ignitium"), Ingredient.of(ModItems.IGNITIUM_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 450)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addStat(GolemTypes.STAT_RANGE.get(), 1)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(CataCompatRegistry.IGNIS_FIREBALL.get(), 2)
				.addModifier(CataCompatRegistry.IGNIS_ATTACK.get(), 1)
				.end()

				.addMaterial(new ResourceLocation(CataDispatch.MODID, "witherite"), Ingredient.of(ModItems.WITHERITE_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 390)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addStat(GolemTypes.STAT_REGEN.get(), 2)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.PROJECTILE_REJECT.get(), 1)
				.addModifier(CataCompatRegistry.HARBINGER_BEAM.get(), 1)
				.addModifier(CataCompatRegistry.HARBINGER_MISSILE.get(), 1)
				.end()
		);
	}
}
