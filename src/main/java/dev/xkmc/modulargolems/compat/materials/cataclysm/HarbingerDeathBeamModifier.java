package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.entity.projectile.Death_Laser_Beam_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

public class HarbingerDeathBeamModifier extends GolemModifier {

	public static void addBeam(LivingEntity user) {
		Death_Laser_Beam_Entity DeathBeam = new Death_Laser_Beam_Entity(ModEntities.DEATH_LASER_BEAM.get(),
				user.level(), user, user.getX(), user.getEyeY(), user.getZ(),
				(user.yHeadRot + 90.0F) * Mth.DEG_TO_RAD,
				-user.getXRot() * Mth.DEG_TO_RAD,
				60);
		user.level().addFreshEntity(DeathBeam);
	}

	public HarbingerDeathBeamModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new HarbingerDeathBeamAttackGoal(entity, lv));
	}

}
