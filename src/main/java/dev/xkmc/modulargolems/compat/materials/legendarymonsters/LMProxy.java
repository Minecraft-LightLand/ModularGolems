package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.loot.MGGLMGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.miauczel.legendary_monsters.entity.ModEntities;
import net.miauczel.legendary_monsters.item.ModItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LMProxy {

	public static void shake(LivingEntity user, Vec3 pos) {
		try {
			LMProxyImpl.shake(user, pos);
		} catch (Throwable ignored) {

		}
	}

	public static void anchorParticle(ServerLevel level, double x, double y, double z, float reach) {
		try {
			LMProxyImpl.anchorParticle(level, x, y, z, reach);
		} catch (Throwable ignored) {

		}
	}

	public static List<LivingEntity> stun(ServerLevel level, double x, double y, double z, LivingEntity golem, float reach, int lv) {
		try {
			return LMProxyImpl.stun(level, x, y, z, golem, reach, lv);
		} catch (Throwable ignored) {
			return new ArrayList<>();
		}
	}

	public static void spawnObliteratorLargeBomb(LivingEntity golem, LivingEntity target, int lv) {
		try {
			LMProxyImpl.spawnObliteratorLargeBomb(golem, target, lv);
		} catch (Throwable ignored) {
		}
	}

		@Nullable
	public static Entity spawnObliteratorLaser(LivingEntity golem, LivingEntity target, int lv, float castYaw) {
		try {
			return LMProxyImpl.spawnObliteratorLaser(golem, target, lv, castYaw);
		} catch (Throwable ignored) {
			return null;
		}
	}

	public static float roarSmashLeap(LivingEntity golem) {
		try {
			return LMProxyImpl.roarSmashLeap(golem);
		} catch (Throwable ignored) {
			return golem.yBodyRot;
		}
	}

	public static void spawnObliteratorRoarSmashLanding(LivingEntity golem, int lv, float castYaw) {
		try {
			LMProxyImpl.spawnObliteratorRoarSmashLanding(golem, lv, castYaw);
		} catch (Throwable ignored) {
		}
	}

	public static void spawnObliteratorUltimateQuake(LivingEntity golem, int lv) {
		try {
			LMProxyImpl.spawnObliteratorUltimateQuake(golem, lv);
		} catch (Throwable ignored) {
		}
	}

	public static void spawnPhantomDaggers(LivingEntity golem, LivingEntity target, int lv) {
		try {
			LMProxyImpl.spawnPhantomDaggers(golem, target, lv);
		} catch (Throwable ignored) {
		}
	}

	public static void spawnPaladinLeapBlades(LivingEntity golem,int lv) {
		try {
			LMProxyImpl.spawnPaladinLeapBlades(golem, lv);
		} catch (Throwable ignored) {
		}
	}

	public static void spawnCloudGolemCloud(double x, double y, double z, net.minecraft.world.level.Level level) {
		try {
			LMProxyImpl.spawnCloudGolemCloud(x, y, z, level);
		} catch (Throwable ignored) {
		}
	}

	public static void spawnCloudGolemBurst(LivingEntity golem, double x, double y, double z, float damage, int n) {
		try {
			LMProxyImpl.spawnCloudGolemBurst(golem, x, y, z, damage, n);
		} catch (Throwable ignored) {
		}
	}

	public static void spawnCloudGolemThunderRow(LivingEntity golem, LivingEntity target, float angleOffset, float dmg) {
		try {
			LMProxyImpl.spawnCloudGolemThunderRow(golem, target, angleOffset, dmg);
		} catch (Throwable ignored) {
		}
	}

	public static void spawnCloudGolemWaves(LivingEntity golem, int count, float angleStep, float damage, float life, double yOffsetRatio, double cx, double cz, float yawDeg) {
		try {
			LMProxyImpl.spawnCloudGolemWaves(golem, count, angleStep, damage, life, yOffsetRatio, cx, cz, yawDeg);
		} catch (Throwable ignored) {
		}
	}

	public static void cloudStompShake(LivingEntity golem) {
		try {
			LMProxyImpl.cloudStompShake(golem);
		} catch (Throwable ignored) {
		}
	}

	public static void cloudSmashGroundFx(LivingEntity golem, double x, double y, double z) {
		try {
			LMProxyImpl.cloudSmashGroundFx(golem, x, y, z);
		} catch (Throwable ignored) {
		}
	}

	public static void cloudSmashParticles(LivingEntity golem) {
		try {
			LMProxyImpl.cloudSmashParticles(golem);
		} catch (Throwable ignored) {
		}
	}

	public static void spawnCloudGolemTornado(LivingEntity golem, float yaw, float damage, int life) {
		try {
			LMProxyImpl.spawnCloudGolemTornado(golem, yaw, damage, life);
		} catch (Throwable ignored) {
		}
	}

	public static void genRecipe(RegistrateRecipeProvider pvd) {

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LMCompatRegistry.CLOUD_CUBE, 9)::unlockedBy,
						ModItems.AIR_RUNE.get())
				.pattern("RIR").pattern("IXI").pattern("RIR")
				.define('I', GolemItems.GOLEM_TEMPLATE)
				.define('R', ModItems.CLOUD_ROD.get())
				.define('X', Ingredient.of(ModItems.AIR_RUNE.get(), ModItems.ATMOSPHERIC_BOOTS.get()))
				.save(ConditionalRecipeWrapper.mod(pvd, LMDispatch.MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LMCompatRegistry.ANNIHILATION_CUBE, 9)::unlockedBy,
						ModItems.PORTAL_SHARD.get())
				.pattern("RIR").pattern("GXG").pattern("RIR")
				.define('X', GolemItems.GOLEM_TEMPLATE)
				.define('R', ModItems.ENDIRITIUM_GEM.get())
				.define('I', ModItems.PORTAL_SHARD.get())
				.define('G', ModItems.EYE_CRYSTAL.get())
				.save(ConditionalRecipeWrapper.mod(pvd, LMDispatch.MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LMCompatRegistry.POSESSED_SOUL_CUBE, 9)::unlockedBy,
						ModItems.CORRUPTED_SOUL.get())
				.pattern("RIR").pattern("IXI").pattern("RIR")
				.define('I', GolemItems.GOLEM_TEMPLATE)
				.define('R', ModItems.METAL_DEBRIS.get())
				.define('X', ModItems.CORRUPTED_SOUL.get())
				.save(ConditionalRecipeWrapper.mod(pvd, LMDispatch.MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LMCompatRegistry.UPGRADE_ANNIHILATION_BOMB.get())::unlockedBy,
						ModItems.BOTTLE_OF_ANNIHILATION.get())
				.pattern(" X ").pattern("ROR").pattern(" R ")
				.define('R', ModItems.PORTAL_SHARD.get())
				.define('X', ModItems.BOTTLE_OF_ANNIHILATION.get())
				.define('O', GolemItems.EMPTY_UPGRADE)
				.save(ConditionalRecipeWrapper.mod(pvd, LMDispatch.MODID));

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LMCompatRegistry.UPGRADE_THUNDER.get())::unlockedBy,
						ModItems.AIR_RUNE.get())
				.pattern(" X ").pattern("ROR").pattern(" R ")
				.define('R', ModItems.CLOUD_ROD.get())
				.define('X', ModItems.AIR_RUNE.get())
				.define('O', GolemItems.EMPTY_UPGRADE)
				.save(ConditionalRecipeWrapper.mod(pvd, LMDispatch.MODID));


	}

	public static void genLootModifier(MGGLMGen pvd) {
		pvd.drop(LMDispatch.MODID, ModEntities.Ancient_Guardian.get(), "molten_metal");
		pvd.drop(LMDispatch.MODID, ModEntities.Cloud_golem.get(), "cloud");
		pvd.drop(LMDispatch.MODID, ModEntities.THE_OBLITERATOR.get(), "obliterator");
		pvd.drop(LMDispatch.MODID, ModEntities.Posessed_Paladin.get(), "paladin");
	}

	/** Boss 本体攻击力（6.0），用于把固定伤害按傀儡攻击力等比缩放。 */
	public static float bossDamage(LivingEntity golem, int lv, float bossValue) {
		try {
			return LMProxyImpl.bossDamage(golem, lv, bossValue);
		} catch (Throwable ignored) {
			return 0.0f;
		}
	}

	public static List<LivingEntity> areaAttack(LivingEntity golem, float range, float arcDeg, float damage, float launch) {
		try {
			return LMProxyImpl.areaAttack(golem, range, arcDeg, damage, launch);
		} catch (Throwable ignored) {
			return new ArrayList<>();
		}
	}

	public static void sendRing(LivingEntity golem, float scale, int segments, boolean shrink, double yOff) {
		try {
			LMProxyImpl.sendRing(golem, scale, segments, shrink, yOff);
		} catch (Throwable ignored) {
		}
	}

	public static void obliteratorBombCharge(LivingEntity golem) {
		try {
			LMProxyImpl.obliteratorBombCharge(golem);
		} catch (Throwable ignored) {
		}
	}

	public static void spawnObliteratorSmallBomb(LivingEntity golem, LivingEntity target, int lv, float lateral) {
		try {
			LMProxyImpl.spawnObliteratorSmallBomb(golem, target, lv, lateral);
		} catch (Throwable ignored) {
		}
	}

	public static void obliteratorSmallBombFire(LivingEntity golem, LivingEntity target, int lv) {
		try {
			LMProxyImpl.obliteratorSmallBombFire(golem, target, lv);
		} catch (Throwable ignored) {
		}
	}

	public static void obliteratorBombFire(LivingEntity golem, LivingEntity target, int lv) {
		try {
			LMProxyImpl.obliteratorBombFire(golem, target, lv);
		} catch (Throwable ignored) {
		}
	}

		public static void obliteratorLaserCharge(LivingEntity golem) {
		try {
			LMProxyImpl.obliteratorLaserCharge(golem);
		} catch (Throwable ignored) {
		}
	}

	public static void obliteratorLaserChargeParticles(LivingEntity golem) {
		try {
			LMProxyImpl.obliteratorLaserChargeParticles(golem);
		} catch (Throwable ignored) {
		}
	}

	@Nullable
	public static Entity obliteratorLaserFire(LivingEntity golem, LivingEntity target, int lv, float castYaw) {
		try {
			return LMProxyImpl.obliteratorLaserFire(golem, target, lv, castYaw);
		} catch (Throwable ignored) {
			return null;
		}
	}

	public static void paladinDaggerSound(LivingEntity golem) {
		try {
			LMProxyImpl.paladinDaggerSound(golem);
		} catch (Throwable ignored) {
		}
	}


	public static void lmSmashParticles(LivingEntity golem, float vec, float offset, float additionalY, float power, float r) {
		try {
			LMProxyImpl.lmSmashParticles(golem, vec, offset, additionalY, power, r);
		} catch (Throwable ignored) {
		}
	}

	public static void obliteratorTeleportSound(LivingEntity golem) {
		try {
			LMProxyImpl.obliteratorTeleportSound(golem);
		} catch (Throwable ignored) {
		}
	}

	public static void obliteratorTeleportBehind(LivingEntity golem, double back) {
		try {
			LMProxyImpl.obliteratorTeleportBehind(golem, back);
		} catch (Throwable ignored) {
		}
	}
	public static void teleportFxAt(net.minecraft.world.level.Level level, double x, double y, double z) {
		try {
			LMProxyImpl.teleportFxAt(level, x, y, z);
		} catch (Throwable ignored) {
		}
	}

	public static void spawnCloneBarrage(LivingEntity golem, LivingEntity target, int lv, boolean secondWave) {
		try {
			LMProxyImpl.spawnCloneBarrage(golem, target, lv, secondWave);
		} catch (Throwable ignored) {
		}
	}

	public static void cloneBarrageStart(LivingEntity golem) {
		try {
			LMProxyImpl.cloneBarrageStart(golem);
		} catch (Throwable ignored) {
		}
	}

	public static void spawnQuadLaser(LivingEntity golem, int lv, boolean right, float castYaw) {
		try {
			LMProxyImpl.spawnQuadLaser(golem, lv, right, castYaw);
		} catch (Throwable ignored) {
		}
	}

	public static void quadLaserCharge(LivingEntity golem) {
		try {
			LMProxyImpl.quadLaserCharge(golem);
		} catch (Throwable ignored) {
		}
	}

	public static void spawnCloneBurst(LivingEntity golem, LivingEntity target, int lv) {
		try {
			LMProxyImpl.spawnCloneBurst(golem, target, lv);
		} catch (Throwable ignored) {
		}
	}

	public static void cloneBurstFinish(LivingEntity golem, int lv) {
		try {
			LMProxyImpl.cloneBurstFinish(golem, lv);
		} catch (Throwable ignored) {
		}
	}

	public static boolean cloneBurstChargeHit(LivingEntity golem, int lv, net.minecraft.world.phys.Vec3 dir) {
		try {
			return LMProxyImpl.cloneBurstChargeHit(golem, lv, dir);
		} catch (Throwable ignored) {
			return false;
		}
	}

	public static void obliteratorPull(LivingEntity golem) {
		try {
			LMProxyImpl.obliteratorPull(golem);
		} catch (Throwable ignored) {
		}
	}

	public static void obliteratorNukeRing(LivingEntity golem, int lv, int distance, boolean warning, double cx, double cz, float castYaw) {
		try {
			LMProxyImpl.obliteratorNukeRing(golem, lv, distance, warning, cx, cz, castYaw);
		} catch (Throwable ignored) {
		}
	}


	public static void backstepStompRing(LivingEntity golem, int lv, int distance, boolean first, float castYaw, double cx, double cz) {
		try {
			LMProxyImpl.backstepStompRing(golem, lv, distance, first, castYaw, cx, cz);
		} catch (Throwable ignored) {
		}
	}


	public static void obliteratorUltimateHit1(LivingEntity golem, int lv) {
		try {
			LMProxyImpl.obliteratorUltimateHit1(golem, lv);
		} catch (Throwable ignored) {
		}
	}

	public static void obliteratorUltimateHit2(LivingEntity golem, int lv) {
		try {
			LMProxyImpl.obliteratorUltimateHit2(golem, lv);
		} catch (Throwable ignored) {
		}
	}
	


	public static void paladinBlockFx(LivingEntity golem) {
		try { LMProxyImpl.paladinBlockFx(golem); } catch (Throwable ignored) { }
	}

	public static void paladinSlamLanding(LivingEntity golem, int lv) {
		try { LMProxyImpl.paladinSlamLanding(golem, lv); } catch (Throwable ignored) { }
	}

	public static void paladinSlamSecond(LivingEntity golem, int lv) {
		try { LMProxyImpl.paladinSlamSecond(golem, lv); } catch (Throwable ignored) { }
	}

	public static void paladinSoulShieldWaves(LivingEntity golem, int lv) {
		try { LMProxyImpl.paladinSoulShieldWaves(golem, lv); } catch (Throwable ignored) { }
	}

	public static void paladinCounterAttack(LivingEntity golem, LivingEntity target, int lv) {
		try { LMProxyImpl.paladinCounterAttack(golem, target, lv); } catch (Throwable ignored) { }
	}

	public static void paladinTridentWave(LivingEntity golem, int lv, int distance, double cx, double cz, float yawDeg) {
		try { LMProxyImpl.paladinTridentWave(golem, lv, distance, cx, cz, yawDeg); } catch (Throwable ignored) { }
	}

	public static void paladinRollStart(LivingEntity golem) {
		try { LMProxyImpl.paladinRollStart(golem); } catch (Throwable ignored) { }
	}

	public static void paladinRollPillar(LivingEntity golem, int lv, float castYaw) {
		try { LMProxyImpl.paladinRollPillar(golem, lv, castYaw); } catch (Throwable ignored) { }
	}

	public static void paladinRollSlash(LivingEntity golem, int lv) {
		try { LMProxyImpl.paladinRollSlash(golem, lv); } catch (Throwable ignored) { }
	}

	public static void paladinThrowTrident(LivingEntity golem, LivingEntity target, int lv) {
		try { LMProxyImpl.paladinThrowTrident(golem, target, lv); } catch (Throwable ignored) { }
	}

	public static void paladinSoulShockwave(LivingEntity golem, int lv) {
		try { LMProxyImpl.paladinSoulShockwave(golem, lv); } catch (Throwable ignored) { }
	}

	public static void paladinDaggerVolley(LivingEntity golem, LivingEntity target, int lv) {
		try { LMProxyImpl.paladinDaggerVolley(golem, target, lv); } catch (Throwable ignored) { }
	}

	public static void cloudBlockFx(LivingEntity golem) {
		try { LMProxyImpl.cloudBlockFx(golem); } catch (Throwable ignored) { }
	}

	public static void spawnCloudGolemCloud(LivingEntity golem, double x, double y, double z, int damage, float attackRange) {
		try { LMProxyImpl.spawnCloudGolemCloud(golem, x, y, z, damage, attackRange); } catch (Throwable ignored) { }
	}

	public static void paladinSoulBladeHit(LivingEntity golem, LivingEntity target, int lv) {
		try {
			LMProxyImpl.paladinSoulBladeHit(golem, target, lv);
		} catch (Throwable ignored) {
		}
	}
}