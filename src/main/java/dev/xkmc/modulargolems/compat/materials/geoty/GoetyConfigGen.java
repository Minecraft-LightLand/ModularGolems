package dev.xkmc.modulargolems.compat.materials.geoty;

import com.Polarice3.Goety.common.items.ModItems;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class GoetyConfigGen extends ConfigDataProvider {

	public GoetyConfigGen(DataGenerator generator) {
		super(generator, "Golem Config for Goety");
	}

	@Override
	public void add(Collector map) {
		map.add(ModularGolems.MATERIALS, new ResourceLocation(GoetyDispatch.MODID, GoetyDispatch.MODID), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(GoetyDispatch.MODID, "cursed_metal"),
						Ingredient.of(ModItems.CURSED_METAL_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 100)
				.addStat(GolemTypes.STAT_ATTACK.get(), 15)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.MAGIC_RES.get(), 1)
				.addModifier(GoetyCompatRegistry.BUSTED.get(), 1)
				.end()

				.addMaterial(new ResourceLocation(GoetyDispatch.MODID, "dark_metal"),
						Ingredient.of(ModItems.DARK_METAL_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 160)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addStat(GolemTypes.STAT_REGEN.get(), 4)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.EXPLOSION_RES.get(), 2)
				.addModifier(GolemModifiers.MAGIC_RES.get(), 2)
				.addModifier(GoetyCompatRegistry.HAUNTED.get(), 2)
				.addModifier(GoetyCompatRegistry.SOUL_REPAIR.get(), 1)
				.end()

		);
	}

}
