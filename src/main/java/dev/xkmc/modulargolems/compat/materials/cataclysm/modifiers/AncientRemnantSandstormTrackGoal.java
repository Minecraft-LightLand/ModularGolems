package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * 沙暴的持续目标维护。攻击 Goal 只在冷却结束时跑一次，那样目标死了要等好几秒才换人，
 * 这里每帧只做便宜的存活校验，重新扫全场目标则按固定间隔做，避免每帧扫 35 格半径的实体。
 */
public class AncientRemnantSandstormTrackGoal extends Goal {

	private static final int RESCAN_INTERVAL = 5;

	private final AbstractGolemEntity<?, ?> golem;
	private int timer;

	public AncientRemnantSandstormTrackGoal(AbstractGolemEntity<?, ?> golem) {
		this.golem = golem;
	}

	@Override
	public boolean canUse() {
		return golem.getTarget() != null;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		var group = AncientRemnantSandstormGroup.of(golem);
		group.track(golem);
		if (timer > 0) {
			timer--;
			return;
		}
		timer = RESCAN_INTERVAL;
		group.assign(golem);
	}

	@Override
	public void stop() {
		timer = 0;
	}

}
