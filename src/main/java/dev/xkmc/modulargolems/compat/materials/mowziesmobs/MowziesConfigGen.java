package dev.xkmc.modulargolems.compat.materials.mowziesmobs;

import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class MowziesConfigGen extends ConfigDataProvider {

	public MowziesConfigGen(DataGenerator generator) {
		super(generator, "Golem Config for Mowzie's Mobs");
	}

	@Override
	public void add(Collector map) {
		map.add(ModularGolems.MATERIALS, new ResourceLocation(MowzieDispatch.MODID, "wroughtnaut"), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(MowzieDispatch.MODID, "wroughtnaut"),
						Ingredient.of(MowzieCompatRegistry.WROUGHTNAUT_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 360)
				.addStat(GolemTypes.STAT_ATTACK.get(), 25)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.MAGIC_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.PROJECTILE_REJECT.get(), 1)
				.addModifier(GolemModifiers.ARMOR_BYPASS.get(), 1)
				.addModifier(MowzieCompatRegistry.AXE_SLAM.get(), 1)
				.end()

		);
	}

}
