package dev.xkmc.modulargolems.content.entity.mode;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;

public class GolemMode {

	public final String key;
	private final int id;
	private final boolean positioned, movable, wander;
	private final MGLangData lang, name;

	protected GolemMode(String key, boolean positioned, boolean movable, boolean wander, MGLangData lang, MGLangData name) {
		this.key = key;
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
		// 跟随/编队模式允许配置卡自定义跟随距离
		var entry = golem.getConfigEntry(null);
		if (this == GolemModes.FOLLOW || this == GolemModes.SQUAD) {
			if (entry != null && entry.followDistance > 0) {
				return entry.followDistance;
			}
		}
		if (couldRandomStroll() && entry != null && entry.wanderRadius > 0) {
			return entry.wanderRadius;
		}
		return couldRandomStroll() ?
				MGConfig.COMMON.stopWanderRadius.get() :
				MGConfig.COMMON.startFollowRadius.get();
	}

	public void tick(AbstractGolemEntity<?, ?> golem) {

	}

	public double getStopDistance() {
		return 3;
	}

}
