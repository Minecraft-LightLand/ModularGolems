package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.l2core.serial.advancements.BaseCriterion;
import dev.xkmc.l2core.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class PartCraftTrigger extends BaseCriterion<PartCraftTrigger.Ins, PartCraftTrigger> {

	public static Ins ins() {
		return new Ins();
	}

	public static Ins withMat(ResourceLocation mat) {
		Ins ans = ins();
		ans.rl = mat;
		return ans;
	}

	public PartCraftTrigger() {
		super(Ins.class);
	}

	public void trigger(ServerPlayer player, ResourceLocation rl) {
		this.trigger(player, e -> e.rl == null || e.rl.equals(rl));
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, PartCraftTrigger> {

		@Nullable
		@SerialField
		private ResourceLocation rl = null;

		public Ins() {
			super(GolemTriggers.PART_CRAFT.get());
		}

	}

}
