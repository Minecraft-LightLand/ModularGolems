package dev.xkmc.modulargolems.compat.materials.common;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.repack.registrate.util.DataIngredient;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.init.registrate.GolemItemRegistry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;

public abstract class ModDispatch {

	protected abstract void genLang(RegistrateLangProvider pvd);

	public abstract void genRecipe(RegistrateRecipeProvider pvd);

	protected abstract ConfigDataProvider getDataGen(DataGenerator gen);

	public static <T> T safeUpgrade(RegistrateRecipeProvider pvd, BiFunction<String, InventoryChangeTrigger.TriggerInstance, T> func, Item item) {
		return func.apply("has_" + pvd.safeName(item), DataIngredient.items(GolemItemRegistry.EMPTY_UPGRADE).getCritereon(pvd));
	}

}
