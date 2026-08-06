package dev.xkmc.modulargolems.editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import dev.xkmc.l2library.serial.config.BaseConfig;
import dev.xkmc.l2library.serial.config.ConfigTypeEntry;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EditorData {

	public static final String PACK_FOLDER = "modulargolems_editor";
	public static final int PACK_FORMAT = 15;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

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
	public static Path worldDatapacks() {
		IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
		if (server == null) return null;
		return server.getWorldPath(LevelResource.DATAPACK_DIR);
	}

	public static boolean validNamespace(String ns) {
		return ns != null && (ns.startsWith("_") || ModList.get().isLoaded(ns));
	}

	@Nullable
	public static ResourceLocation parseId(String s) {
		if (s == null || s.isBlank()) return null;
		try {
			return new ResourceLocation(s.trim());
		} catch (Exception e) {
			return null;
		}
	}

	@Nullable
	public static Component validateFileId(String s) {
		ResourceLocation id = parseId(s);
		if (id == null) return EditorLang.INVALID_ID.get(s);
		if (!validNamespace(id.getNamespace())) return EditorLang.NAMESPACE_HINT.get();
		return null;
	}

	public static <T extends BaseConfig> Path save(ConfigTypeEntry<T> type, ResourceLocation id, T config) throws IOException {
		Path root = worldDatapacks();
		if (root == null) {
			throw new IOException("no active world");
		}
		Path pack = root.resolve(PACK_FOLDER);
		writePackMeta(pack);
		Path file = pack.resolve(type.asPath(id) + ".json");
		Files.createDirectories(file.getParent());
		JsonElement elem = JsonCodec.toJson(config, type.cls());
		Files.writeString(file, GSON.toJson(elem), StandardCharsets.UTF_8);
		return file;
	}

	private static void writePackMeta(Path pack) throws IOException {
		Files.createDirectories(pack);
		Path meta = pack.resolve("pack.mcmeta");
		if (!Files.exists(meta)) {
			String content = "{\n  \"pack\": {\n    \"description\": \"Modular Golems Editor\",\n    \"pack_format\": " + PACK_FORMAT + "\n  }\n}";
			Files.writeString(meta, content, StandardCharsets.UTF_8);
		}
	}

	public static <T extends BaseConfig> T copy(ConfigTypeEntry<T> type, T orig) {
		return JsonCodec.from(JsonCodec.toJson(orig, type.cls()), type.cls(), null);
	}

	public static GolemMaterialConfig newMaterial() {
		return new GolemMaterialConfig();
	}

	public static Path currentWorldDir() {
		MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
		if (server == null) return null;
		return server.getWorldPath(LevelResource.ROOT);
	}

	public static Optional<ResourceLocation> getID(ConfigTypeEntry<? extends BaseConfig> type, BaseConfig config) {
		return Optional.ofNullable(config.getID());
	}

}
