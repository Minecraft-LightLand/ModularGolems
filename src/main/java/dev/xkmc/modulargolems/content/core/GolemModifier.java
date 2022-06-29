package dev.xkmc.modulargolems.content.core;

import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.network.chat.Component;

public class GolemModifier extends NamedEntry<GolemModifier> {

	public GolemModifier() {
		super(GolemTypeRegistry.MODIFIERS);
	}

	public Component getTooltip() {
		return getDesc().append(": ").append(Component.translatable(getDescriptionId() + ".desc"));
	}

}
