package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.CataclysmProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public class IgnisFireballModifier extends GolemModifier {

	private static final int[] ANGLE = {-5, -2, 0, 2, 5};

	public IgnisFireballModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new IgnisFireballAttackGoal(entity, lv));
	}

	public static void addFireball(LivingEntity user, int lv) {
		user.level().playLocalSound(user.getX(), user.getY(), user.getZ(), SoundEvents.EVOKER_PREPARE_SUMMON,
				user.getSoundSource(), 5.0F, 1.4F + user.getRandom().nextFloat() * 0.1F, false);
		int index = user.getRandom().nextInt(5);
		lv = Mth.clamp(lv, 0, 2);
		for (int i = 2 - lv; i < 3 + lv; i++) {
			CataclysmProxy.shootFireball(user, new Vec3(ANGLE[i], 3.0D, 0.0D), 15 + i * 10, index == i);
		}
	}

}
