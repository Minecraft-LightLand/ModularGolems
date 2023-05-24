package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.LangData;
import dev.xkmc.modulargolems.init.data.ModConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.Map;

public class PickupGoal extends Goal {

	private static final int INTERVAL = 10;

	private final AbstractGolemEntity<?, ?> golem;
	private final int lv;

	private int delay = 0;

	private int destroyItemCount = 0, destroyExpCount = 0;

	public PickupGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		this.golem = golem;
		this.lv = lv;
	}

	@Override
	public boolean canUse() {
		return true;
	}

	@Override
	public void tick() {
		if (delay > 0) {
			delay--;
			return;
		}
		delay = INTERVAL;
		AABB box = golem.getBoundingBox().inflate(lv * 8);
		tryHandleItem(box);
		tryHandleExp(box);
	}

	private void tryHandleItem(AABB box) {
		Player player = golem.getOwner();
		var items = golem.level.getEntities(EntityTypeTest.forClass(ItemEntity.class),
				box, e -> true);
		for (var item : items) {
			if (player != null) {
				item.playerTouch(player);
			}
			if (item.isRemoved()) {
				continue;
			}
			handleLeftoverItem(item);
		}
		if (destroyItemCount > 0) {
			if (player != null) {
				ModularGolems.LOGGER.info(LangData.DESTROY_ITEM.get(golem, destroyItemCount).getString());
				player.sendSystemMessage(LangData.DESTROY_ITEM.get(golem, destroyItemCount));
				destroyItemCount = 0;
			}
		}
	}

	private void tryHandleExp(AABB box) {
		Player player = golem.getOwner();
		var exps = golem.level.getEntities(EntityTypeTest.forClass(ExperienceOrb.class),
				box, e -> true);
		for (var exp : exps) {
			exp.value = repairGolemAndItems(exp.value);
			if (player != null) {
				player.takeXpDelay = 0;
				exp.playerTouch(player);
			}
			if (exp.isRemoved()) {
				continue;
			}
			handleLeftoverExp(exp);
		}
		if (destroyExpCount > 0) {
			if (player != null) {
				player.sendSystemMessage(LangData.DESTROY_EXP.get(golem, destroyExpCount));
				destroyExpCount = 0;
			}
		}
	}

	private int repairGolemAndItems(int exp) {
		Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomItemWith(Enchantments.MENDING, golem, ItemStack::isDamaged);
		if (entry != null) {
			ItemStack itemstack = entry.getValue();
			float ratio = itemstack.getXpRepairRatio();
			int recovered = Math.min((int) (exp * ratio), itemstack.getDamageValue());
			itemstack.setDamageValue(itemstack.getDamageValue() - recovered);
			int remain = ratio <= 0 ? 0 : (int) Math.max(0, exp - recovered / ratio);
			return remain > 0 ? this.repairGolemAndItems(remain) : 0;
		}
		if (!golem.hasFlag(GolemFlags.MENDING)) {
			return exp;
		}
		float lost = golem.getMaxHealth() - golem.getHealth();
		float ratio = ModConfig.COMMON.mendingXpCost.get();
		float heal = Math.min(lost, exp / ratio);
		int cost = (int) (heal * ratio);
		golem.heal(heal);
		return exp - cost;
	}

	private void handleLeftoverItem(ItemEntity item) {
		if (golem.hasFlag(GolemFlags.NO_DESTROY) || item.hasPickUpDelay()) {
			return;
		}
		destroyItemCount += item.getItem().getCount();
		item.discard();
	}

	private void handleLeftoverExp(ExperienceOrb exp) {
		destroyExpCount += exp.value;
		exp.discard();
	}

}
