package dev.xkmc.modulargolems.compat.materials.twilightforest;

import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.registrate.GolemModifierRegistry;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import twilightforest.data.tags.ItemTagGenerator;

import java.util.Map;
import java.util.Objects;

public class TFConfigGen extends ConfigDataProvider {

	public TFConfigGen(DataGenerator generator) {
		super(generator, "data/" + TFDispatch.MODID + "/golem_config/", "Golem Config for Twilight Forest");
	}

	public void add(Map<String, BaseConfig> map) {
		map.put("materials/" + TFDispatch.MODID, new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(TFDispatch.MODID, "ironwood"), Ingredient.of(ItemTagGenerator.IRONWOOD_INGOTS))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 200)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 10)
				.addStat(GolemTypeRegistry.STAT_REGEN.get(), 2)
				.addModifier(TFCompatRegistry.TF_DAMAGE.get(), 1)
				.addModifier(TFCompatRegistry.TF_HEALING.get(), 1).end()

				.addMaterial(new ResourceLocation(TFDispatch.MODID, "steeleaf"), Ingredient.of(ItemTagGenerator.STEELEAF_INGOTS))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 20)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 30)
				.addModifier(TFCompatRegistry.TF_DAMAGE.get(), 1)
				.addModifier(TFCompatRegistry.TF_HEALING.get(), 1).end()

				.addMaterial(new ResourceLocation(TFDispatch.MODID, "knightmetal"), Ingredient.of(ItemTagGenerator.KNIGHTMETAL_INGOTS))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 300)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 20)
				.addStat(GolemTypeRegistry.STAT_WEIGHT.get(), -0.4)
				.addModifier(GolemModifierRegistry.THORN.get(), 2)
				.addModifier(TFCompatRegistry.TF_DAMAGE.get(), 1)
				.addModifier(TFCompatRegistry.TF_HEALING.get(), 1)
				.end()

				.addMaterial(new ResourceLocation(TFDispatch.MODID, "fiery"), Ingredient.of(ItemTagGenerator.FIERY_INGOTS))
				.addStat(GolemTypeRegistry.STAT_HEALTH.get(), 200)
				.addStat(GolemTypeRegistry.STAT_ATTACK.get(), 20)
				.addModifier(GolemModifierRegistry.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifierRegistry.THORN.get(), 1)
				.addModifier(TFCompatRegistry.FIERY.get(), 1)
				.addModifier(TFCompatRegistry.TF_DAMAGE.get(), 1)
				.addModifier(TFCompatRegistry.TF_HEALING.get(), 1)
				.end()
		);
	}

}
