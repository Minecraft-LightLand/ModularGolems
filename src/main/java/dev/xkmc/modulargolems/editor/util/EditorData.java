package dev.xkmc.modulargolems.editor.util;

import com.google.gson.JsonElement;
import dev.xkmc.l2library.serial.config.BaseConfig;
import dev.xkmc.l2library.serial.config.ConfigTypeEntry;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.editor.base.EditorFile;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EditorData {

	public static final String PACK_FOLDER = "modulargolems_editor";

	public static boolean savedFlag;

	private static <T> Comparator<T> byId(java.util.function.Function<T, String> name) {
		return Comparator.comparing(name);
	}

	public static List<GolemStatType> listStats() {
		List<GolemStatType> ans = new ArrayList<>(GolemTypes.STAT_TYPES.get().getValues());
		ans.sort(byId(e -> e.getRegistryName().toString()));
		return ans;
	}

	public static List<GolemModifier> listModifiers() {
		List<GolemModifier> ans = new ArrayList<>(GolemTypes.MODIFIERS.get().getValues());
		ans.removeIf(e -> e instanceof AttributeGolemModifier);
		ans.sort(byId(e -> e.getRegistryName().toString()));
		return ans;
	}

	public static List<GolemType<?, ?>> listGolemTypes() {
		List<GolemType<?, ?>> ans = new ArrayList<>(GolemTypes.TYPES.get().getValues());
		ans.sort(byId(e -> e.getRegistryName().toString()));
		return ans;
	}

	public static List<Item> listItems() {
		List<Item> ans = new ArrayList<>(ForgeRegistries.ITEMS.getValues());
		ans.removeIf(e -> ForgeRegistries.ITEMS.getKey(e) == null);
		ans.sort(byId(e -> ForgeRegistries.ITEMS.getKey(e).toString()));
		return ans;
	}

	public static List<Item> listParts() {
		List<Item> ans = new ArrayList<>();
		for (var holder : BuiltInRegistries.ITEM.getTagOrEmpty(MGTagGen.GENERIC_PARTS)) {
			ans.add(holder.value());
		}
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

	public static Component statName(GolemStatType stat) {
		MutableComponent ans = Component.translatable(stat.getAttribute().getDescriptionId());
		if (stat.percentDisplay()) {
			ans = ans.append("%");
		}
		return ans;
	}

	public static Component statFilterName(StatFilterType type) {
		return Component.translatable(ModularGolems.MODID + ".editor.stat_filter." + type.name());
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

	@Nullable
	public static Component validateFileId(String s) {
		ResourceLocation id = EditorFile.parseId(s);
		if (id == null) return EditorLang.INVALID_ID.get(s);
		if (!EditorFile.validNamespace(id.getNamespace())) return EditorLang.NAMESPACE_HINT.get();
		return null;
	}

	public static <T extends BaseConfig> Path save(ConfigTypeEntry<T> type, ResourceLocation id, T config) throws java.io.IOException {
		return EditorFile.save(type, id, config, PACK_FOLDER);
	}

	public static <T extends BaseConfig> T copy(ConfigTypeEntry<T> type, T orig) {
		return EditorFile.copy(type, orig);
	}

	public static GolemMaterialConfig newMaterial() {
		return new GolemMaterialConfig();
	}

	public static Optional<ResourceLocation> getID(ConfigTypeEntry<? extends BaseConfig> type, BaseConfig config) {
		return Optional.ofNullable(config.getID());
	}

}
