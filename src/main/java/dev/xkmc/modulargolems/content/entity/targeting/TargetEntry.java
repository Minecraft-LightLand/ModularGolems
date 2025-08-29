package dev.xkmc.modulargolems.content.entity.targeting;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class TargetEntry {

	private long gameTick;
	private Set<UUID> prevSet = new LinkedHashSet<>(), currentSet = new LinkedHashSet<>();

	private void tick(long gameTime) {
		if (gameTick == gameTime) return;
		if (gameTick > gameTime) {
			gameTick = gameTime;
			return;
		}
		if (gameTime > gameTick + 1) {
			prevSet = new LinkedHashSet<>();
			currentSet = new LinkedHashSet<>();
		} else {
			prevSet = currentSet;
			currentSet = new LinkedHashSet<>();
		}
		gameTick = gameTime;
	}

	public void put(long time, AbstractGolemEntity<?, ?> self) {
		tick(time);
		currentSet.add(self.getUUID());
	}

	public void tick(long time, AbstractGolemEntity<?, ?> self) {
		tick(time);
		currentSet.add(self.getUUID());
	}

	public int get(long time, AbstractGolemEntity<?, ?> self) {
		tick(time);
		int n = prevSet.size();
		if (prevSet.contains(self.getUUID())) n--;
		return n;
	}

	public long getLastTick() {
		return gameTick;
	}

}
