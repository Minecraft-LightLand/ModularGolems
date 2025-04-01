package dev.xkmc.modulargolems.content.client.tracker;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.modulargolems.content.capability.GolemTracker;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.registry.TrackerGroup;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.UUID;

public class DeadGolemPage extends GolemInfoScreen {

	protected DeadGolemPage(Component title) {
		super(title);
	}

	@Override
	public List<Pair<UUID, GolemTracker.TrackedData>> getData() {
		return getData(e -> e.status.isDeath());
	}

	@Override
	public void init() {
		super.init();
		new GolemTabManager<>(this, new TrackerGroup())
				.init(this::addRenderableWidget, GolemTabRegistry.TRACKER_DEAD);
	}

}
