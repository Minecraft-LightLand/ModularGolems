package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class HumanoidGolemEvent extends LivingEvent {

	private final HumanoidGolemEntity golem;

	public HumanoidGolemEvent(HumanoidGolemEntity golem) {
		super(golem);
		this.golem = golem;
	}

	public HumanoidGolemEntity getEntity() {
		return golem;
	}

}
