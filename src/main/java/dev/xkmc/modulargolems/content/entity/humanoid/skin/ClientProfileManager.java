package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.TreeMap;

public class ClientProfileManager {

	private static final Map<String, GameProfile> CACHE = new TreeMap<>();

	@Nullable
	public static SpecialRenderProfile get(String name) {
		var profile = getProfile(name);
		if (profile == null) return null;
		SkinManager skins = Minecraft.getInstance().getSkinManager();
		PlayerSkin skin = skins.getInsecureSkin(profile);
		PlayerSkin.Model skinModel = skin.model();
		boolean slim = skinModel == PlayerSkin.Model.SLIM;
		ResourceLocation texture = skins.getInsecureSkin(profile).texture();
		if (texture.equals(ResourceLocation.withDefaultNamespace("missingno")))
			return null;
		return new SpecialRenderProfile(slim, texture);
	}

	@Nullable
	private static GameProfile getProfile(String name) {
		if (!CACHE.containsKey(name)) {
			CACHE.put(name, null);
			SkullBlockEntity.fetchGameProfile(name).thenAccept(x -> x.ifPresent(e -> CACHE.put(name, e)));
		}
		return CACHE.get(name);
	}

}
