package dev.xkmc.modulargolems.compat.materials.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class CreateConfigGen extends ConfigDataProvider {

	public CreateConfigGen(DataGenerator generator, CompletableFuture<HolderLookup.Provider> pvd) {
		super(generator, pvd, "Golem Config for Create");
	}

	public void add(Collector map) {

		map.add(ModularGolems.MATERIALS, Identifier.fromNamespaceAndPath(CreateDispatch.MODID, CreateDispatch.MODID), new GolemMaterialConfig()
				.addMaterial(Identifier.fromNamespaceAndPath(CreateDispatch.MODID, "cardboard"),
						Ingredient.of(AllItems.CARDBOARD))
				.addStat(GolemTypes.STAT_HEALTH.get(), 2)
				.addStat(GolemTypes.STAT_ATTACK.get(), 2)
				.addStat(GolemTypes.STAT_ATKKB.get(), 2)
				.addStat(GolemTypes.STAT_WEIGHT.get(), 0.4)
				.end()

				.addMaterial(Identifier.fromNamespaceAndPath(CreateDispatch.MODID, "zinc"),
						Ingredient.of(AllTags.commonItemTag("ingots/zinc")))
				.addStat(GolemTypes.STAT_HEALTH.get(), 50)
				.addStat(GolemTypes.STAT_ATTACK.get(), 10)
				.addModifier(CreateCompatRegistry.COATING.get(), 1).end()

				.addMaterial(Identifier.fromNamespaceAndPath(CreateDispatch.MODID, "andesite_alloy"),
						Ingredient.of(AllItems.ANDESITE_ALLOY.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 30)
				.addStat(GolemTypes.STAT_ATTACK.get(), 5)
				.addModifier(CreateCompatRegistry.BODY.get(), 1)
				.addModifier(CreateCompatRegistry.MOBILE.get(), 1)
				.addModifier(CreateCompatRegistry.FORCE.get(), 1)
				.addModifier(GolemModifiers.MAGIC_RES.get(), 1).end()

				.addMaterial(Identifier.fromNamespaceAndPath(CreateDispatch.MODID, "brass"),
						Ingredient.of(AllTags.commonItemTag("ingots/brass")))
				.addStat(GolemTypes.STAT_HEALTH.get(), 150)
				.addStat(GolemTypes.STAT_ATTACK.get(), 15)
				.addStat(GolemTypes.STAT_KBRES.get(), 0.5)
				.addModifier(CreateCompatRegistry.BODY.get(), 1)
				.addModifier(CreateCompatRegistry.MOBILE.get(), 1)
				.addModifier(CreateCompatRegistry.FORCE.get(), 1)
				.addModifier(GolemModifiers.MAGIC_RES.get(), 2).end()

				.addMaterial(Identifier.fromNamespaceAndPath(CreateDispatch.MODID, "railway"),
						Ingredient.of(AllBlocks.RAILWAY_CASING.get()))
				.addStat(GolemTypes.STAT_HEALTH.get(), 300)
				.addStat(GolemTypes.STAT_ATTACK.get(), 15)
				.addStat(GolemTypes.STAT_SWEEP.get(), 1)
				.addStat(GolemTypes.STAT_KBRES.get(), 1)
				.addStat(GolemTypes.STAT_ATKKB.get(), 1)
				.addStat(GolemTypes.STAT_WEIGHT.get(), -0.2)
				.addModifier(CreateCompatRegistry.BODY.get(), 1)
				.addModifier(CreateCompatRegistry.MOBILE.get(), 2)
				.addModifier(CreateCompatRegistry.FORCE.get(), 2)
				.addModifier(GolemModifiers.MAGIC_RES.get(), 2)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1).end()

		);
	}

}
