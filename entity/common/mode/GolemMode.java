package dev.xkmc.modulargolems.content.entity.common.mode;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.LangData;
import net.minecraft.network.chat.Component;

public class GolemMode {

	private final int id;
	private final boolean positioned, movable;
	private final LangData lang;

	protected GolemMode(boolean positioned, boolean movable, LangData lang) {
		this.positioned = positioned;
		this.movable = movable;
		this.lang = lang;
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

	public Component getDesc(AbstractGolemEntity<?, ?> golem) {
		if (positioned) {
			var p = golem.getGuardPos();
			return lang.get(p.getX(), p.getY(), p.getZ());
		} else {
			return lang.get();
		}
	}

}
