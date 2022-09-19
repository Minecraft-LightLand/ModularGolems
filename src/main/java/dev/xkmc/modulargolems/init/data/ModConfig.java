package dev.xkmc.modulargolems.init.data;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {

	public static class Common {

		public final ForgeConfigSpec.DoubleValue thorn;
		public final ForgeConfigSpec.DoubleValue fiery;
		public final ForgeConfigSpec.DoubleValue magicResistance;
		public final ForgeConfigSpec.DoubleValue compatTFHealing;
		public final ForgeConfigSpec.DoubleValue compatTFDamage;
		public final ForgeConfigSpec.IntValue carminiteTime;
		public final ForgeConfigSpec.DoubleValue coating;

		Common(ForgeConfigSpec.Builder builder) {
			thorn = builder.comment("Percentage damage reflection per level of thorn")
					.defineInRange("thorn", 0.2, 0, 100);
			fiery = builder.comment("Percentage damage addition per level of fiery")
					.defineInRange("fiery", 0.5, 0, 100);
			magicResistance = builder.comment("Percentage damage reduction per level of magic resistance")
					.defineInRange("magicResistance", 0.2, 0, 1);
			compatTFHealing = builder.comment("Percentage healing bonus per level of twilight healing")
					.defineInRange("compatTFHealing", 0.5, 0, 100);
			compatTFDamage = builder.comment("Percentage damage bonus per level of twilight damage")
					.defineInRange("compatTFDamage", 0.2, 0, 100);
			carminiteTime = builder.comment("Time for the golem to be invincible (in ticks) per level of carminite")
					.defineInRange("carminiteTime", 29, 1, 100000);
			coating = builder.comment("Percentage damage absorption applied per level of coating")
					.defineInRange("coating", 0.2, 0, 1);
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
