package dev.xkmc.modulargolems.compat.materials.l2complements.modifiers;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.function.BiConsumer;

public class EnderTeleportModifier extends GolemModifier {

	public EnderTeleportModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(10, new EnderTeleportGoal(entity, lv));
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (event.getSource().isMagic())
			return;
		for (int i = 0; i < 16; i++) {
			if (teleport(entity)) {
				event.setCanceled(true);
				return;
			}
		}
	}

	public static boolean teleportTowards(AbstractGolemEntity<?, ?> entity, Entity pTarget) {
		Vec3 vec3 = new Vec3(entity.getX() - pTarget.getX(), entity.getY(0.5D) - pTarget.getEyeY(), entity.getZ() - pTarget.getZ());
		vec3 = vec3.normalize();
		int r = ModConfig.COMMON.teleportRadius.get();
		double d1 = entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * r - vec3.x * r * 2;
		double d2 = entity.getY() + (double) (entity.getRandom().nextInt(r * 2) - r) - vec3.y * r * 2;
		double d3 = entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * r - vec3.z * r * 2;
		return teleport(entity, d1, d2, d3);
	}

	private static boolean teleport(AbstractGolemEntity<?, ?> entity) {
		int r = ModConfig.COMMON.teleportRadius.get();
		if (!entity.level.isClientSide() && entity.isAlive()) {
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

		while (ipos.getY() > entity.level.getMinBuildHeight() && !entity.level.getBlockState(ipos).getMaterial().blocksMotion()) {
			ipos.move(Direction.DOWN);
		}

		BlockState blockstate = entity.level.getBlockState(ipos);
		boolean flag = blockstate.getMaterial().blocksMotion();
		boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
		if (flag && !flag1) {
			EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(entity, pX, pY, pZ);
			if (event.isCanceled()) return false;
			Vec3 vec3 = entity.position();
			boolean flag2 = entity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
			if (flag2) {
				entity.level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(entity));
				if (!entity.isSilent()) {
					entity.level.playSound(null, entity.xo, entity.yo, entity.zo, SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 1.0F, 1.0F);
					entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
				}
			}

			return flag2;
		} else {
			return false;
		}
	}

}
