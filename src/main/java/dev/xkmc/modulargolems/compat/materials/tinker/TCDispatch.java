package dev.xkmc.modulargolems.compat.materials.tinker;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.compat.materials.tinker.automation.TinkerRecipeGen;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.TConstruct;

public class TCDispatch extends ModDispatch {

	public static final String MODID = TConstruct.MOD_ID;

	public static final TagKey<Item> AMETHYST_BRONZE = tag("amethyst_bronze");
	public static final TagKey<Item> COBALT = tag("cobalt");
	public static final TagKey<Item> MANYULLYN = tag("manyullyn");
	public static final TagKey<Item> HEPATIZON = tag("hepatizon");
	public static final TagKey<Item> ROSE_GOLD = tag("rose_gold");

	public static TagKey<Item> tag(String id) {
		return ItemTags.create(new ResourceLocation("forge", "ingots/" + id));
	}

	public TCDispatch() {
		TCCompatRegistry.register();
	}

	@Override
	protected void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".amethyst_bronze", "Amethyst Bronze");
		pvd.add("golem_material." + MODID + ".manyullyn", "Manyullyn");
		pvd.add("golem_material." + MODID + ".hepatizon", "Hepatizon");
		pvd.add("golem_material." + MODID + ".cobalt", "Cobalt");
		pvd.add("golem_material." + MODID + ".rose_gold", "Rose Gold");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		TinkerRecipeGen.genRecipe(pvd);
	}

	@Nullable
	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new TinkerConfigGen(gen);
	}

}
