package dev.xkmc.modulargolems.content.core;

import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class GolemModifier extends NamedEntry<GolemModifier> {

	public GolemModifier() {
		super(GolemTypeRegistry.MODIFIERS);
	}

	public Component getTooltip(int v) {
		//TODO level
		return getDesc().append(": ").append(Component.translatable(getDescriptionId() + ".desc"));
	}

	public void onGolemSpawn(AbstractGolemEntity<?> entity, int level) {

	}

	public void onHurt(AbstractGolemEntity<?> entity, LivingHurtEvent event, int level) {

	}

	public void onAttack(AbstractGolemEntity<?> entity, LivingHurtEvent event, int level){

	}

}
