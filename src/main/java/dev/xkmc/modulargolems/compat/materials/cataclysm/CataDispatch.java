package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.init.ModEntities;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.loot.MGGLMGen;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

public class CataDispatch extends ModDispatch {

	public static final String MODID = "cataclysm";

	public CataDispatch() {
		CataCompatRegistry.register();
		MinecraftForge.EVENT_BUS.register(CataEventHandler.class);
	}

	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".ignitium", "Ignitium");
		pvd.add("golem_material." + MODID + ".witherite", "Witherite");
		pvd.add("golem_material." + MODID + ".cursium", "Cursium");
		pvd.add("golem_material." + MODID + ".storm", "Storm");
		pvd.add("golem_material." + MODID + ".ender_guardian", "Ender Guardian");
		pvd.add("golem_material." + MODID + ".ancient_metal", "Ancient Metal");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		CataRecipGen.genRecipe(pvd);
	}

	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new CataConfigGen(gen);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void dispatchClientSetup() {
		ModelOverrides.registerOverride(new ResourceLocation(CataDispatch.MODID, "ignitium"),
				ModelOverride.texturePredicate((e) -> ignisBlue(e) ? "_soul" : ""));
	}

	@Override
	public void genLootModifier(MGGLMGen pvd) {
		pvd.drop(MODID, ModEntities.IGNIS.get(), "ignitium");
		pvd.drop(MODID, ModEntities.THE_HARBINGER.get(), "witherite");
		pvd.drop(MODID, ModEntities.MALEDICTUS.get(), "cursium");
		pvd.drop(ModularGolems.MODID, ModEntities.NETHERITE_MONSTROSITY.get(), "netherite");
		pvd.drop(MODID, ModEntities.ENDER_GUARDIAN.get(), "ender_guardian");
		pvd.drop(MODID, ModEntities.SCYLLA.get(), "storm");
		pvd.drop(MODID, ModEntities.ANCIENT_REMNANT.get(), "ancient_metal");
	}

	public static boolean ignisBlue(LivingEntity e) {
		return e.getHealth() <= e.getMaxHealth() / 2;
	}

}
