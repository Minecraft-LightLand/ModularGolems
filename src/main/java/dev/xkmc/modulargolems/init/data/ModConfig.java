package dev.xkmc.modulargolems.init.data;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {

	public static class Common {

		public final ForgeConfigSpec.DoubleValue thorn;
		public final ForgeConfigSpec.DoubleValue fiery;
		public final ForgeConfigSpec.DoubleValue magicResistance;
		public final ForgeConfigSpec.DoubleValue explosionResistance;
		public final ForgeConfigSpec.DoubleValue compatTFHealing;
		public final ForgeConfigSpec.DoubleValue compatTFDamage;
		public final ForgeConfigSpec.IntValue carminiteTime;
		public final ForgeConfigSpec.DoubleValue coating;
		public final ForgeConfigSpec.BooleanValue barehandRetrieve;
		public final ForgeConfigSpec.DoubleValue damageCap;
		public final ForgeConfigSpec.IntValue conduitCooldown;
		public final ForgeConfigSpec.IntValue conduitDamage;
		public final ForgeConfigSpec.DoubleValue conduitBoostAttack;
		public final ForgeConfigSpec.IntValue conduitBoostArmor;
		public final ForgeConfigSpec.IntValue conduitBoostToughness;
		public final ForgeConfigSpec.DoubleValue conduitBoostReduction;
		public final ForgeConfigSpec.DoubleValue conduitBoostSpeed;
		public final ForgeConfigSpec.IntValue thunderHeal;
		public final ForgeConfigSpec.IntValue teleportRadius;
		public final ForgeConfigSpec.BooleanValue exponentialStat;

		Common(ForgeConfigSpec.Builder builder) {
			barehandRetrieve = builder.comment("Allow players to retrieve the golems by bare hand")
					.define("barehandRetrieve", true);
			exponentialStat = builder.comment("Percentage modifiers stack exponentially")
					.define("exponentialStat", false);

			builder.push("modifiers");
			thorn = builder.comment("Percentage damage reflection per level of thorn")
					.defineInRange("thorn", 0.2, 0, 100);
			magicResistance = builder.comment("Percentage damage reduction per level of magic resistance")
					.defineInRange("magicResistance", 0.2, 0, 1);
			explosionResistance = builder.comment("Percentage damage reduction per level of explosion resistance")
					.defineInRange("explosionResistance", 0.2, 0, 1);
			damageCap = builder.comment("Damage Cap at max level")
					.defineInRange("damageCap", 0.1d, 0, 1);
			thunderHeal = builder.comment("Healing when thunder immune golems are striked")
					.defineInRange("thunderHeal", 10, 1, 10000);
			builder.pop();

			builder.push("twilight forest compat");
			fiery = builder.comment("Percentage damage addition per level of fiery")
					.defineInRange("fiery", 0.5, 0, 100);
			compatTFHealing = builder.comment("Percentage healing bonus per level of twilight healing")
					.defineInRange("compatTFHealing", 0.5, 0, 100);
			compatTFDamage = builder.comment("Percentage damage bonus per level of twilight damage")
					.defineInRange("compatTFDamage", 0.2, 0, 100);
			carminiteTime = builder.comment("Time for the golem to be invincible (in ticks) per level of carminite")
					.defineInRange("carminiteTime", 20, 1, 100000);
			builder.pop();

			builder.push("create compat");
			coating = builder.comment("Damage absorbed per level of coating")
					.defineInRange("coating", 1d, 0, 10000);
			builder.pop();

			builder.push("l2complements compat");
			conduitCooldown = builder.comment("Conduit cooldown")
					.defineInRange("conduitCooldown", 120, 0, 10000);
			conduitDamage = builder.comment("Conduit damage")
					.defineInRange("conduitDamage", 2, 0, 10000);
			conduitBoostAttack = builder.comment("Conduit modifier boosts attack (percentage) when golem is in water")
					.defineInRange("conduitBoostAttack", 0.2, 0, 100);
			conduitBoostArmor = builder.comment("Conduit modifier boosts armor when golem is in water")
					.defineInRange("conduitBoostArmor", 5, 0, 100);
			conduitBoostToughness = builder.comment("Conduit modifier boosts toughness when golem is in water")
					.defineInRange("conduitBoostToughness", 2, 0, 100);
			conduitBoostSpeed = builder.comment("Conduit modifier boosts speed (percentage) when golem is in water")
					.defineInRange("conduitBoostSpeed", 0.2, 0, 100);
			conduitBoostReduction = builder.comment("Conduit modifier reduce damage taken when golem is in water (multiplicative)")
					.defineInRange("conduitBoostReduction", 0.2, 0, 100);
			teleportRadius = builder.comment("Teleport max radius")
					.defineInRange("teleportRadius", 6, 1, 32);
			builder.pop();
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
