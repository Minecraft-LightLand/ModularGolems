package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.entity.projectile.Void_Rune_Entity;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.BiConsumer;

public class EnderGuardianVoidRuneModifier extends GolemModifier {

	public static void addRune(LivingEntity user, LivingEntity target, int lv) {
		double minY = Math.min(target.getY(), user.getY());
		double maxY = Math.max(target.getY(), user.getY()) + 1;
		Vec3 v = target.getEyePosition().subtract(user.getEyePosition()).normalize();
		float angle = (float) (Mth.atan2(v.z, v.x));
		for (int j = 1; j <= 10 * lv; ++j) {
			double dist = 1.25 * j;
			spawnFangs(user, user.getX() + Mth.cos(angle) * dist, user.getZ() + Mth.sin(angle) * dist, minY, maxY, angle, j);

		}

	}

	private static void spawnFangs(LivingEntity user, double x, double z, double minY, double maxY, float rotation, int delay) {
		BlockPos pos = BlockPos.containing(x, maxY, z);
		boolean flag = false;
		double dy = 0.0;
		do {
			BlockPos below = pos.below();
			BlockState state = user.level().getBlockState(below);
			if (state.isFaceSturdy(user.level(), below, Direction.UP)) {
				if (!user.level().isEmptyBlock(pos)) {
					BlockState next = user.level().getBlockState(pos);
					VoxelShape shape = next.getCollisionShape(user.level(), pos);
					if (!shape.isEmpty()) {
						dy = shape.max(Direction.Axis.Y);
					}
				}
				flag = true;
				break;
			}

			pos = pos.below();
		} while (pos.getY() >= Mth.floor(minY) - 1);
		if (flag) {
			user.level().addFreshEntity(new Void_Rune_Entity(user.level(), x, pos.getY() + dy, z, rotation, delay, user));
		}

	}


	public EnderGuardianVoidRuneModifier() {
		super(StatFilterType.MASS, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new EnderGuardianVoidRuneAttackGoal(entity, lv));
	}

}
