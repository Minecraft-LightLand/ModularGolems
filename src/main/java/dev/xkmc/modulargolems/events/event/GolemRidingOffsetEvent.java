package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;

public class GolemRidingOffsetEvent extends Event {

	private final AbstractGolemEntity<?, ?> golem;

	private Vec3 offset;

	public GolemRidingOffsetEvent(AbstractGolemEntity<?, ?> golem) {
		this.golem = golem;
	}

	public AbstractGolemEntity<?, ?> getGolem() {
		return golem;
	}

	public void setOffset(Vec3 offset) {
		this.offset = offset;
	}

	public Vec3 getOffset() {
		return offset;
	}

	public void setOffsetY(double y) {
		setOffset(new Vec3(offset.x, y, offset.z));
	}

}
