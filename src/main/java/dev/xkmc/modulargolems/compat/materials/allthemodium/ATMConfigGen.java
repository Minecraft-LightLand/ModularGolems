package dev.xkmc.modulargolems.compat.materials.allthemodium;

import com.thevortex.allthemodium.registry.ModRegistry;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class ATMConfigGen extends ConfigDataProvider {

	public ATMConfigGen(DataGenerator generator) {
		super(generator, "Golem Config for ATM");
	}

	@Override
	public void add(Collector map) {
		map.add(ModularGolems.MATERIALS, new ResourceLocation(ATMDispatch.MODID, ATMDispatch.MODID), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(ATMDispatch.MODID, "allthemodium"),
						Ingredient.of(ModRegistry.ALLTHEMODIUM_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 500)
				.addStat(GolemTypes.STAT_ATTACK.get(), 50)
				.addStat(GolemTypes.STAT_WEIGHT.get(), 0.2)
				.addStat(GolemTypes.STAT_REGEN.get(), 5)
				.addStat(GolemTypes.STAT_SWEEP.get(), 2)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.PLAYER_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.RECYCLE.get(), 1)
				.addModifier(GolemModifiers.ADD_SLOT.get(), 1)
				.addModifier(GolemModifiers.ARMOR_BYPASS.get(), 1)
				.end()

				.addMaterial(new ResourceLocation(ATMDispatch.MODID, "vibranium"),
						Ingredient.of(ModRegistry.VIBRANIUM_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 800)
				.addStat(GolemTypes.STAT_ATTACK.get(), 80)
				.addStat(GolemTypes.STAT_WEIGHT.get(), 0.4)
				.addStat(GolemTypes.STAT_REGEN.get(), 8)
				.addStat(GolemTypes.STAT_SWEEP.get(), 3)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.PLAYER_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.PROJECTILE_REJECT.get(), 1)
				.addModifier(GolemModifiers.EXPLOSION_RES.get(), 2)
				.addModifier(GolemModifiers.RECYCLE.get(), 1)
				.addModifier(GolemModifiers.ADD_SLOT.get(), 2)
				.addModifier(GolemModifiers.ARMOR_BYPASS.get(), 2)
				.end()

				.addMaterial(new ResourceLocation(ATMDispatch.MODID, "unobtainium"),
						Ingredient.of(ModRegistry.UNOBTAINIUM_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 1000)
				.addStat(GolemTypes.STAT_ATTACK.get(), 100)
				.addStat(GolemTypes.STAT_WEIGHT.get(), 0.6)
				.addStat(GolemTypes.STAT_REGEN.get(), 10)
				.addStat(GolemTypes.STAT_SWEEP.get(), 4)
				.addStat(GolemTypes.STAT_RANGE.get(), 1)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.PLAYER_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.PROJECTILE_REJECT.get(), 1)
				.addModifier(GolemModifiers.MAGIC_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.EXPLOSION_RES.get(), 2)
				.addModifier(GolemModifiers.RECYCLE.get(), 1)
				.addModifier(GolemModifiers.ADD_SLOT.get(), 3)
				.addModifier(GolemModifiers.ARMOR_BYPASS.get(), 3)
				.end()

		);
	}

}
