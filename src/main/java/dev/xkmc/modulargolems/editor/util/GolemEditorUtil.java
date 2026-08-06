package dev.xkmc.modulargolems.editor.util;

import dev.xkmc.l2library.serial.config.BaseConfig;
import dev.xkmc.l2library.serial.config.ConfigTypeEntry;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.editor.base.EditorFile;
import dev.xkmc.modulargolems.editor.base.EditorUtil;
import dev.xkmc.modulargolems.editor.base.IngredientScreen;
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
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GolemEditorUtil implements IngredientScreen.Source {

	public static final GolemEditorUtil INSTANCE = new GolemEditorUtil();
	public static final String PACK_FOLDER = "modulargolems_editor";

	public static List<GolemStatType> listStats() {
		List<GolemStatType> ans = new ArrayList<>(GolemTypes.STAT_TYPES.get().getValues());
		ans.sort(EditorUtil.byId(e -> e.getRegistryName().toString()));
		return ans;
	}

	public static List<GolemModifier> listModifiers() {
		List<GolemModifier> ans = new ArrayList<>(GolemTypes.MODIFIERS.get().getValues());
		ans.removeIf(e -> e instanceof AttributeGolemModifier);
		ans.sort(EditorUtil.byId(e -> e.getRegistryName().toString()));
		return ans;
	}

	public static List<GolemType<?, ?>> listGolemTypes() {
		List<GolemType<?, ?>> ans = new ArrayList<>(GolemTypes.TYPES.get().getValues());
		ans.sort(EditorUtil.byId(e -> e.getRegistryName().toString()));
		return ans;
	}

	public static List<Item> listParts() {
		List<Item> ans = new ArrayList<>();
		for (var holder : BuiltInRegistries.ITEM.getTagOrEmpty(MGTagGen.GENERIC_PARTS)) {
			ans.add(holder.value());
		}
		ans.sort(EditorUtil.byId(e -> ForgeRegistries.ITEMS.getKey(e).toString()));
		return ans;
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
	public static Component validateFileId(String s) {
		ResourceLocation id = EditorFile.parseId(s);
		if (id == null) return EditorLang.INVALID_ID.get(s);
		if (!EditorFile.validNamespace(id.getNamespace())) return EditorLang.NAMESPACE_HINT.get();
		return null;
	}

	public static GolemMaterialConfig newMaterial() {
		return new GolemMaterialConfig();
	}

	public static <T extends BaseConfig> Path save(ConfigTypeEntry<T> type, ResourceLocation id, T config) throws IOException {
		return EditorUtil.save(type, id, config, PACK_FOLDER);
	}

	@Override
	public List<Item> items() {
		return EditorUtil.listItems();
	}

	@Override
	public List<TagKey<Item>> tags() {
		return EditorUtil.listTags();
	}

	@Override
	public Component itemName(Item item) {
		return EditorUtil.itemName(item);
	}

	@Override
	public Component tagName(TagKey<Item> tag) {
		return EditorUtil.tagName(tag);
	}

	@Override
	public Ingredient itemIngredient(Item item) {
		return EditorUtil.itemIngredient(item);
	}

	@Override
	public Ingredient tagIngredient(TagKey<Item> tag) {
		return EditorUtil.tagIngredient(tag);
	}

	@Override
	public ItemStack ingredientIcon(Ingredient ing) {
		return EditorUtil.ingredientIcon(ing);
	}

	@Override
	public Component ingredientText(Ingredient ing) {
		return EditorUtil.ingredientText(ing);
	}

	@Override
	public Component itemTitle() {
		return EditorLang.SELECT_ITEM.get();
	}

	@Override
	public Component tagTitle() {
		return EditorLang.SELECT_TAG.get();
	}

}
