package dev.xkmc.modulargolems.init.data;

import dev.xkmc.l2core.util.ConfigInit;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.neoforged.neoforge.common.ModConfigSpec;

public class MGConfig {

	public static class Client extends ConfigInit {

		Client(Builder builder) {
			markL2();
		}

	}

	public static class Common extends ConfigInit {

		public final ModConfigSpec.DoubleValue thorn;
		public final ModConfigSpec.DoubleValue fieryDamageFactor;
		public final ModConfigSpec.DoubleValue magicResistance;
		public final ModConfigSpec.DoubleValue explosionResistance;
		public final ModConfigSpec.DoubleValue compatTFHealing;
		public final ModConfigSpec.DoubleValue compatTFDamage;
		public final ModConfigSpec.IntValue manaMendingCost;
		public final ModConfigSpec.DoubleValue manaMendingVal;
		public final ModConfigSpec.DoubleValue manaBoostingDamage;
		public final ModConfigSpec.IntValue manaBoostingCost;
		public final ModConfigSpec.IntValue manaProductionVal;
		public final ModConfigSpec.DoubleValue manaBurstDamage;
		public final ModConfigSpec.IntValue manaBurstCost;
		public final ModConfigSpec.DoubleValue pixieAttackProb;
		public final ModConfigSpec.DoubleValue pixieCounterattackProb;
		public final ModConfigSpec.IntValue carminiteTime;
		public final ModConfigSpec.DoubleValue coating;
		public final ModConfigSpec.DoubleValue mechSpeed;
		public final ModConfigSpec.DoubleValue mechAttack;
		public final ModConfigSpec.IntValue mechMaxFuel;
		public final ModConfigSpec.BooleanValue barehandRetrieve;
		public final ModConfigSpec.BooleanValue strictInteract;
		public final ModConfigSpec.DoubleValue damageCap;
		public final ModConfigSpec.IntValue conduitCooldown;
		public final ModConfigSpec.IntValue conduitDamage;
		public final ModConfigSpec.DoubleValue conduitBoostAttack;
		public final ModConfigSpec.IntValue conduitBoostArmor;
		public final ModConfigSpec.IntValue conduitBoostToughness;
		public final ModConfigSpec.DoubleValue conduitBoostReduction;
		public final ModConfigSpec.DoubleValue conduitBoostSpeed;
		public final ModConfigSpec.IntValue thunderHeal;
		public final ModConfigSpec.IntValue teleportRadius;
		public final ModConfigSpec.IntValue teleportCooldown;
		public final ModConfigSpec.DoubleValue targetDamageBonus;
		public final ModConfigSpec.IntValue basePickupRange;
		public final ModConfigSpec.IntValue mendingXpCost;
		public final ModConfigSpec.DoubleValue armorBypassChance;
		public final ModConfigSpec.DoubleValue slicingDropUpgradeChance;
		public final ModConfigSpec.DoubleValue beaconCannonDamageFactor;
		public final ModConfigSpec.DoubleValue sonicCannonDamageFactor;
		public final ModConfigSpec.DoubleValue sonicCannonResonanceBonus;
		public final ModConfigSpec.DoubleValue mechanicalArmSpeed;
		public final ModConfigSpec.DoubleValue mechanicalArmPowerBonus;
		public final ModConfigSpec.DoubleValue mechanicalArmMiscBonusFactor;


		public final ModConfigSpec.IntValue summonDistance;
		public final ModConfigSpec.IntValue retrieveDistance;
		public final ModConfigSpec.IntValue retrieveRange;
		public final ModConfigSpec.BooleanValue ownerPickupOnly;
		public final ModConfigSpec.BooleanValue allowEditCuriosForOthers;
		public final ModConfigSpec.DoubleValue startFollowRadius;
		public final ModConfigSpec.DoubleValue stopWanderRadius;
		public final ModConfigSpec.DoubleValue maxWanderRadius;
		public final ModConfigSpec.DoubleValue riddenSpeedFactor;

		public final ModConfigSpec.IntValue targetResetTime;
		public final ModConfigSpec.DoubleValue targetResetNoMovementRange;

		public final ModConfigSpec.IntValue dogGolemSlot;
		public final ModConfigSpec.IntValue humanoidGolemSlot;
		public final ModConfigSpec.IntValue largeGolemSlot;
		public final ModConfigSpec.BooleanValue doEnemyAggro;
		public final ModConfigSpec.BooleanValue allowDimensionChange;

		public final ModConfigSpec.DoubleValue ignitiumHealRate;
		public final ModConfigSpec.DoubleValue ignisSkillDamageFactor;
		public final ModConfigSpec.DoubleValue fireballArmorBonus;
		public final ModConfigSpec.DoubleValue flameStrikeArmorBonus;
		public final ModConfigSpec.DoubleValue laserArmorBonus;
		public final ModConfigSpec.DoubleValue missileArmorBonus;
		public final ModConfigSpec.DoubleValue earthquakeArmorBonus;
		public final ModConfigSpec.DoubleValue sandCurseBonus;
		public final ModConfigSpec.BooleanValue wandBypassConfig;
		public final ModConfigSpec.BooleanValue sendForceRemovalMessage;

		Common(Builder builder) {
			markL2();
			builder.push("general", "General Configurations");
			{
				barehandRetrieve = builder.text("Allow players to retrieve the golems by bare hand")
						.define("barehandRetrieve", true);
				doEnemyAggro = builder.text("Copy Iron Golem Aggro")
						.comment("Make mobs aggro to iron golem automatically aggro to modular golems")
						.define("doEnemyAggro", true);
				allowDimensionChange = builder.text("Allow golems to enter portal")
						.define("allowDimensionChange", false);
				summonDistance = builder.text("Max distance to summon single golem")
						.defineInRange("summonDistance", 64, 1, 1000);
				retrieveDistance = builder.text("Max distance to retrieve single golem")
						.defineInRange("retrieveDistance", 64, 1, 1000);
				retrieveRange = builder.text("Max distance to retrieve all golems")
						.defineInRange("retrieveRange", 20, 1, 1000);
				ownerPickupOnly = builder.text("Only owner can pickup")
						.define("ownerPickupOnly", true);
				strictInteract = builder.text("Restrict Interaction")
						.comment("When enabled, the following features will be disabled when player holds item in hand:")
						.comment("- give item to golem or take items from golem by right click").comment("- order dog golem to seat")
						.define("strictInteract", false);
				wandBypassConfig = builder.text("Wand interaction bypass config lock")
						.define("wandBypassConfig", false);
				allowEditCuriosForOthers = builder.text("Allow command wand to edit curios of other mobs you own")
						.define("allowEditCuriosForOthers", true);
				startFollowRadius = builder.text("Start following radius")
						.comment("Max golem activity radius before following player in follow mode")
						.defineInRange("startFollowRadius", 10d, 1, 100);
				stopWanderRadius = builder.text("Wandering radius")
						.comment("Max golem activity radius before willing to go back to origin in wander mode")
						.defineInRange("stopWanderRadius", 20d, 1, 100);
				maxWanderRadius = builder.text("Max golem activity radius before being teleported back")
						.defineInRange("maxWanderRadius", 30d, 1, 100);
				riddenSpeedFactor = builder.text("Speed factor for dog golem when ridden by entities")
						.defineInRange("riddenSpeedFactor", 0.8, 0, 2);
				targetResetTime = builder.text("Target reset time after no movement, in ticks")
						.defineInRange("targetResetTime", 600, 1, 10000);
				targetResetNoMovementRange = builder.text("Distance considered as no movement")
						.defineInRange("targetResetNoMovementRange", 0.5, 0, 10);
				slicingDropUpgradeChance = builder.text("Chance for each upgrade to drop when killed by Slicing Axe")
						.defineInRange("slicingDropUpgradeChance", 0.5, 0, 1);
				beaconCannonDamageFactor = builder.text("Damage of Beacon Cannon as percentage of golem melee damage")
						.defineInRange("beaconCannonDamageFactor", 0.5, 0, 1);
				sonicCannonDamageFactor = builder.text("Damage of Sonic Cannon as percentage of golem melee damage")
						.defineInRange("sonicCannonDamageFactor", 0.25, 0, 1);
				sonicCannonResonanceBonus = builder.text("Bonus damage of Sonic Cannon per sculk part, as percentage of golem melee damage")
						.defineInRange("sonicCannonResonanceBonus", 0.1, 0, 1);
				sendForceRemovalMessage = builder.comment("Send force removal message")
						.define("sendForceRemovalMessage", true);

				largeGolemSlot = builder.text("Default slots for large golem")
						.defineInRange("largeGolemSlot", 4, 0, 100);
				humanoidGolemSlot = builder.text("Default slots for humanoid golem")
						.defineInRange("humanoidGolemSlot", 3, 0, 100);
				dogGolemSlot = builder.text("Default slots for dog golem")
						.defineInRange("dogGolemSlot", 2, 0, 100);
			}
			builder.pop();

			// modifiers
			{
				builder.push("modifiers", "Golem Modifier Properties");
				{
					thorn = builder.text("Percentage damage reflection per level of thorn")
							.defineInRange("thorn", 0.2, 0, 100);
					magicResistance = builder.text("Percentage damage reduction per level of magic resistance")
							.defineInRange("magicResistance", 0.2, 0, 1);
					explosionResistance = builder.text("Percentage damage reduction per level of explosion resistance")
							.defineInRange("explosionResistance", 0.2, 0, 1);
					damageCap = builder.text("Damage Cap at max level")
							.defineInRange("damageCap", 0.1d, 0, 1);
					thunderHeal = builder.text("Healing when thunder immune golems are striked")
							.defineInRange("thunderHeal", 10, 1, 10000);
					basePickupRange = builder.text("Pickup range per level for pickup upgrade")
							.defineInRange("basePickupRange", 6, 1, 16);
					mendingXpCost = builder.text("Mending Xp Cost per health point")
							.defineInRange("mendingXpCost", 2, 1, 10000);
					armorBypassChance = builder.text("Armor Penetration modifier: chance for damage to bypass armor per level")
							.defineInRange("armorBypassChance", 0.2, 0, 1);

				}
				builder.pop();

				builder.push("twilightforest", "Twilight Forest Compat Properties");
				{
					fieryDamageFactor = builder.text("Percentage damage addition per level of fiery")
							.defineInRange("fieryDamageFactor", 0.25, 0, 100);
					compatTFHealing = builder.text("Percentage healing bonus per level of twilight healing")
							.defineInRange("compatTFHealing", 0.5, 0, 100);
					compatTFDamage = builder.text("Percentage damage bonus per level of twilight damage")
							.defineInRange("compatTFDamage", 0.2, 0, 100);
					carminiteTime = builder.text("Time for the golem to be invincible (in ticks) per level of carminite")
							.defineInRange("carminiteTime", 20, 1, 100000);
				}
				builder.pop();

				builder.push("botania", "Botania Compat Properties");
				{
					manaMendingCost = builder.text("Mana mending cost per HP")
							.defineInRange("mendingManaCost", 200, 1, 50000);
					manaMendingVal = builder.text("Mana mending value per level")
							.defineInRange("manaMendingVal", 4D, 0.1D, 100D);
					manaBoostingDamage = builder.text("Percentage damage bonus per level of mana boost")
							.defineInRange("manaBoostingDamage", 0.2, 0, 100);
					manaBoostingCost = builder.text("Mana boosting cost per level (of 1 hit)")
							.defineInRange("manaBoostingCost", 500, 0, 100000);
					manaProductionVal = builder.text("Mana production value per level")
							.defineInRange("manaProductionVal", 20, 0, 100000);
					manaBurstDamage = builder.text("Damage of magic burst as percentage of attack damage")
							.defineInRange("manaBurstDamage", 0.25, 0, 1.0);
					manaBurstCost = builder.text("Mana burst cost per level")
							.defineInRange("manaBurstCost", 2000, 0, 100000);
					pixieAttackProb = builder.text("Probability of summoning a pixie when attacking")
							.defineInRange("pixieAttackProb", 0.05, 0, 1.0);
					pixieCounterattackProb = builder.text("Probability of summoning a pixie when attacked")
							.defineInRange("pixiecounterattackProb", 0.25, 0, 1.0);
				}
				builder.pop();

				builder.push("create", "Create Compat Properties");
				{
					coating = builder.text("Damage absorbed per level of coating")
							.defineInRange("coating", 1d, 0, 10000);
					mechSpeed = builder.text("Speed boost per level of Mechanical Mobility")
							.defineInRange("mechSpeed", 0.2, 0, 1);
					mechAttack = builder.text("Attack boost per level of Mechanical Force")
							.defineInRange("mechAttack", 0.2, 0, 1);
					mechMaxFuel = builder.text("Maximum number of fuel item that can be added at the same time")
							.defineInRange("mechMaxFuel", 3, 1, 364);
					mechanicalArmSpeed = builder.text("Mechanical Arm: default movement speed")
							.comment("Animation at speed of 1 is 5 seconds. At speed of 0.5, it takes the arm 10 seconds to perform a fix.")
							.defineInRange("mechanicalArmSpeed", 0.5, 0.1, 4);
					mechanicalArmPowerBonus = builder.text("Mechanical Arm: bonus speed when golem is fueled up")
							.comment("At default speed of 0.5 and bonus of 1, golems fix itself 3 times faster when powered.")
							.defineInRange("mechanicalArmPowerBonus", 1d, 0, 4);
					mechanicalArmMiscBonusFactor = builder.text("Mechanical Arm: bonus speed factor for other create golem effects")
							.comment("- Mechanical Engine Modifier: +0.2, making Create golem fix itself faster than other golems even when unpowered.")
							.comment("- Mechanical Mobility Effect: +0.2 per level, effectively another power bonus")
							.comment("- Mechanical Force Effect: +0.1 per level, effectively another power bonus")
							.comment("This config value puts a factor on above bonuses.")
							.defineInRange("mechanicalArmMiscBonusFactor", 1d, 0, 10);
				}
				builder.pop();

				builder.push("l2complements", "L2Complements Compat Properties");
				{
					conduitCooldown = builder.text("Conduit cooldown")
							.defineInRange("conduitCooldown", 120, 0, 10000);
					conduitDamage = builder.text("Conduit damage")
							.defineInRange("conduitDamage", 2, 0, 10000);
					conduitBoostAttack = builder.text("Conduit modifier boosts attack (percentage) when golem is in water")
							.defineInRange("conduitBoostAttack", 0.2, 0, 100);
					conduitBoostArmor = builder.text("Conduit modifier boosts armor when golem is in water")
							.defineInRange("conduitBoostArmor", 5, 0, 100);
					conduitBoostToughness = builder.text("Conduit modifier boosts toughness when golem is in water")
							.defineInRange("conduitBoostToughness", 2, 0, 100);
					conduitBoostSpeed = builder.text("Conduit modifier boosts speed (percentage) when golem is in water")
							.defineInRange("conduitBoostSpeed", 0.2, 0, 100);
					conduitBoostReduction = builder.text("Conduit modifier reduce damage taken when golem is in water (multiplicative)")
							.defineInRange("conduitBoostReduction", 0.2, 0, 100);
					teleportRadius = builder.text("Teleport max radius")
							.defineInRange("teleportRadius", 6, 1, 32);
					teleportCooldown = builder.text("Teleport cooldown in ticks for avoiding physical damage")
							.defineInRange("teleportCooldown", 40, 1, 10000);
					targetDamageBonus = builder.text("Damage bonus for attacking specific type of enemy")
							.defineInRange("targetDamageBonus", 0.5, 0, 100);
				}
				builder.pop();

				builder.push("cataclysm", "Cataclysm Compat");
				{
					ignitiumHealRate = builder.text("Heal rate as percentage of damage dealt per level of blazing brand")
							.defineInRange("ignitiumHealRate", 0.1d, 0, 10);
					ignisSkillDamageFactor = builder.text("Ignitium golem skill damage factor")
							.defineInRange("ignisSkillDamageFactor", 0.5d, 0, 1);
					fireballArmorBonus = builder.text("Ignis Helmet Bonus: Fireball damage boost")
							.defineInRange("fireballArmorBonus", 0.5, 0, 10);
					flameStrikeArmorBonus = builder.text("Ignis Shinguard Bonus: Flame Strike damage boost")
							.defineInRange("flameStrikeArmorBonus", 0.5, 0, 10);
					laserArmorBonus = builder.text("Harbinger Helmet Bonus: Death Laser damage boost")
							.defineInRange("laserArmorBonus", 1d, 0, 10);
					missileArmorBonus = builder.text("Harbinger Chestplate Bonus: Homing Missile damage boost")
							.defineInRange("missileArmorBonus", 1d, 0, 10);
					earthquakeArmorBonus = builder.text("Monstrosity Armor Bonus: Monstrosity Earthquake Modifier damage boost")
							.defineInRange("earthquakeArmorBonus", 0.5d, 0, 10);
					sandCurseBonus = builder.text("Sandstorm Upgrade damage bonus per level")
							.defineInRange("sandCurseBonus", 0.1d, 0, 10);
				}
				builder.pop();
			}
		}

	}

	public static final Client CLIENT = ModularGolems.REGISTRATE.registerClient(Client::new);
	public static final Common COMMON = ModularGolems.REGISTRATE.registerSynced(Common::new);

	public static void init() {
	}

}
