package dev.xkmc.modulargolems.init.data;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public record NBTRecipe(FinishedRecipe recipe, ItemStack stack) implements FinishedRecipe {//TODO remove in 1.20.2

	public NBTRecipe(FinishedRecipe recipe, ItemStack stack) {
		this.recipe = recipe;
		this.stack = stack;
	}

	public void serializeRecipeData(JsonObject json) {
		this.recipe.serializeRecipeData(json);
		json.getAsJsonObject("result").addProperty("nbt", this.stack.getOrCreateTag().toString());
	}

	public ResourceLocation getId() {
		return this.recipe.getId();
	}

	public RecipeSerializer<?> getType() {
		return this.recipe.getType();
	}

	@Nullable
	public JsonObject serializeAdvancement() {
		return this.recipe.serializeAdvancement();
	}

	@Nullable
	public ResourceLocation getAdvancementId() {
		return this.recipe.getAdvancementId();
	}

	public FinishedRecipe recipe() {
		return this.recipe;
	}

	public ItemStack stack() {
		return this.stack;
	}

}
