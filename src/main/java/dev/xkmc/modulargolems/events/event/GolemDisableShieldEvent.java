package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.entity.LivingEntity;

public class GolemDisableShieldEvent extends HumanoidGolemEvent {

	private final LivingEntity source;

	private boolean shouldDisable;

	public GolemDisableShieldEvent(HumanoidGolemEntity golem, LivingEntity source, boolean shouldDisable) {
		super(golem);
		this.source = source;
		this.shouldDisable = shouldDisable;
	}

	public LivingEntity getSource() {
		return source;
	}

	public void setDisabled(boolean disabled) {
		shouldDisable = disabled;
	}

	public boolean shouldDisable() {
		return shouldDisable;
	}

}
