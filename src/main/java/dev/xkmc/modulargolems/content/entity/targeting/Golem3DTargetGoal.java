package dev.xkmc.modulargolems.content.entity.targeting;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Golem3DTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {

	private final AbstractGolemEntity<?, ?> self;

	public Golem3DTargetGoal(AbstractGolemEntity<?, ?> self, int interval) {
		super(self, LivingEntity.class, interval, false, false,
				(e, sl) -> self.predicateTarget(e));
		this.self = self;
	}

	public @Nullable LivingEntity getTarget() {
		return target;
	}

	public void findTarget() {
		if (!(self.level() instanceof ServerLevel sl)) return;
		var entities = self.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()));
		var list = new ArrayList<TargetingStatus>();
		var cen = self.getEyePosition();
		var box = self.getBoundingBox();
		for (var e : entities) {
			if (!targetConditions.test(sl, self, e)) continue;
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
