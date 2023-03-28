package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.l2library.base.advancements.BaseCriterion;
import dev.xkmc.l2library.base.advancements.BaseCriterionInstance;
import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class GolemThunderTrigger extends BaseCriterion<GolemThunderTrigger.Ins, GolemThunderTrigger> {

	public static Ins ins() {
		return new Ins(GolemTriggers.THUNDER.getId(), EntityPredicate.Composite.ANY);
	}

	public GolemThunderTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, e -> true);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, GolemThunderTrigger> {

		public Ins(ResourceLocation id, EntityPredicate.Composite player) {
			super(id, player);
		}

	}

}
