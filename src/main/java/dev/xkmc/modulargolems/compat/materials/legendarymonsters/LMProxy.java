package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.CameraShakeEntity;
import net.miauczel.legendary_monsters.entity.custom.ElectricityEntity;
import net.miauczel.legendary_monsters.entity.custom.LightningBoltEntity;
import net.miauczel.legendary_monsters.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LMProxy {

	public static void shake(LivingEntity user, Vec3 pos) {
		try {
			CameraShakeEntity.cameraShake(user.level(), pos, 15.0F, 0.3F, 0, 15);
		} catch (Throwable ignored) {

		}
	}

	public static List<LivingEntity> stun(ServerLevel level, double x, double y, double z, LivingEntity golem, float reach, int lv) {
		List<LivingEntity> affected = new ArrayList<>();
		try {
			int n = 128;
			for (double i = 0; i < n; ++i) {
				var a = (Math.PI * 2D) / n * i;
				level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
						x + 0.5F + Math.cos(a) * reach,
						y,
						z + 0.5F + Math.sin(a) * reach,
						0, 0.0F, 0.05, 0.0F, 1);
			}

			level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.NEUTRAL, 1.0F, 1.0F);
			Vec3 cen = new Vec3(x, y, z);
			for (LivingEntity le : level.getEntitiesOfClass(LivingEntity.class, (new AABB(cen, cen)).inflate(reach), e -> e != golem)
					.stream().sorted(Comparator.comparingDouble(ec -> ec.distanceToSqr(cen))).toList()) {
				if (golem.isAlliedTo(le)) continue;
				le.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 20 * lv, 255, false, false));
				le.hurt(golem.damageSources().mobAttack(golem), (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE) * lv * 0.4f);
				affected.add(le);
			}
		} catch (Throwable ignored) {

		}
		return affected;
	}

	public static void performThunderAttack(Mob e, LivingEntity target, int lv) {
		try {
			spawnBoltStrip(e, target, 8 + 8 * lv, 4 + 3 * lv);
		} catch (Throwable ignored) {

		}
	}

	public static void spawnElectricShock(Entity attacker, LivingEntity entity, float damage, int n) {
		try {
			var dir = attacker.position().subtract(entity.position()).normalize();
			double val = (dir.x * dir.x + dir.z * dir.z);
			Vec3 ax0 = val < 1e-4 ? new Vec3(1, 0, 0) :
					new Vec3(-dir.x * dir.y, val, -dir.z * dir.y).normalize();
			Vec3 ax1 = dir.cross(ax0).normalize();
			for (int i = 0; i < n; i++) {
				double rad = Math.PI * 2 / n * i;
				var vec = ax1.scale(Math.sin(rad)).add(dir.scale(Math.cos(rad)));
				float angle = (float) (Math.atan2(vec.z, vec.x) * Mth.RAD_TO_DEG);
				ElectricityEntity e = new ElectricityEntity(entity, vec.x, vec.y, vec.z, entity.level(), damage, angle, 20.0F);
				Vec3 pos = entity.position().add(vec);
				e.setPos(pos.x, pos.y, pos.z);
				entity.level().addFreshEntity(e);
			}
		} catch (Throwable ignored) {

		}
	}

	private static void spawnBoltStrip(Mob e, LivingEntity target, int step, int dmg) {
		double d0 = Math.min(target.getY(), e.getY());
		double d1 = Math.max(target.getY(), e.getY()) + (double) 2.0F;
		float angle = (float) Mth.atan2(target.getZ() - e.getZ(), target.getX() - e.getX());
		for (int l = 0; l < step; ++l) {
			double len = (double) 1.25F * (double) (l + 1);
			int delay = (int) (1.25F * (float) l);
			var x = e.getX() + Mth.cos(angle) * len;
			var z = e.getZ() + Mth.sin(angle) * len;
			spawnSingleBolt(e, x, z, d0, d1, angle, delay, dmg);
		}

	}

	private static void spawnSingleBolt(Mob e, double x, double z, double minY, double maxY, float rotation, int delay, int dmg) {
		BlockPos pos = new BlockPos((int) x, (int) maxY, (int) z);
		boolean flag = false;
		double d0 = 0.0F;

		do {
			BlockPos low = pos.below();
			BlockState state = e.level().getBlockState(low);
			if (state.isFaceSturdy(e.level(), low, Direction.UP)) {
				if (!e.level().isEmptyBlock(pos)) {
					BlockState topState = e.level().getBlockState(pos);
					VoxelShape topShape = topState.getCollisionShape(e.level(), pos);
					if (!topShape.isEmpty()) {
						d0 = topShape.max(Direction.Axis.Y);
					}
				}

				flag = true;
				break;
			}

			pos = pos.below();
		} while (pos.getY() >= Mth.floor(minY) - 1);

		if (flag) {
			e.level().addFreshEntity(new LightningBoltEntity(e.level(), x, (double) pos.getY() + d0, z, rotation, delay, e, 20, dmg));
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

		RecipeGen.unlock(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LMCompatRegistry.UPGRADE_THUNDER.get())::unlockedBy,
						ModItems.AIR_RUNE.get())
				.pattern(" X ").pattern("ROR").pattern(" R ")
				.define('R', ModItems.CLOUD_ROD.get())
				.define('X', ModItems.AIR_RUNE.get())
				.define('O', GolemItems.EMPTY_UPGRADE)
				.save(ConditionalRecipeWrapper.mod(pvd, LMDispatch.MODID));
	}
}
