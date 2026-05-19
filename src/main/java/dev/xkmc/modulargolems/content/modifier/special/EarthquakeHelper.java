package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeHelper {

	public static final byte FLAG = 83;

	public static void makeParticles(LivingEntity le, float vec, float math) {
		if (le.level().isClientSide) {
			for (int i1 = 0; i1 < 80 + le.getRandom().nextInt(12); ++i1) {
				double DeltaMovementX = le.getRandom().nextGaussian() * 0.07;
				double DeltaMovementY = le.getRandom().nextGaussian() * 0.07;
				double DeltaMovementZ = le.getRandom().nextGaussian() * 0.07;
				float f = Mth.cos(le.yBodyRot * 0.017453292F);
				float f1 = Mth.sin(le.yBodyRot * 0.017453292F);
				float angle = 0.017453292F * le.yBodyRot + i1;
				double extraX = 2.0F * Mth.sin((float) (Math.PI + angle));
				double extraY = 0.30000001192092896;
				double extraZ = 2.0F * Mth.cos(angle);
				double theta = le.yBodyRot * 0.017453292519943295;
				++theta;
				double vecX = Math.cos(theta);
				double vecZ = Math.sin(theta);
				int hitX = Mth.floor(le.getX() + vec * vecX + extraX);
				int hitY = Mth.floor(le.getY());
				int hitZ = Mth.floor(le.getZ() + vec * vecZ + extraZ);
				BlockPos hit = new BlockPos(hitX, hitY, hitZ);
				BlockState block = le.level().getBlockState(hit.below());
				le.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block), le.getX() + (double) vec * vecX + extraX + (double) (f * math), le.getY() + extraY, le.getZ() + (double) vec * vecZ + extraZ + (double) (f1 * math), DeltaMovementX, DeltaMovementY, DeltaMovementZ);
			}
		}

	}

	public static void launch(LivingEntity le, Entity e, float f) {
		double d0 = e.getX() - le.getX();
		double d1 = e.getZ() - le.getZ();
		double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
		e.push(d0 / d2 * f, 0.375 * f, d1 / d2 * f);
	}

	@Nullable
	public static Instance findInstance(AbstractGolemEntity<?, ?> golem, LivingEntity target, double distSqr) {
		if (golem.getVehicle() != null) return null;
		if (!golem.getPassengers().isEmpty()) return null;
		List<Instance> list = new ArrayList<>();
		long time = golem.level().getGameTime();
		for (var e : golem.getModifiersExtended().entrySet()) {
			if (e.getKey() instanceof Modifier m) {
				long last = golem.getPersistentData().getLong(e.getKey().getID() + ":timestamp");
				if (last + m.getCoolDown(golem, e.getValue()) < time || last > time) {
					if (m.getEarthquakeRangeSqr(golem, target, e.getValue()) > distSqr) {
						list.add(new Instance(m, e.getValue()));
					}
				}
			}
		}
		if (!list.isEmpty()) {
			return list.get(golem.getRandom().nextInt(list.size()));
		}
		return null;
	}

	public static boolean shouldRetreat(AbstractGolemEntity<?, ?> golem, LivingEntity target, double dist, double reach) {
		return golem.hasFlag(GolemFlags.EARTH_QUAKE) && dist < reach + 4 &&
				EarthquakeHelper.findInstance(golem, target, dist * dist - reach * reach + 4) != null;
	}

	public record Instance(Modifier modifier, int lv) {

	}

	public interface Modifier {

		void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level);

		default double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
			return 25;
		}

		default void performJump(AbstractGolemEntity<?, ?> golem, int lv) {
			golem.addDeltaMovement(new Vec3(0, 1, 0));
		}

		default int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
			return 100;
		}
	}

}
