package dev.xkmc.modulargolems.compat.materials.common;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.compat.materials.create.CreateDispatch;
import dev.xkmc.modulargolems.compat.materials.l2complements.LCDispatch;
import dev.xkmc.modulargolems.compat.materials.l2hostility.LHDispatch;
import dev.xkmc.modulargolems.compat.materials.twilightforest.TFDispatch;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.data.MGConfigGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CompatManager {

	public static final List<ModDispatch> LIST = new ArrayList<>();

	public static void register() {
		//TODO
		//if (ModList.get().isLoaded(BotDispatch.MODID)) LIST.add(new BotDispatch());
		if (ModList.get().isLoaded(TFDispatch.MODID)) LIST.add(new TFDispatch());
		if (ModList.get().isLoaded(CreateDispatch.MODID)) LIST.add(new CreateDispatch());
		if (ModList.get().isLoaded(LCDispatch.MODID)) LIST.add(new LCDispatch());
		//if (ModList.get().isLoaded(BGDispatch.MODID)) LIST.add(new BGDispatch());
		if (ModList.get().isLoaded(LHDispatch.MODID)) LIST.add(new LHDispatch());
		if (ModList.get().isLoaded(CataDispatch.MODID)) LIST.add(new CataDispatch());
		//if (ModList.get().isLoaded(EnchantmentIndustry.ID)) CEICompat.register();
	}

	public static void dispatchGenLang(RegistrateLangProvider pvd) {
		for (ModDispatch dispatch : LIST) {
			dispatch.genLang(pvd);
		}
	}

	public static void gatherData(GatherDataEvent event) {
		for (ModDispatch dispatch : LIST) {
			var gen = dispatch.getDataGen(event.getGenerator(), event.getLookupProvider());
			if (gen != null) {
				event.getGenerator().addProvider(event.includeServer(), gen);
			}
		}
	}

	public static void dispatchGenRecipe(RegistrateRecipeProvider pvd) {
		for (ModDispatch dispatch : LIST) {
			dispatch.genRecipe(pvd);
		}
	}

	public static void dispatchClientSetup() {
		for (ModDispatch dispatch : LIST) {
			dispatch.dispatchClientSetup();
		}
	}

	public static void lateRegister() {
		for (ModDispatch dispatch : LIST) {
			dispatch.lateRegister();
		}
	}


	private static Map<ResourceLocation, Ingredient> ALL_CONFIGS;

	@SuppressWarnings("ConstantConditions")
	public static Map<ResourceLocation, Ingredient> gatherConfig() {
		if (ALL_CONFIGS != null) return ALL_CONFIGS;
		ConfigDataProvider.Collector map = new ConfigDataProvider.Collector(new HashMap<>());
		for (ModDispatch dispatch : CompatManager.LIST) {
			var gen = dispatch.getDataGen(null, null);
			gen.add(map);
		}
		new MGConfigGen(null, null).add(map);
		Map<ResourceLocation, Ingredient> ing = new HashMap<>();
		for (ConfigDataProvider.ConfigEntry<?> config : map.map().values()) {
			if (config.config() instanceof GolemMaterialConfig mat) {
				ing.putAll(mat.ingredients);
			}
		}
		ALL_CONFIGS = ing;
		return ing;
	}


}
