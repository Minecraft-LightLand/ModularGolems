package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeHelper {

	public static final byte FLAG = 83;
	public static final double RANGE = 5;
	public static final float LEAP_DAMAGE_MULT = 0.2F;

	/** 向下扫到第一个可站立方块面并返回其上的落点；找不到返回 null。落地类特效/瞬移共用。 */
	@Nullable
	public static Vec3 findFloor(Level level, Vec3 p, int diff) {
		double x = p.x, maxY = p.y, z = p.z, minY = maxY - diff;
		BlockPos pos = BlockPos.containing(x, maxY, z);
		double dy = 0.0F;
		do {
			BlockPos low = pos.below();
			BlockState lowState = level.getBlockState(low);
			if (lowState.isFaceSturdy(level, low, Direction.UP)) {
				if (!level.isEmptyBlock(pos)) {
					BlockState state = level.getBlockState(pos);
					VoxelShape shape = state.getCollisionShape(level, pos);
					if (!shape.isEmpty()) dy = shape.max(Direction.Axis.Y);
				}
				return new Vec3(x, pos.getY() + dy, z);
			}
			pos = pos.below();
		} while (pos.getY() >= minY);
		return null;
	}

	public static void performDefaultJump(AbstractGolemEntity<?, ?> golem) {
		var target = golem.getTarget();
		if (target == null || golem.getControllingPassenger() instanceof Player)
			golem.addDeltaMovement(new Vec3(0, 1.3, 0));
		else golem.setDeltaMovement(
				(target.getX() - golem.getX()) * 0.15,
				1.3,
				(target.getZ() - golem.getZ()) * 0.15
		);
	}

	public static boolean isLeaping(AbstractGolemEntity<?, ?> golem) {
		return !golem.onGround();
	}

	/** 技能互斥锁存在 {@link dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal} 上，这里只做转发。 */
	public static boolean isBusy(AbstractGolemEntity<?, ?> golem) {
		return golem.meleeGoal != null && golem.meleeGoal.busy;
	}

	public static void setBusy(AbstractGolemEntity<?, ?> golem, boolean on) {
		if (golem.meleeGoal != null) golem.meleeGoal.busy = on;
	}

	public static MutableComponent leapReduction() {
		return Component.literal(Math.round((1 - LEAP_DAMAGE_MULT) * 100) + "%").withStyle(ChatFormatting.AQUA);
	}

	public static void playQuakeEasterEgg(AbstractGolemEntity<?, ?> golem) {
		SkillSound.playQuakeEasterEgg(golem, 1.0F, 1.0F);
	}

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
	public static Instance findMountInstance(AbstractGolemEntity<?, ?> golem) {
		if (golem.hasFlag(GolemFlags.PASSIVE)) return null;
		List<Instance> list = new ArrayList<>();
		long time = golem.level().getGameTime();
		for (var e : golem.getModifiersExtended().entrySet()) {
			if (e.getKey() instanceof Modifier m) {
				long last = golem.getPersistentData().getLong(e.getKey().getID() + ":timestamp");
				if ((last + m.getCoolDown(golem, e.getValue()) < time || last > time) && m.canUse(golem)) {
					list.add(new Instance(golem, m, e.getValue()));
				}
			}
		}
		if (!list.isEmpty()) {
			return list.get(golem.getRandom().nextInt(list.size()));
		}
		return null;
	}

	@Nullable
	public static Instance findInstance(AbstractGolemEntity<?, ?> golem, LivingEntity target, double distSqr) {
		if (golem.getVehicle() != null && !(golem.getVehicle() instanceof AbstractGolemEntity<?, ?> dog && dog.getControllingPassenger() == golem))
			return null;
		if (!golem.getPassengers().isEmpty()) return null;
		if (golem.getMode() == GolemModes.STAND) return null;
		if (isBusy(golem) || golem.getVehicle() instanceof AbstractGolemEntity<?, ?> dg && isBusy(dg)) return null;
		List<Instance> list = new ArrayList<>();
		long time = golem.level().getGameTime();
		for (var e : golem.getModifiersExtended().entrySet()) {
			if (e.getKey() instanceof Modifier m) {
				long last = golem.getPersistentData().getLong(e.getKey().getID() + ":timestamp");
				if ((last + m.getCoolDown(golem, e.getValue()) < time || last > time) && m.canUse(golem)) {
					if (m.getEarthquakeRangeSqr(golem, target, e.getValue()) > distSqr) {
						list.add(new Instance(golem, m, e.getValue()));
					}
				}
			}
		}
		if (golem.getVehicle() instanceof AbstractGolemEntity<?, ?> dog && !dog.hasFlag(GolemFlags.PASSIVE)) {
			for (var e : dog.getModifiersExtended().entrySet()) {
				if (e.getKey() instanceof Modifier m) {
					long last = dog.getPersistentData().getLong(e.getKey().getID() + ":timestamp");
					if ((last + m.getCoolDown(dog, e.getValue()) < time || last > time) && m.canUse(dog)) {
						if (m.getEarthquakeRangeSqr(dog, target, e.getValue()) > distSqr) {
							list.add(new Instance(dog, m, e.getValue()));
						}
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

	public record Instance(AbstractGolemEntity<?, ?> owner, Modifier modifier, int lv) {

		public void performJump(AbstractGolemEntity<?, ?> mover) {
			modifier().performJump(mover, lv());
		}

		public void addCD() {
			owner().getPersistentData().putLong(modifier().self().getID() + ":timestamp", owner().level().getGameTime());
		}

		public boolean isValid() {
			var time = owner().level().getGameTime();
			long last = owner().getPersistentData().getLong(modifier().self().getID() + ":timestamp");
			return last + modifier().getCoolDown(owner(), lv()) < time || last > time;
		}

	}

	public interface Modifier {

		void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level);

		/** 技能当前是否允许使用。默认始终允许；需要形态/血量门槛的技能覆写它。 */
		default boolean canUse(AbstractGolemEntity<?, ?> golem) {
			return true;
		}

		default double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
			return 25;
		}

		default void performJump(AbstractGolemEntity<?, ?> golem, int lv) {
			var target = golem.getTarget();
			if (target == null || golem.getControllingPassenger() instanceof Player)
				golem.addDeltaMovement(new Vec3(0, 1.3, 0));
			else golem.setDeltaMovement(
					(target.getX() - golem.getX()) * 0.15,
					1.3,
					(target.getZ() - golem.getZ()) * 0.15
			);
		}

		default void onEarthQuakeLanded(AbstractGolemEntity<?, ?> golem, int lv) {
			EarthquakeHelper.playQuakeEasterEgg(golem);
		}

		default int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
			return 100;
		}

		/** Whether the jump should also be executed server-side (client normally predicts it). */
		default boolean useServerJump() {
			return false;
		}

		default GolemModifier self() {
			return Wrappers.cast(this);
		}

	}

}
