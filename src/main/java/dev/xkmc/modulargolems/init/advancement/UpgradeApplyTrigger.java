package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.l2library.serial.advancements.BaseCriterion;
import dev.xkmc.l2library.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class UpgradeApplyTrigger extends BaseCriterion<UpgradeApplyTrigger.Ins, UpgradeApplyTrigger> {

	public static Ins ins() {
		return new Ins(GolemTriggers.UPGRADE_APPLY.getId(), ContextAwarePredicate.ANY);
	}

	public static Ins withUpgrade(UpgradeItem item) {
		Ins ans = ins();
		ans.ingredient = Ingredient.of(item);
		return ans;
	}

	public static Ins withRemain(int remain) {
		Ins ans = ins();
		ans.remain = remain;
		return ans;
	}

	public static Ins withTotal(int total) {
		Ins ans = ins();
		ans.total = total;
		return ans;
	}

	public UpgradeApplyTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player, ItemStack upgrade, int remain, int total) {
		this.trigger(player, e -> (e.ingredient.isEmpty() || e.ingredient.test(upgrade)) &&
				(e.remain < 0 || e.remain >= remain) &&
				(e.total < 0 || e.total <= total));
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, UpgradeApplyTrigger> {

		@SerialClass.SerialField
		private Ingredient ingredient = Ingredient.EMPTY;

		@SerialClass.SerialField
		private int remain = -1, total = -1;

		public Ins(ResourceLocation id, ContextAwarePredicate player) {
			super(id, player);
		}

	}

}
