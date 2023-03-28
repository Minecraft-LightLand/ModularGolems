package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.l2complements.init.materials.LCMats;
import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;

public class LCConfigGen extends ConfigDataProvider {

	public LCConfigGen(DataGenerator generator) {
		super(generator, "data/" + LCDispatch.MODID + "/golem_config/", "Golem Config for L2Complements");
	}

	public void add(Map<String, BaseConfig> map) {
		map.put("materials/" + LCDispatch.MODID, new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(LCDispatch.MODID, "totemic_gold"), Ingredient.of(LCMats.TOTEMIC_GOLD.getIngot()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 100)
				.addStat(GolemTypes.STAT_ATTACK.get(), 10)
				.addStat(GolemTypes.STAT_WEIGHT.get(), -0.4)
				.addStat(GolemTypes.STAT_REGEN.get(), 10)
				.addModifier(GolemModifiers.RECYCLE.get(), 1).end()

				.addMaterial(new ResourceLocation(LCDispatch.MODID, "poseidite"), Ingredient.of(LCMats.POSEIDITE.getIngot()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 200)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addModifier(GolemModifiers.SWIM.get(), 1)
				.addModifier(LCCompatRegistry.CONDUIT.get(), 1).end()

				.addMaterial(new ResourceLocation(LCDispatch.MODID, "shulkerate"), Ingredient.of(LCMats.SHULKERATE.getIngot()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 1000)
				.addStat(GolemTypes.STAT_ATTACK.get(), 10)
				.addStat(GolemTypes.STAT_WEIGHT.get(), -0.4)
				.addModifier(GolemModifiers.DAMAGE_CAP.get(), 2)
				.addModifier(GolemModifiers.PROJECTILE_REJECT.get(), 1).end()

				.addMaterial(new ResourceLocation(LCDispatch.MODID, "eternium"), Ingredient.of(LCMats.ETERNIUM.getIngot()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 1000)
				.addStat(GolemTypes.STAT_ATTACK.get(), 10)
				.addStat(GolemTypes.STAT_REGEN.get(), 1000)
				.addModifier(GolemModifiers.IMMUNITY.get(), 1).end()

		);
	}

}
