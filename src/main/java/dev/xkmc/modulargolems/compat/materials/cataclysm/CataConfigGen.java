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
				.addStat(GolemTypes.STAT_ATTACK.get(), 30)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addStat(GolemTypes.STAT_RANGE.get(), 1)
				.addStat(GolemTypes.STAT_DR.get(), 2)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.DAMAGE_CAP.get(), 3)
				.addModifier(GolemModifiers.DYNAMIC_REDUCTION.get(), 1)
				.addModifier(CataCompatRegistry.IGNIS_FIREBALL.get(), 2)
				.addModifier(CataCompatRegistry.IGNIS_JUMP.get(), 1)
				.addModifier(CataCompatRegistry.IGNIS_ATTACK.get(), 1)
				.end()

				.addMaterial(new ResourceLocation(CataDispatch.MODID, "witherite"), Ingredient.of(ModItems.WITHERITE_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 390)
				.addStat(GolemTypes.STAT_ATTACK.get(), 30)
				.addStat(GolemTypes.STAT_REGEN.get(), 2)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.DAMAGE_CAP.get(), 2)
				.addModifier(GolemModifiers.PROJECTILE_REJECT.get(), 1)
				.addModifier(CataCompatRegistry.HARBINGER_BEAM.get(), 1)
				.addModifier(CataCompatRegistry.HARBINGER_MISSILE.get(), 1)
				.end()

				.addMaterial(new ResourceLocation(CataDispatch.MODID, "cursium"), Ingredient.of(ModItems.CURSIUM_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 390)
				.addStat(GolemTypes.STAT_ATTACK.get(), 40)
				.addStat(GolemTypes.STAT_REGEN.get(), 2)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addStat(GolemTypes.STAT_DR.get(), 2)
				.addModifier(GolemModifiers.DYNAMIC_REDUCTION.get(), 1)
				.addModifier(GolemModifiers.RECYCLE.get(), 1)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.DAMAGE_CAP.get(), 2)
				.addModifier(CataCompatRegistry.EARTHQUAKE_SPEAR.get(), 1)
				.addModifier(CataCompatRegistry.MALEDICTUS_ATTACK.get(), 1)
				.end()

				.addMaterial(new ResourceLocation(CataDispatch.MODID, "ender_guardian"),
						Ingredient.of(CataCompatRegistry.VOID_CONSTRUCT.get()),
						Ingredient.of(CataCompatRegistry.VOID_CUBE.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 333)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.PROJECTILE_REJECT.get(), 1)
				.addModifier(GolemModifiers.DAMAGE_CAP.get(), 3)
				.addModifier(CataCompatRegistry.RUNE.get(), 2)
				.end()

				.addMaterial(new ResourceLocation(CataDispatch.MODID, "storm"),
						Ingredient.of(CataCompatRegistry.STORM_CONSTRUCT.get()),
						Ingredient.of(CataCompatRegistry.AZURE_CUBE.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 390)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addStat(GolemTypes.STAT_REGEN.get(), 2)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addModifier(GolemModifiers.SWIM.get(), 1)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.THUNDER_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.DAMAGE_CAP.get(), 2)
				.addModifier(CataCompatRegistry.SCYLLA_LIGHTNING.get(), 1)
				.addModifier(CataCompatRegistry.SCYLLA_WAVE.get(), 1)
				.end()


				.addMaterial(new ResourceLocation(CataDispatch.MODID, "ancient_metal"), Ingredient.of(ModItems.ANCIENT_METAL_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 288)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(CataCompatRegistry.ANCIENT_MELTDOWN.get(), 1)
				.addModifier(GolemModifiers.DAMAGE_CAP.get(), 1)
				.addModifier(CataCompatRegistry.SANDSTORM.get(), 1)
				.end()
		);


		//
	}
}
