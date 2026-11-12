package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.util.SkillSound;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
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

	/**
	 * Whether the golem is currently airborne from a jump attack, used to gate other skills.
	 */
	public static boolean isLeaping(AbstractGolemEntity<?, ?> golem) {
		return !golem.onGround();
	}

	/**
	 * 技能互斥锁，存放在 {@link dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal} 上，
	 * 这里只做转发，保留旧调用点不变。
	 */
	public static boolean isBusy(AbstractGolemEntity<?, ?> golem) {
		return golem.meleeGoal != null && golem.meleeGoal.isBusy();
	}

	public static void setBusy(AbstractGolemEntity<?, ?> golem, boolean on) {
		if (golem.meleeGoal != null) golem.meleeGoal.setBusy(on);
	}

	/** 离地技能期间保留的伤害比例，共享常量，保证 tooltip 与实际减伤不会各说各话。 */
	public static final float LEAP_DAMAGE_MULT = 0.2F;

	/** tooltip 里显示的离地减伤，如 {@code 80%}。 */
	public static MutableComponent leapReduction() {
		return Component.literal(Math.round((1 - LEAP_DAMAGE_MULT) * 100) + "%")
				.withStyle(ChatFormatting.AQUA);
	}

	/** 跳跃技能共用的彩蛋，避免每个技能各写一遍。 */
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
		// 空中已有冲锋或跳跃时傀儡归它所有，此时再选跳跃会叠加
		if (isBusy(golem) || golem.getVehicle() instanceof AbstractGolemEntity<?, ?> dog && isBusy(dog)) return null;
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

		default double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
			return 25;
		}

		/**
		 * 技能当前是否允许使用。默认始终允许；需要形态/血量门槛的技能覆写它。
		 */
		default boolean canUse(AbstractGolemEntity<?, ?> golem) {
			return true;
		}

		/** 技能落地时调用，让共用彩蛋每次只播一次。技能只在非标准落点覆写。 */
		default void onEarthQuakeLanded(AbstractGolemEntity<?, ?> golem, int level) {
			playQuakeEasterEgg(golem);
		}

		/** 玩家骑乘时垂直起跳；AI 骑乘则朝目标斜跳，避免落空后还需重选目标。 */
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

		/** 技能自行处理起跳（服务端执行），默认由客户端本地预测负责。 */
		default boolean useServerJump() {
			return false;
		}

		default int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
			return 100;
		}

		default GolemModifier self() {
			return Wrappers.cast(this);
		}

	}

}