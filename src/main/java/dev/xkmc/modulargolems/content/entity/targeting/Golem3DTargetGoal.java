package dev.xkmc.modulargolems.content.entity.targeting;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Golem3DTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {

	private final AbstractGolemEntity<?, ?> self;

	public Golem3DTargetGoal(AbstractGolemEntity<?, ?> self, int interval) {
		super(self, LivingEntity.class, interval, false, false, self::predicateTarget);
		this.self = self;
	}

	public @Nullable LivingEntity getTarget() {
		return target;
	}

	@Override
	public boolean canUse() {
		if (self.getControllingPassenger() instanceof Player) {
			if (self.tickCount % 2 == 0)
				return true;
			var prev = self.getTarget();
			if (prev != null && !self.meleeGoal.canReachTarget(prev))
				return true;
		}
		return super.canUse();
	}

	public void findTarget() {
		if (self.getControllingPassenger() instanceof AbstractGolemEntity<?, ?> rider) {
			target = rider.getTarget();
			return;
		}
		var entities = self.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()));
		if (self.getControllingPassenger() instanceof Player) {
			for (var e : entities) {
				if (!targetConditions.test(self, e)) continue;
				var reason = TargetManager.predicateTarget(self, e);
				if (reason == null) continue;
				if (self.meleeGoal.canReachTarget(e)) {
					target = e;
					return;
				}
			}
			return;
		}
		var list = new ArrayList<TargetingStatus>();
		var cen = self.getEyePosition();
		var box = self.getBoundingBox();
		for (var e : entities) {
			if (!targetConditions.test(self, e)) continue;
			var reason = TargetManager.predicateTarget(self, e);
			if (reason == null) continue;
			var ebox = e.getBoundingBox();
			double yDiff = Math.max(ebox.minY - box.maxY, box.minY - ebox.maxY);
			list.add(new TargetingStatus(e, reason, TargetManager.get(self).getPrevCount(self, e), e.distanceToSqr(cen), yDiff));
		}
		target = TargetManager.findBestTarget(self, list);
	}

	@Override
	protected AABB getTargetSearchArea(double r) {
		return this.mob.getBoundingBox().inflate(r, r, r);
	}

}
