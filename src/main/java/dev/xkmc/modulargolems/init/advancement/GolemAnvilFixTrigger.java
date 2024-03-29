package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.l2library.serial.advancements.BaseCriterion;
import dev.xkmc.l2library.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class GolemAnvilFixTrigger extends BaseCriterion<GolemAnvilFixTrigger.Ins, GolemAnvilFixTrigger> {

	public static Ins ins() {
		return new Ins(GolemTriggers.ANVIL_FIX.getId(), ContextAwarePredicate.ANY);
	}

	public static Ins withMat(ResourceLocation mat) {
		Ins ans = ins();
		ans.rl = mat;
		return ans;
	}

	public GolemAnvilFixTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player, ResourceLocation mat) {
		this.trigger(player, e -> e.rl == null || e.rl.equals(mat));
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, GolemAnvilFixTrigger> {

		@Nullable
		@SerialClass.SerialField
		private ResourceLocation rl = null;

		public Ins(ResourceLocation id, ContextAwarePredicate player) {
			super(id, player);
		}

	}

}
