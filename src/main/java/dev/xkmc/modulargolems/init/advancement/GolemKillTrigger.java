package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.l2library.serial.advancements.BaseCriterion;
import dev.xkmc.l2library.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;

public class GolemKillTrigger extends BaseCriterion<GolemKillTrigger.Ins, GolemKillTrigger> {

	public static Ins ins() {
		return new Ins(GolemTriggers.KILL.getId(), EntityPredicate.Composite.ANY);
	}

	public static Ins byType(EntityType<?> type) {
		Ins ans = ins();
		ans.type = type;
		return ans;
	}

	public GolemKillTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player, Entity killed) {
		this.trigger(player, e -> e.type == null || e.type == killed.getType());
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, GolemKillTrigger> {

		@Nullable
		@SerialClass.SerialField
		private EntityType<?> type;

		public Ins(ResourceLocation id, EntityPredicate.Composite player) {
			super(id, player);
		}

	}

}
