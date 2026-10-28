package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.serialization.SerialClass;

@SerialClass
public class TargetingAnimState {

	@SerialClass.SerialField(toClient = true)
	/**
	 * Negative until this golem has actually been tracking something, so that a golem which never had
	 * a target reports no retract animation instead of replaying it from frame zero every tick.
	 */
	public int lastTargetTime = -1, lastNoTargetTime, lastTick;

	@SerialClass.SerialField(toClient = true)
	public boolean targeting = false;

	public void tick(AbstractGolemEntity<?, ?> e) {
		if (lastTick != e.tickCount + 1) {
			int diff = e.tickCount - lastTick - 1;
			if (lastTargetTime >= 0) lastTargetTime += diff;
			lastNoTargetTime += diff;
		}
		lastTick = e.tickCount;

		var pos = e.getTargetAimPos();
		boolean prevTargeting = targeting;
		targeting = pos.length() > 0;
		if (!targeting && prevTargeting) {
			lastTargetTime = lastTick - 1;
		}
		if (targeting && !prevTargeting) {
			lastNoTargetTime = lastTick - 1;
		}
	}

	public int getStartingAnim() {
		if (!targeting) return -1;
		return lastTick - lastNoTargetTime;
	}

	public int getEndingAnim() {
		if (targeting || lastTargetTime < 0) return -1;
		return lastTick - lastTargetTime;
	}

}
