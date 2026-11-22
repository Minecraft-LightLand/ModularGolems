package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;

@SerialClass
public class TargetingAnimState {

	@SerialField(toClient = true)
	public int lastTargetTime = -1, lastNoTargetTime, lastTick;

	@SerialField(toClient = true)
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
