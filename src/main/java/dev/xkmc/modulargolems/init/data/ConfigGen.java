package dev.xkmc.modulargolems.init.data;

import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.config.GolemPartConfig;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItemRegistry;
import dev.xkmc.modulargolems.init.registrate.GolemModifierRegistry;
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
		// stats of the material
		// the value represents what if the entire golem is made of this material
		map.put("materials/vanilla", new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(ModularGolems.MODID, "copper"), Ingredient.of(Items.COPPER_INGOT))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 50)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 10).end()

				.addMaterial(new ResourceLocation(ModularGolems.MODID, "iron"), Ingredient.of(Items.IRON_INGOT))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 100)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 15).end()

				.addMaterial(new ResourceLocation(ModularGolems.MODID, "gold"), Ingredient.of(Items.GOLD_INGOT))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 20)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 5)
				.addStat(GolemTypeRegistry.STAT_WEIGHT.get(), -0.4)
				.addStat(GolemTypeRegistry.STAT_ATKSPEED.get(), 0.5).end()

				.addMaterial(new ResourceLocation(ModularGolems.MODID, "netherite"), Ingredient.of(Items.NETHERITE_INGOT))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 300)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 30)
				.addStat(GolemTypeRegistry.STAT_WEIGHT.get(), -0.4)
				.addStat(GolemTypeRegistry.STAT_SWEEP.get(), 2)
				.addModifier(GolemModifierRegistry.FIRE_IMMUNE.get(), 1).end()

				.addMaterial(new ResourceLocation(ModularGolems.MODID, "sculk"), Ingredient.of(Items.ECHO_SHARD))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 500)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 30)
				.addStat(GolemTypeRegistry.STAT_SPEED.get(), 0.5)
				.addModifier(GolemModifierRegistry.FIRE_IMMUNE.get(), 1).end()
		);

		// Choose which stat to use, and what percentage for the complete golem
		map.put("parts/default", new GolemPartConfig()
				.addPart(GolemItemRegistry.GOLEM_BODY.get())
				.addFilter(StatFilterType.HEALTH, 0.5)
				.addFilter(StatFilterType.ATTACK, 0)
				.addFilter(StatFilterType.MOVEMENT, 0)
				.addFilter(StatFilterType.MASS, 0.3).end()

				.addPart(GolemItemRegistry.GOLEM_ARM.get())
				.addFilter(StatFilterType.HEALTH, 0)
				.addFilter(StatFilterType.ATTACK, 0.5)
				.addFilter(StatFilterType.MOVEMENT, 0)
				.addFilter(StatFilterType.MASS, 0.2).end()

				.addPart(GolemItemRegistry.GOLEM_LEGS.get())
				.addFilter(StatFilterType.HEALTH, 0.5)
				.addFilter(StatFilterType.ATTACK, 0)
				.addFilter(StatFilterType.MOVEMENT, 1)
				.addFilter(StatFilterType.MASS, 0.3).end()

				.addPart(GolemItemRegistry.HUMANOID_BODY.get())
				.addFilter(StatFilterType.HEALTH, 0.5)
				.addFilter(StatFilterType.ATTACK, 0)
				.addFilter(StatFilterType.MOVEMENT, 0)
				.addFilter(StatFilterType.MASS, 0.4).end()

				.addPart(GolemItemRegistry.HUMANOID_ARMS.get())
				.addFilter(StatFilterType.HEALTH, 0)
				.addFilter(StatFilterType.ATTACK, 1)
				.addFilter(StatFilterType.MOVEMENT, 0)
				.addFilter(StatFilterType.MASS, 0.3).end()

				.addPart(GolemItemRegistry.HUMANOID_LEGS.get())
				.addFilter(StatFilterType.HEALTH, 0.5)
				.addFilter(StatFilterType.ATTACK, 0)
				.addFilter(StatFilterType.MOVEMENT, 1)
				.addFilter(StatFilterType.MASS, 0.3).end()

				.addEntity(GolemTypeRegistry.TYPE_GOLEM.get())
				.end()

				.addEntity(GolemTypeRegistry.TYPE_HUMANOID.get())
				.addFilter(GolemTypeRegistry.STAT_HEALTH.get(), 0.4)
				.addFilter(GolemTypeRegistry.STAT_ATTACK.get(), 0.3)
				.end()
		);
	}

}