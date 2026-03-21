package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraftforge.eventbus.api.Event;

public class GolemRidingOffsetEvent extends Event {

	private final AbstractGolemEntity<?, ?> golem;

	private double offset;

	public GolemRidingOffsetEvent(AbstractGolemEntity<?, ?> golem) {
		this.golem = golem;
	}

	public AbstractGolemEntity<?, ?> getGolem() {
		return golem;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public double getOffset() {
		return offset;
	}

}
