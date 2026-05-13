package dev.xkmc.modulargolems.content.entity.skin;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.component.ResolvableProfile;

public class ClientProfileManager {

	public static SpecialRenderProfile get(String name) {
		var profile = ResolvableProfile.createUnresolved(name);
		var cache = Minecraft.getInstance().playerSkinRenderCache();
		var info = cache.getOrDefault(profile);
		boolean slim = info.playerSkin().model() == PlayerModelType.SLIM;
		Identifier texture = info.playerSkin().body().texturePath();
		return new SpecialRenderProfile(slim, texture);
	}

}
