package dev.xkmc.modulargolems.editor.base;

import com.google.gson.JsonElement;
import dev.xkmc.l2library.serial.config.BaseConfig;
import dev.xkmc.l2library.serial.config.ConfigTypeEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class EditorUtil {

	public static <T> Comparator<T> byId(Function<T, String> name) {
		return Comparator.comparing(name);
	}

	public static List<Item> listItems() {
		List<Item> ans = new ArrayList<>(ForgeRegistries.ITEMS.getValues());
		ans.removeIf(e -> ForgeRegistries.ITEMS.getKey(e) == null);
		ans.sort(byId(e -> ForgeRegistries.ITEMS.getKey(e).toString()));
		return ans;
	}

	public static List<TagKey<Item>> listTags() {
		List<TagKey<Item>> ans = BuiltInRegistries.ITEM.getTagNames().toList();
		ans.sort(byId(e -> e.location().toString()));
		return ans;
	}

	public static Component itemName(Item item) {
		return new ItemStack(item).getHoverName();
	}

	public static Component tagName(TagKey<Item> tag) {
		return Component.literal("#" + tag.location());
	}

	public static Ingredient itemIngredient(Item item) {
		return Ingredient.of(item);
	}

	public static Ingredient tagIngredient(TagKey<Item> tag) {
		return Ingredient.of(tag);
	}

	@Nullable
	public static ItemStack ingredientIcon(Ingredient ing) {
		if (ing == null || ing.isEmpty()) return null;
		ItemStack[] items = ing.getItems();
		return items.length == 0 ? null : items[0];
	}

	public static Component ingredientText(Ingredient ing) {
		if (ing == null || ing.isEmpty()) return Component.literal("-");
		JsonElement elem = ing.toJson();
		if (elem.isJsonObject()) {
			var obj = elem.getAsJsonObject();
			if (obj.has("tag")) {
				return Component.literal("#" + obj.get("tag").getAsString());
			}
			if (obj.has("item")) {
				return Component.literal(obj.get("item").getAsString());
			}
			if (obj.has("items")) {
				int n = obj.get("items").getAsJsonArray().size();
				return Component.translatable("gui.item_list.multi", n);
			}
		}
		ItemStack icon = ingredientIcon(ing);
		if (icon != null) {
			return Component.literal(ForgeRegistries.ITEMS.getKey(icon.getItem()).toString());
		}
		return Component.literal("?");
	}

	public static <T extends BaseConfig> Path save(ConfigTypeEntry<T> type, ResourceLocation id, T config, String packFolder) throws IOException {
		return EditorFile.save(type, id, config, packFolder);
	}

	@Nullable
	public static <T extends BaseConfig> T copy(ConfigTypeEntry<T> type, T orig) {
		return EditorFile.copy(type, orig);
	}

}
