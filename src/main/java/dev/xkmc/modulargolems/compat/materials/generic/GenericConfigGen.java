package dev.xkmc.modulargolems.compat.materials.generic;

import com.simibubi.create.AllTags;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.create.CreateCompatRegistry;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class GenericConfigGen extends ConfigDataProvider {
    public GenericConfigGen(DataGenerator generator) {
        super(generator, "Golem Config for Generic Materials");
    }

    @Override
    public void add(Collector map) {
        map.add(ModularGolems.MATERIALS, new ResourceLocation(GenericDispatch.MODID, GenericDispatch.MODID), new GolemMaterialConfig()
                .addMaterial(new ResourceLocation(GenericDispatch.MODID, "silver"),
                        Ingredient.of(AllTags.forgeItemTag("ingots/silver")))
                .addStat(GolemTypes.STAT_HEALTH.get(), 20)
                .addStat(GolemTypes.STAT_ATTACK.get(), 10)
                .addStat(GolemTypes.STAT_WEIGHT.get(), -0.2)
                .addModifier(GenericCompatRegistry.DIVINE.get(), 2)
                .addModifier(GolemModifiers.THUNDER_IMMUNE.get(), 1)
                .addModifier(GolemModifiers.MAGIC_RES.get(), 1).end()

                .addMaterial(new ResourceLocation(GenericDispatch.MODID, "tin"),
                        Ingredient.of(AllTags.forgeItemTag("ingots/tin")))
                .addStat(GolemTypes.STAT_HEALTH.get(), 100)
                .addStat(GolemTypes.STAT_ATTACK.get(), 15)
                .addModifier(CreateCompatRegistry.COATING.get(), 1)
                .addModifier(GenericCompatRegistry.WEIDING.get(), 1)
                .addModifier(GenericCompatRegistry.TIN_PLAGUE.get(), 1).end()

                .addMaterial(new ResourceLocation(GenericDispatch.MODID, "lead"),
                        Ingredient.of(AllTags.forgeItemTag("ingots/lead")))
                .addStat(GolemTypes.STAT_HEALTH.get(), 10)
                .addStat(GolemTypes.STAT_ATTACK.get(), 20)
                .addStat(GolemTypes.STAT_WEIGHT.get(), -0.2)
                .addStat(GolemTypes.STAT_KBRES.get(), 1)
                .addStat(GolemTypes.STAT_ATKKB.get(), 1)
                .addModifier(GenericCompatRegistry.WEIDING.get(), 1)
                .addModifier(GenericCompatRegistry.POISONOUS.get(), 1)
                .addModifier(GolemModifiers.MAGIC_RES.get(), 1).end()
        );
    }
}
