package dev.xkmc.modulargolems.compat.materials.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record ConditionalRecipeWrapper(FinishedRecipe base, String modid) implements FinishedRecipe {

	public static Consumer<FinishedRecipe> mod(RegistrateRecipeProvider pvd, String modid) {
		return (r) -> pvd.accept(new ConditionalRecipeWrapper(r, modid));
	}

	@Override
	public void serializeRecipeData(JsonObject pJson) {
		base.serializeRecipeData(pJson);
	}

	@Override
	public ResourceLocation getId() {
		return base.getId();
	}

	@Override
	public RecipeSerializer<?> getType() {
		return base.getType();
	}

	@Nullable
	@Override
	public JsonObject serializeAdvancement() {
		return base.serializeAdvancement();
	}

	@Nullable
	@Override
	public ResourceLocation getAdvancementId() {
		return base.getAdvancementId();
	}

	@Override
	public JsonObject serializeRecipe() {
		JsonObject ans = base.serializeRecipe();
		JsonArray conditions = new JsonArray();
		JsonObject condition = new JsonObject();
		condition.addProperty("type", "forge:mod_loaded");
		condition.addProperty("modid", modid);
		conditions.add(condition);
		ans.add("conditions", conditions);
		return ans;
	}
}
