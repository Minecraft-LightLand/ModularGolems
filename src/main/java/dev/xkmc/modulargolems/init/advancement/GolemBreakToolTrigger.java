package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.l2library.base.advancements.BaseCriterion;
import dev.xkmc.l2library.base.advancements.BaseCriterionInstance;
import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class GolemBreakToolTrigger extends BaseCriterion<GolemBreakToolTrigger.Ins, GolemBreakToolTrigger> {

	public static Ins ins() {
		return new Ins(GolemTriggers.BREAK.getId(), EntityPredicate.Composite.ANY);
	}

	public GolemBreakToolTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, e -> true);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, GolemBreakToolTrigger> {

		public Ins(ResourceLocation id, EntityPredicate.Composite player) {
			super(id, player);
		}

	}

}
