package dev.xkmc.modulargolems.content.entity.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class Golem3DTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

	public Golem3DTargetGoal(Mob self, Class<T> cls, int interval, boolean mustSee, boolean mustReach, @Nullable Predicate<LivingEntity> pred) {
		super(self, cls, interval, mustSee, mustReach, pred);
	}

	@Override
	protected AABB getTargetSearchArea(double r) {
		return this.mob.getBoundingBox().inflate(r, r, r);
	}

}
