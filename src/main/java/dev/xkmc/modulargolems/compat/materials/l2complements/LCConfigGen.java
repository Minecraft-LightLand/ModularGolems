package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.l2complements.init.data.LCMats;
import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.registrate.GolemModifierRegistry;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
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
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 100)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 10)
				.addStat(GolemTypeRegistry.STAT_WEIGHT.get(), -0.4)
				.addStat(GolemTypeRegistry.STAT_REGEN.get(), 10)
				.addModifier(GolemModifierRegistry.RECYCLE.get(), 1).end()

				.addMaterial(new ResourceLocation(LCDispatch.MODID, "poseidite"), Ingredient.of(LCMats.POSEIDITE.getIngot()))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 200)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 20).end()

				.addMaterial(new ResourceLocation(LCDispatch.MODID, "shulkerate"), Ingredient.of(LCMats.SHULKERATE.getIngot()))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 1000)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 10)
				.addStat(GolemTypeRegistry.STAT_WEIGHT.get(), -0.4)
				.addModifier(GolemModifierRegistry.DAMAGE_CAP.get(), 2)
				.addModifier(GolemModifierRegistry.PROJECTILE_REJECT.get(), 1).end()

				.addMaterial(new ResourceLocation(LCDispatch.MODID, "eternium"), Ingredient.of(LCMats.ETERNIUM.getIngot()))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 1000)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 10)
				.addStat(GolemTypeRegistry.STAT_REGEN.get(), 1000)
				.addModifier(GolemModifierRegistry.IMMUNITY.get(), 1).end()

		);
	}

}
