package dev.xkmc.modulargolems.compat.materials.common;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.generators.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.init.loot.MGGLMGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class ModDispatch {

	@Nullable
	final Lazy<ClientModDispatch> client;

	protected ModDispatch() {
		this.client = null;
	}

	protected ModDispatch(Supplier<Supplier<ClientModDispatch>> client) {
		this.client = Lazy.of(() -> client.get().get());
	}

	protected abstract void genLang(RegistrateLangProvider pvd);

	public abstract void genRecipe(RegistrateRecipeProvider pvd);

	@Nullable
	public abstract ConfigDataProvider getDataGen(DataGenerator gen, CompletableFuture<HolderLookup.Provider> pvd);

	public static <T> T safeUpgrade(RegistrateRecipeProvider pvd, BiFunction<String, Criterion<InventoryChangeTrigger.TriggerInstance>, T> func, Item item) {
		return func.apply("has_" + pvd.safeName(item), DataIngredient.items(GolemItems.EMPTY_UPGRADE.get()).getCriterion(pvd));
	}

	public void lateRegister() {
	}

	public void commonSetup() {
	}

	public void genLootModifier(MGGLMGen pvd) {
	}

}
