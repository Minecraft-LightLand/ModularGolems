package dev.xkmc.modulargolems.content.entity.mode;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;

public class GolemMode {

	private final int id;
	private final boolean positioned, movable, wander;
	private final MGLangData lang, name;

	protected GolemMode(boolean positioned, boolean movable, boolean wander, MGLangData lang, MGLangData name) {
		this.positioned = positioned;
		this.movable = movable;
		this.wander = wander;
		this.lang = lang;
		this.name = name;
		id = GolemModes.LIST.size();
		GolemModes.LIST.add(this);
	}

	public boolean canChangeDimensions() {
		return !hasPos();
	}

	public int getID() {
		return id;
	}

	public boolean isMovable() {
		return movable;
	}

	public boolean hasPos() {
		return positioned;
	}

	public boolean couldRandomStroll() {
		return wander;
	}

	public Component getName() {
		return name.get();
	}

	public Component getDesc(AbstractGolemEntity<?, ?> golem) {
		if (positioned) {
			var p = golem.getGuardPos();
			return lang.get(p.getX(), p.getY(), p.getZ());
		} else {
			return lang.get();
		}
	}

	public double getStartFollowDistance(AbstractGolemEntity<?, ?> golem) {
		if (this == GolemModes.SQUAD) {
			var entry = golem.getConfigEntry(null);
			if (entry != null) {
				return entry.squadConfig.getRadius();
			}
		}
		return couldRandomStroll() ?
				MGConfig.COMMON.stopWanderRadius.get() :
				MGConfig.COMMON.startFollowRadius.get();
	}
}
