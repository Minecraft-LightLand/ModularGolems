package dev.xkmc.modulargolems.events.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class GolemDeathEvent extends LivingEvent implements ICancellableEvent {

	private final DamageSource source;

	public GolemDeathEvent(LivingEntity golem, DamageSource source) {
		super(golem);
		this.source = source;
	}

	public DamageSource getSource() {
		return source;
	}

}
