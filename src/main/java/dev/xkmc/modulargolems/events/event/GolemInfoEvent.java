package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class GolemInfoEvent extends Event {

	private final AbstractGolemEntity<?, ?> golem;
	private final List<Component> info;

	public GolemInfoEvent(AbstractGolemEntity<?, ?> golem, List<Component> info) {
		this.golem = golem;
		this.info = info;
	}

	public AbstractGolemEntity<?, ?> getGolem() {
		return golem;
	}

	public void addLine(Component comp) {
		info.add(comp);
	}

}
