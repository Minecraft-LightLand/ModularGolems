package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.l2core.serial.advancements.BaseCriterion;
import dev.xkmc.l2core.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;

public class GolemKillTrigger extends BaseCriterion<GolemKillTrigger.Ins, GolemKillTrigger> {

	public static Ins ins() {
		return new Ins();
	}

	public static Ins byType(EntityType<?> type) {
		Ins ans = ins();
		ans.type = type;
		return ans;
	}

	public GolemKillTrigger() {
		super(Ins.class);
	}

	public void trigger(ServerPlayer player, Entity killed) {
		this.trigger(player, e -> e.type == null || e.type == killed.getType());
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, GolemKillTrigger> {

		@Nullable
		@SerialField
		private EntityType<?> type;

		public Ins() {
			super(GolemTriggers.KILL.get());
		}

	}

}
