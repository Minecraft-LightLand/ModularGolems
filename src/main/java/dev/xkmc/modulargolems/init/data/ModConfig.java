package dev.xkmc.modulargolems.init.data;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {

	public static class Common {

		public final ForgeConfigSpec.DoubleValue thorn;
		public final ForgeConfigSpec.DoubleValue fiery;

		Common(ForgeConfigSpec.Builder builder) {
			thorn = builder.comment("Percentage damage reflection per level of thorn")
					.defineInRange("thorn", 0.2, 0, 100);
			fiery = builder.comment("Percentage damage addition per level of fiery")
					.defineInRange("fiery", 0.5, 0, 100);
		}

	}

	public static final ForgeConfigSpec COMMON_SPEC;
	public static final Common COMMON;

	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	/**
	 * Registers any relevant listeners for config
	 */
	public static void init() {
		ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.COMMON_SPEC);
	}


}
