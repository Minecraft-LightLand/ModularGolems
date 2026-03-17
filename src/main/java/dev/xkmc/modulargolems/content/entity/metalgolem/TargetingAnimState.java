package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.l2serial.serialization.SerialClass;

@SerialClass
public class TargetingAnimState {

	@SerialClass.SerialField(toClient = true)
	public int lastTargetTime, lastNoTargetTime, lastStartDur, lastEndDur, lastTick;

	@SerialClass.SerialField(toClient = true)
	public boolean targeting = false;

	public void tick(MetalGolemEntity e) {
		if (lastTick != e.tickCount + 1) {
			int diff = e.tickCount - lastTick - 1;
			lastTargetTime += diff;
			lastNoTargetTime += diff;
		}
		lastTick = e.tickCount;

		var pos = e.getTargetAimPos();
		boolean prevTargeting = targeting;
		targeting = pos.length() > 0;
		if (!targeting && prevTargeting) {
			lastNoTargetTime = lastTick - 1;
		}
		if (targeting && !prevTargeting) {
			lastEndDur =
			lastTargetTime = lastTick - 1;
		}
	}

	public int getStartingAnim() {
		if (!targeting) return -1;
		return lastTick - lastNoTargetTime;
	}

	public int getEndingAnim() {
		if (targeting) return -1;
		return lastTick - lastTargetTime;
	}

}
