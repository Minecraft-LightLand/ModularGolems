package dev.xkmc.modulargolems.compat.materials.common;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public abstract class ModDispatch {

	protected abstract void genLang(RegistrateLangProvider pvd);

	public abstract void genRecipe(RegistrateRecipeProvider pvd);

	@Nullable
	public abstract ConfigDataProvider getDataGen(DataGenerator gen);

	public static <T> T safeUpgrade(RegistrateRecipeProvider pvd, BiFunction<String, Criterion<InventoryChangeTrigger.TriggerInstance>, T> func, Item item) {
		return func.apply("has_" + pvd.safeName(item), DataIngredient.items(GolemItems.EMPTY_UPGRADE.get()).getCriterion(pvd));
	}

	public void dispatchClientSetup() {
	}

	public void lateRegister() {
	}

}
