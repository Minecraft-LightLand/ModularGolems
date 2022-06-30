package dev.xkmc.modulargolems.init;

import dev.xkmc.l2library.serial.config.ConfigMerger;
import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.serial.network.PacketHandlerWithConfig;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public enum NetworkManager {
	MATERIALS, PARTS;

	public String getID() {
		return name().toLowerCase(Locale.ROOT);
	}

	public static final PacketHandlerWithConfig HANDLER = new PacketHandlerWithConfig(
			new ResourceLocation(ModularGolems.MODID, "main"), 1, "golem_config"
	);

	public <T extends BaseConfig> T getMerged() {
		return HANDLER.getCachedConfig(getID());
	}

	public static void register() {
		HANDLER.addCachedConfig(MATERIALS.getID(), new ConfigMerger<>(GolemMaterialConfig.class));
	}

}
