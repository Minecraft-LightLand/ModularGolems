package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.init.ModEntities;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.IgnisArmorItem;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.loot.MGGLMGen;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;

public class CataDispatch extends ModDispatch {

	public static final String MODID = "cataclysm";

	public CataDispatch() {
		super(() -> CataClient::new);
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
		return e.getHealth() <= e.getMaxHealth() / 2 || e.getItemBySlot(EquipmentSlot.CHEST).is(CataCompatRegistry.IGNIS_CHESTPLATE.get());
	}

	public static void stackBlazingBrand(LivingEntity golem, LivingEntity target, float dmg, int min) {
		if (golem.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof IgnisArmorItem) min++;
		if (golem.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof IgnisArmorItem) min++;
		if (golem.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof IgnisArmorItem) min++;
		GolemCataProxy.stackBlazingBrand(golem, target, dmg, min);
	}

}
