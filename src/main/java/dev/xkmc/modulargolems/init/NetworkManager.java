package dev.xkmc.modulargolems.init;

import dev.xkmc.l2library.serial.config.ConfigMerger;
import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.serial.network.PacketHandlerWithConfig;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public enum NetworkManager {
	MATERIAL;

	public String getID() {
		return name().toLowerCase(Locale.ROOT);
	}

	public static final PacketHandlerWithConfig HANDLER = new PacketHandlerWithConfig(
			new ResourceLocation(ModularGolems.MODID, "main"), 1, "artifact_config"
	);

	public <T extends BaseConfig> T getMerged() {
		return HANDLER.getCachedConfig(getID());
	}

	public static void register() {
		HANDLER.addCachedConfig(MATERIAL.getID(), new ConfigMerger<>(GolemMaterialConfig.class));
	}

}
