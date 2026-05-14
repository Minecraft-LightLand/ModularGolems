package dev.xkmc.modulargolems.content.entity.metalgolem;


import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;

@SerialClass
public class TargetingAnimState {

	@SerialField
	public int lastTargetTime, lastNoTargetTime, lastTick;

	@SerialField
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
		targeting = pos.isPresent();
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
		if (targeting) return -1;
		return lastTick - lastTargetTime;
	}

}
