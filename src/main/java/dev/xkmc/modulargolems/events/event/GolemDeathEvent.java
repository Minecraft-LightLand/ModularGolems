package dev.xkmc.modulargolems.events.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class GolemDeathEvent extends LivingEvent {

	private final DamageSource source;

	public GolemDeathEvent(LivingEntity golem, DamageSource source) {
		super(golem);
		this.source = source;
	}

	public DamageSource getSource() {
		return source;
	}

}
