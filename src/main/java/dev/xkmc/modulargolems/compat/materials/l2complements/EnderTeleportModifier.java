package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;
import java.util.function.BiConsumer;

public class EnderTeleportModifier extends GolemModifier {

	private static final String KEY = "modulargolems:teleport";

	public EnderTeleportModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		int seconds = MGConfig.COMMON.teleportCooldown.get() / 20;
		return List.of(Component.translatable(getDescriptionId() + ".desc", seconds).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(10, new EnderTeleportGoal(entity));
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (event.getSource().is(L2DamageTypes.MAGIC))
			return;
		if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) {
			event.setCanceled(true);
		}
		if (!mayTeleport(entity)) {
			return;
		}
		for (int i = 0; i < 16; i++) {
			if (teleport(entity)) {
				event.setCanceled(true);
				resetCooldown(entity);
				return;
			}
		}
	}

	public static boolean mayTeleport(AbstractGolemEntity<?, ?> entity) {
		long time = entity.getPersistentData().getLong(KEY);
		long current = entity.level().getGameTime();
		return current >= time + MGConfig.COMMON.teleportCooldown.get();
	}

	public static void resetCooldown(AbstractGolemEntity<?, ?> entity) {
		entity.getPersistentData().putLong(KEY, entity.level().getGameTime());
	}

	public static boolean teleportTowards(AbstractGolemEntity<?, ?> entity, Entity pTarget) {
		return teleport(entity, pTarget.getX(), pTarget.getY(), pTarget.getZ());
	}

	private static boolean teleport(AbstractGolemEntity<?, ?> entity) {
		int r = MGConfig.COMMON.teleportRadius.get();
		if (!entity.level().isClientSide() && entity.isAlive()) {
			double d0 = entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * r * 2;
			double d1 = entity.getY() + (double) (entity.getRandom().nextInt(r * 2) - r);
			double d2 = entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * r * 2;
			return teleport(entity, d0, d1, d2);
		} else {
			return false;
		}
	}

	private static boolean teleport(AbstractGolemEntity<?, ?> entity, double pX, double pY, double pZ) {
		BlockPos.MutableBlockPos ipos = new BlockPos.MutableBlockPos(pX, pY, pZ);

		while (ipos.getY() > entity.level().getMinBuildHeight() && !entity.level().getBlockState(ipos).blocksMotion()) {
			ipos.move(Direction.DOWN);
		}

		BlockState blockstate = entity.level().getBlockState(ipos);
		boolean flag = blockstate.blocksMotion();
		boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
		if (flag && !flag1) {
			EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(entity, pX, pY, pZ);
			if (event.isCanceled()) return false;
			Vec3 vec3 = entity.position();
			boolean flag2 = entity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
			if (flag2) {
				entity.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(entity));
				if (!entity.isSilent()) {
					entity.level().playSound(null, entity.xo, entity.yo, entity.zo, SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 1.0F, 1.0F);
					entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
				}
			}

			return flag2;
		} else {
			return false;
		}
	}

}
