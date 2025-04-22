package dev.xkmc.modulargolems.compat.misc;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraftforge.fml.loading.FMLLoader;
import vazkii.patchouli.common.base.PatchouliConfig;

public class PatchouliFlagHelper {

	public static void setFlag(String s, boolean flag) {
		try {
			PatchouliConfig.setFlag(s, flag);
		} catch (Exception e) {
			if (!FMLLoader.isProduction()) {
				ModularGolems.LOGGER.throwing(e);
			}
		}
	}

}
