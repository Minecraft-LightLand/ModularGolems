package dev.xkmc.modulargolems.init.data;

import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.config.GolemPartConfig;
import dev.xkmc.modulargolems.init.registrate.GolemItemRegistry;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;

public class ConfigGen extends ConfigDataProvider {

	public ConfigGen(DataGenerator generator) {
		super(generator, "data/modulargolems/golem_config/", "Golem Config");
	}

	@Override
	public void add(Map<String, BaseConfig> map) {
		map.put("materials/vanilla", new GolemMaterialConfig()
				.addMaterial(new ResourceLocation("minecraft:copper"), Ingredient.of(Items.COPPER_INGOT))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 50)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 10).end()
				.addMaterial(new ResourceLocation("minecraft:iron"), Ingredient.of(Items.IRON_INGOT))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 100)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 15).end()
				.addMaterial(new ResourceLocation("minecraft:gold"), Ingredient.of(Items.GOLD_INGOT))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 20)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 5)
				.addStat(GolemTypeRegistry.STAT_SPEED.get(), -0.1).end()
				.addMaterial(new ResourceLocation("minecraft:netherite"), Ingredient.of(Items.NETHERITE_INGOT))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 300)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 30)
				.addStat(GolemTypeRegistry.STAT_SPEED.get(), -0.1)
				.addStat(GolemTypeRegistry.STAT_SWEEP.get(), 2).end()
		);


		map.put("parts/default", new GolemPartConfig()
				.addMaterial(GolemItemRegistry.GOLEM_BODY.get())
				.addFilter(GolemTypeRegistry.STAT_HEALTH.get(), 0.5)
				.addFilter(GolemTypeRegistry.STAT_REGEN.get(), 0.5)
				.addFilter(GolemTypeRegistry.STAT_ATTACK.get(), 0)
				.addFilter(GolemTypeRegistry.STAT_SWEEP.get(), 0).end()
				.addMaterial(GolemItemRegistry.GOLEM_LEFT_HAND.get())
				.addFilter(GolemTypeRegistry.STAT_HEALTH.get(), 0.1)
				.addFilter(GolemTypeRegistry.STAT_REGEN.get(), 0)
				.addFilter(GolemTypeRegistry.STAT_ATTACK.get(), 0.5)
				.addFilter(GolemTypeRegistry.STAT_SWEEP.get(), 0.5).end()
				.addMaterial(GolemItemRegistry.GOLEM_RIGHT_HAND.get())
				.addFilter(GolemTypeRegistry.STAT_HEALTH.get(), 0.1)
				.addFilter(GolemTypeRegistry.STAT_REGEN.get(), 0)
				.addFilter(GolemTypeRegistry.STAT_ATTACK.get(), 0.5)
				.addFilter(GolemTypeRegistry.STAT_SWEEP.get(), 0.5).end()
				.addMaterial(GolemItemRegistry.GOLEM_LEGS.get())
				.addFilter(GolemTypeRegistry.STAT_HEALTH.get(), 0.3)
				.addFilter(GolemTypeRegistry.STAT_REGEN.get(), 0.5)
				.addFilter(GolemTypeRegistry.STAT_ATTACK.get(), 0)
				.addFilter(GolemTypeRegistry.STAT_SWEEP.get(), 0).end()
		);
	}


}