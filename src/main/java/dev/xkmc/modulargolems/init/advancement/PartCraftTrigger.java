package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.l2library.serial.advancements.BaseCriterion;
import dev.xkmc.l2library.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class PartCraftTrigger extends BaseCriterion<PartCraftTrigger.Ins, PartCraftTrigger> {

	public static Ins ins() {
		return new Ins(GolemTriggers.PART_CRAFT.getId(), ContextAwarePredicate.ANY);
	}

	public static Ins withMat(ResourceLocation mat) {
		Ins ans = ins();
		ans.rl = mat;
		return ans;
	}

	public PartCraftTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player, ResourceLocation rl) {
		this.trigger(player, e -> e.rl == null || e.rl.equals(rl));
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, PartCraftTrigger> {

		@Nullable
		@SerialClass.SerialField
		private ResourceLocation rl = null;

		public Ins(ResourceLocation id, ContextAwarePredicate player) {
			super(id, player);
		}

	}

}
