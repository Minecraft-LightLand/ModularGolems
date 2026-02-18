package dev.xkmc.modulargolems.compat.materials.common;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.init.loot.MGGLMGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class ModDispatch {

	final LazyOptional<ClientModDispatch> client;

	protected ModDispatch() {
		this.client = LazyOptional.empty();
	}

	protected ModDispatch(Supplier<Supplier<ClientModDispatch>> client) {
		this.client = LazyOptional.of(() -> client.get().get());
	}

	protected abstract void genLang(RegistrateLangProvider pvd);

	public abstract void genRecipe(RegistrateRecipeProvider pvd);

	@Nullable
	public abstract ConfigDataProvider getDataGen(DataGenerator gen);

	public static <T> T safeUpgrade(RegistrateRecipeProvider pvd, BiFunction<String, InventoryChangeTrigger.TriggerInstance, T> func, Item item) {
		return func.apply("has_" + pvd.safeName(item), DataIngredient.items(GolemItems.EMPTY_UPGRADE.get()).getCritereon(pvd));
	}

	public void lateRegister() {
	}

	public void commonSetup() {
	}

	public void genLootModifier(MGGLMGen pvd) {
	}

}
