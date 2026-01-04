package dev.xkmc.modulargolems.content.entity.targeting;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public record TargetingStatus(LivingEntity target, TargetingReason reason, int oldCount, double distSqr, double yDiff) {

	public double eval(AbstractGolemEntity<?, ?> self) {
		double score = switch (reason()) {
			case FORCED -> 1000;
			case LAST_HURT -> 180;
			case HURT -> 180;
			case MALICE -> 180;
			case PREY -> 150;
			case PREVIOUS -> 120;
		};
		score -= Mth.clamp((Math.sqrt(distSqr()) / 5), 0, 5);
		score -= oldCount() * 10;
		score -= Mth.clamp(yDiff() / 3, 0, 5);
		return score;
	}
}