package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.l2library.serial.advancements.BaseCriterion;
import dev.xkmc.l2library.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class GolemMassSummonTrigger extends BaseCriterion<GolemMassSummonTrigger.Ins, GolemMassSummonTrigger> {

	public static Ins ins() {
		return new Ins(GolemTriggers.MAS_SUMMON.getId(), ContextAwarePredicate.ANY);
	}

	public static Ins atLeast(int count) {
		Ins ans = ins();
		ans.count = count;
		return ans;
	}

	public GolemMassSummonTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player, int count) {
		this.trigger(player, e -> e.count <= count);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, GolemMassSummonTrigger> {

		@SerialClass.SerialField
		private int count;

		public Ins(ResourceLocation id, ContextAwarePredicate player) {
			super(id, player);
		}

	}

}
