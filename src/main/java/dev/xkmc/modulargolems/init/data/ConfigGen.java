package dev.xkmc.modulargolems.init.data;

import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class ConfigGen extends ConfigDataProvider {

	public ConfigGen(DataGenerator generator) {
		super(generator, "data/modulargolems/golem_config/", "Golem Config");
	}

	@Override
	public void add(Map<String, BaseConfig> map) {
		map.put("material/vanilla", new GolemMaterialConfig()
				.addMaterial(new ResourceLocation("minecraft:copper"), Ingredient.of(Items.COPPER_INGOT))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 12)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 3).end()
				.addMaterial(new ResourceLocation("minecraft:iron"),Ingredient.of(Items.IRON_INGOT))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 25)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 4).end()
				.addMaterial(new ResourceLocation("minecraft:gold"), Ingredient.of(Items.GOLD_INGOT))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 5)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 2)
				.addStat(GolemTypeRegistry.STAT_SPEED.get(), -0.1).end()
				.addMaterial(new ResourceLocation("minecraft:netherite"),Ingredient.of(Items.NETHERITE_INGOT))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 100)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 7)
				.addStat(GolemTypeRegistry.STAT_SPEED.get(), -0.1).end()
		);
	}


}