package dev.xkmc.modulargolems.editor.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TagFile {

	public static final String ENTITY_TAGS = "entity_types";

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	/**
	 * Writes a tag JSON for the given tag id with replace:true. Values are raw tag entries.
	 */
	public static Path save(ResourceLocation tagId, JsonElement valuesArray, String packFolder) throws IOException {
		Path root = EditorFile.configRoot();
		if (root == null) {
			throw new IOException("no active world");
		}
		Path pack = root.resolve(packFolder);
		Files.createDirectories(pack);
		JsonObject obj = new JsonObject();
		obj.addProperty("replace", true);
		obj.add("values", valuesArray);
		Path file = pack.resolve("data/" + tagId.getNamespace() + "/tags/" + ENTITY_TAGS + "/" + tagId.getPath() + ".json");
		Files.createDirectories(file.getParent());
		Files.writeString(file, GSON.toJson(obj), StandardCharsets.UTF_8);
		return file;
	}

	/**
	 * Reads raw tag values from a single pack, or null if the pack has no such tag.
	 */
	@Nullable
	public static List<JsonElement> read(PackResources pack, ResourceLocation tagId) {
		Loaded loaded = readAll(pack, tagId);
		return loaded == null ? null : loaded.values();
	}

	/**
	 * Reads a tag JSON from a single pack together with its replace flag, or null if absent.
	 */
	@Nullable
	public static Loaded readAll(PackResources pack, ResourceLocation tagId) {
		var sup = pack.getResource(PackType.SERVER_DATA, new ResourceLocation(tagId.getNamespace(),
				"tags/" + ENTITY_TAGS + "/" + tagId.getPath() + ".json"));
		if (sup == null) return null;
		try (var in = sup.get()) {
			String s = new String(in.readAllBytes(), StandardCharsets.UTF_8);
			JsonObject obj = GSON.fromJson(s, JsonObject.class);
			if (obj == null) return null;
			JsonArray arr = obj.has("values") ? obj.getAsJsonArray("values") : new JsonArray();
			boolean replace = obj.has("replace") && obj.get("replace").getAsBoolean();
			List<JsonElement> ans = new ArrayList<>();
			arr.forEach(ans::add);
			return new Loaded(replace, ans);
		} catch (Exception e) {
			return null;
		}
	}

	public record Loaded(boolean replace, List<JsonElement> values) {

	}

}
