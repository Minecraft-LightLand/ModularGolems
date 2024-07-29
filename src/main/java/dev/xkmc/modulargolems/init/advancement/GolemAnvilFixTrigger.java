package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.l2core.serial.advancements.BaseCriterion;
import dev.xkmc.l2core.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class GolemAnvilFixTrigger extends BaseCriterion<GolemAnvilFixTrigger.Ins, GolemAnvilFixTrigger> {

	public static Ins ins() {
		return new Ins();
	}

	public static Ins withMat(ResourceLocation mat) {
		Ins ans = ins();
		ans.rl = mat;
		return ans;
	}

	public GolemAnvilFixTrigger() {
		super(Ins.class);
	}

	public void trigger(ServerPlayer player, ResourceLocation mat) {
		this.trigger(player, e -> e.rl == null || e.rl.equals(mat));
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, GolemAnvilFixTrigger> {

		@Nullable
		@SerialField
		private ResourceLocation rl = null;

		protected Ins() {
			super(GolemTriggers.ANVIL_FIX.get());
		}
	}

}
