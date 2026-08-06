package dev.xkmc.modulargolems.editor.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import dev.xkmc.l2library.serial.config.BaseConfig;
import dev.xkmc.l2library.serial.config.ConfigTypeEntry;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class EditorFile {

	public static final int PACK_FORMAT = 15;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	@Nullable
	public static Path worldDatapacks() {
		IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
		if (server == null) return null;
		return server.getWorldPath(LevelResource.DATAPACK_DIR);
	}

	@Nullable
	public static Path currentWorldDir() {
		MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
		if (server == null) return null;
		return server.getWorldPath(LevelResource.ROOT);
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

	public static <T extends BaseConfig> Path save(ConfigTypeEntry<T> type, ResourceLocation id, T config, String packFolder) throws IOException {
		Path root = worldDatapacks();
		if (root == null) {
			throw new IOException("no active world");
		}
		Path pack = root.resolve(packFolder);
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

	@Nullable
	public static <T extends BaseConfig> T copy(ConfigTypeEntry<T> type, T orig) {
		return JsonCodec.from(JsonCodec.toJson(orig, type.cls()), type.cls(), null);
	}

}
