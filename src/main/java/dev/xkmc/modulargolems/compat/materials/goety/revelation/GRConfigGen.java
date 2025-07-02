package dev.xkmc.modulargolems.compat.materials.goety.revelation;

import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.goety.GoetyCompatRegistry;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class GRConfigGen extends ConfigDataProvider {

	public GRConfigGen(DataGenerator generator) {
		super(generator, "Golem Config for Goety Relevation");
	}

	@Override
	public void add(Collector map) {
		map.add(ModularGolems.MATERIALS, new ResourceLocation(GRDispatch.MODID, GRDispatch.MODID), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(GRDispatch.MODID, "apocalyptium"),
						Ingredient.of(GoetyCompatRegistry.REV_INGOT))
				.addStat(GolemTypes.STAT_HEALTH.get(), 666)
				.addStat(GolemTypes.STAT_ATTACK.get(), 33)
				.addStat(GolemTypes.STAT_REGEN.get(), 6)
				.addStat(GolemTypes.STAT_SPEED.get(), 0.66)
				.addStat(GolemTypes.STAT_RANGE.get(), 1)
				.addStat(GolemTypes.STAT_SWEEP.get(), 3)
				.addModifier(GolemModifiers.ADD_SLOT.get(), 2)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.MAGIC_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.PROJECTILE_REJECT.get(), 1)
				.addModifier(GolemModifiers.EXPLOSION_RES.get(), 3)
				.addModifier(GoetyCompatRegistry.SOUL_REPAIR.get(), 2)
				.addModifier(GoetyCompatRegistry.APOSTLE.get(), 1)
				.addModifier(GRCompatRegistry.REVIVE.get(), 1)
				.addModifier(GRCompatRegistry.CD_BYPASS.get(), 1)
				.addModifier(GRCompatRegistry.BOW.get(), 2)
				.addModifier(GoetyCompatRegistry.SOUL.get(), 2)
				.end()

		);
	}

}
