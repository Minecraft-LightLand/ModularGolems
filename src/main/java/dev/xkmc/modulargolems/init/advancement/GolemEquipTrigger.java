package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.l2core.serial.advancements.BaseCriterion;
import dev.xkmc.l2core.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.server.level.ServerPlayer;

public class GolemEquipTrigger extends BaseCriterion<GolemEquipTrigger.Ins, GolemEquipTrigger> {

	public static Ins ins(int min) {
		Ins ans = new Ins();
		ans.minimum = min;
		return ans;
	}

	public GolemEquipTrigger() {
		super(Ins.class);
	}

	public void trigger(ServerPlayer player, int count) {
		this.trigger(player, e -> e.minimum <= count);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, GolemEquipTrigger> {

		@SerialField
		private int minimum = 0;

		public Ins() {
			super(GolemTriggers.EQUIP.get());
		}

	}

}
