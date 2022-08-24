package dev.xkmc.modulargolems.content.core;

import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class GolemModifier extends NamedEntry<GolemModifier> {

	public static final int MAX_LEVEL = 10;

	public final int maxLevel;

	public GolemModifier(int maxLevel) {
		super(GolemTypeRegistry.MODIFIERS);
		this.maxLevel = maxLevel;
	}

	public Component getTooltip(int v) {
		//TODO level
		return getDesc().append(": ").append(Component.translatable(getDescriptionId() + ".desc"));
	}

	public void onGolemSpawn(AbstractGolemEntity<?, ?> entity, int level) {

	}

	/**
	 * fires when this golem attacks others
	 */
	public void onAttack(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {

	}

	/**
	 * fires when this golem is attacked. Damage cancellation phase
	 */
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {

	}

	/**
	 * fires when this golem is attacked. Damage calculation phase
	 */
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {

	}

	/**
	 * fires when this golem is attacked. Damage taking phase
	 */
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {

	}

}
