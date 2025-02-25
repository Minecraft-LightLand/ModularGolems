package dev.xkmc.mob_weapon_api.util;

import dev.xkmc.mob_weapon_api.api.projectile.BowUseContext;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;

public class ShootUtils {



	private static final HashSet<Class<?>> BLACKLIST = new HashSet<>();

	@SuppressWarnings("ConstantConditions")
	public static boolean arrowIsInfinite(ItemStack arrow, ItemStack bow) {
		if (!(arrow.getItem() instanceof ArrowItem item)) {
			return false;
		}
		if (BLACKLIST.contains(item.getClass())) {
			return false;
		}
		try {
			return item.isInfinite(arrow, bow, null);
		} catch (NullPointerException npe) {
			BLACKLIST.add(item.getClass());
		}
		return false;
	}

	public static void shootAimHelper(LivingEntity target, Projectile arrow, float v, float g) {
		getShootVector(target, arrow.position(), v, g, 0).apply(arrow);
	}

	public static ArrowConsumer getShootVector(LivingEntity target, Vec3 arrow, float v, float g, float ina) {
		double dx = target.getX() - arrow.x();
		double dy = target.getY(0.5) - arrow.y();
		double dz = target.getZ() - arrow.z();

		double c = dx * dx + dz * dz + dy * dy;
		boolean completed = false;
		if (target instanceof Slime) {
			var clip = target.level().clip(new ClipContext(target.position(), target.position().add(0, -3, 0),
					ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, target));
			if (clip.getType() == HitResult.Type.BLOCK) {
				dy += clip.getLocation().y() - target.getY();
				completed = true;
			}
		}
		if (!completed) {
			double rt = Math.sqrt(c) / v;
			var tv = target.getDeltaMovement();
			dx += tv.x * rt;
			dy += tv.y * rt;
			dz += tv.z * rt;
		}

		c = dx * dx + dz * dz + dy * dy;

		if (g > 0 && c > v * v * 4) {
			double a = g * g / 4;
			double b = dy * g - v * v;

			double delta = b * b - 4 * a * c;
			if (delta > 0) {
				double t21 = (-b + Math.sqrt(delta)) / (2 * a);
				double t22 = (-b - Math.sqrt(delta)) / (2 * a);
				if (t21 > 0 || t22 > 0) {
					double t2 = t21 > 0 ? t22 > 0 ? Math.min(t21, t22) : t21 : t22;
					return new ArrowConsumer(new Vec3(dx, dy + g * t2 / 2, dz), v, ina);

				}
			}
		}
		return new ArrowConsumer(new Vec3(dx, dy, dz), v, ina);
	}

	public record ArrowConsumer(Vec3 vec3, float v, float inaccuracy) implements BowUseContext.AimResult {

		public ArrowConsumer rotate(float angle) {
			return new ArrowConsumer(vec3.yRot(angle * Mth.DEG_TO_RAD), v, inaccuracy);
		}

		public void apply(Projectile arrow) {
			arrow.shoot(vec3.x, vec3.y, vec3.z, v, inaccuracy);
		}

		@Override
		public void shoot(Projectile projectile, float angleInDegree) {
			rotate(angleInDegree).apply(projectile);
			projectile.getPersistentData().putInt("DespawnFactor", 20);
		}
	}

}
