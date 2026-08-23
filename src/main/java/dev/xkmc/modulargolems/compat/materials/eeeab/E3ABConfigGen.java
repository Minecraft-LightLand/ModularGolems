package dev.xkmc.modulargolems.compat.materials.eeeab;

import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.miauczel.legendary_monsters.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class E3ABConfigGen extends ConfigDataProvider {

	public E3ABConfigGen(DataGenerator generator) {
		super(generator, "Golem Config for Legendary Monsters");
	}

	@Override
	public void add(Collector map) {
		map.add(ModularGolems.MATERIALS, new ResourceLocation(E3ABDispatch.MODID, E3ABDispatch.MODID), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(E3ABDispatch.MODID, "molten_metal"),
						Ingredient.of(ModItems.MOLTEN_METAL_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 200)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addStat(GolemTypes.STAT_ATKKB.get(), 1)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.PROJECTILE_REJECT.get(), 1)
				.addModifier(GolemModifiers.ARMOR_BYPASS.get(), 2)
				.end()

		);
	}

}
