package dev.xkmc.modulargolems.compat.materials.royalvariation;

import com.mongoose.royalvariations.common.items.RVItems;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class RVConfigGen extends ConfigDataProvider {

	public RVConfigGen(DataGenerator generator) {
		super(generator, "Golem Config for Royal Variation");
	}

	@Override
	public void add(Collector map) {
		map.add(ModularGolems.MATERIALS, new ResourceLocation(RVDispatch.MODID, "royal"), new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(RVDispatch.MODID, "royal"),
						Ingredient.of(RVItems.SPIRITUAL_CROWN_SHARD.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 200)
				.addStat(GolemTypes.STAT_ATTACK.get(), 15)
				.addStat(GolemTypes.STAT_ATKKB.get(), 1)
				.addStat(GolemTypes.STAT_WEIGHT.get(), -0.2)
				.addModifier(RVCompatRegistry.CALVARY.get(), 1)
				.addModifier(RVCompatRegistry.MARKING.get(), 1)
				.end()

		);
	}

}
