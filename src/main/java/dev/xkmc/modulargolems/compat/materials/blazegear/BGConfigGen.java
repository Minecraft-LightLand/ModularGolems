package dev.xkmc.modulargolems.compat.materials.blazegear;

import com.flashfyre.blazegear.registry.BGItems;
import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;

public class BGConfigGen extends ConfigDataProvider {

	public BGConfigGen(DataGenerator generator) {
		super(generator, "data/" + BGDispatch.MODID + "/golem_config/", "Golem Config for BlazeGear");
	}

	public void add(Map<String, BaseConfig> map) {
		map.put("materials/" + BGDispatch.MODID, new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(BGDispatch.MODID, "brimsteel"), Ingredient.of(BGItems.BRIMSTEEL_INGOT.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 100)
				.addStat(GolemTypes.STAT_ATTACK.get(), 15)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(BGCompatRegistry.BLAZING.get(), 1).end()


		);
	}

}
