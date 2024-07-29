package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.l2core.serial.advancements.BaseCriterion;
import dev.xkmc.l2core.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class GolemMassSummonTrigger extends BaseCriterion<GolemMassSummonTrigger.Ins, GolemMassSummonTrigger> {

	public static Ins ins() {
		return new Ins();
	}

	public static Ins atLeast(int count) {
		Ins ans = ins();
		ans.count = count;
		return ans;
	}

	public GolemMassSummonTrigger(ResourceLocation id) {
		super(Ins.class);
	}

	public void trigger(ServerPlayer player, int count) {
		this.trigger(player, e -> e.count <= count);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, GolemMassSummonTrigger> {

		@SerialField
		private int count;

		public Ins() {
			super(GolemTriggers.MAS_SUMMON.get());
		}

	}

}
