package dev.xkmc.modulargolems.init.data;

import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class ConfigGen extends ConfigDataProvider {

	public ConfigGen(DataGenerator generator) {
		super(generator, "data/", "Golem Config");
	}

	@Override
	public void add(Map<String, BaseConfig> map) {
		map.put("vanilla", new GolemMaterialConfig()
				.addMaterial(new ResourceLocation("minecraft:copper"), Ingredient.of(Blocks.COPPER_BLOCK))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 50)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 10).end()
				.addMaterial(new ResourceLocation("minecraft:iron"), Ingredient.of(Blocks.IRON_BLOCK))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 100)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 15).end()
				.addMaterial(new ResourceLocation("minecraft:gold"), Ingredient.of(Blocks.GOLD_BLOCK))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 20)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 5)
				.addStat(GolemTypeRegistry.STAT_SPEED.get(), -0.1).end()
				.addMaterial(new ResourceLocation("minecraft:netherite"), Ingredient.of(Blocks.NETHERITE_BLOCK))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 400)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 30)
				.addStat(GolemTypeRegistry.STAT_SPEED.get(), -0.1).end()
		);
	}


}