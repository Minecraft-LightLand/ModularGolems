package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.events.event.GolemHandleExpEvent;
import dev.xkmc.modulargolems.events.event.GolemHandleItemEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.mixin.ExperienceOrbAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Map;

public class PickupGoal extends Goal {

	private static final int INTERVAL = 10;

	private final AbstractGolemEntity<?, ?> golem;
	private final int lv;

	private int delay = 0;
	private int destroyItemCount = 0, destroyExpCount = 0;
	private BlockEntity target;

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
		AABB box = golem.getBoundingBox().inflate(lv * MGConfig.COMMON.basePickupRange.get());
		tryHandleItem(box);
		tryHandleExp(box);
	}

	private void tryHandleItem(AABB box) {
		Player player = golem.getOwner();
		var items = golem.level().getEntities(EntityTypeTest.forClass(ItemEntity.class),
				box, e -> true);
		validateTarget();
		for (var item : items) {
			handleLeftoverItem(item, player);
		}
		if (destroyItemCount > 0) {
			if (player != null) {
				ModularGolems.LOGGER.info(MGLangData.DESTROY_ITEM.get(golem, destroyItemCount).getString());
				player.sendSystemMessage(MGLangData.DESTROY_ITEM.get(golem, destroyItemCount));
				destroyItemCount = 0;
			}
		}
	}

	private void tryHandleExp(AABB box) {
		Player player = golem.getOwner();
		var exps = golem.level().getEntities(EntityTypeTest.forClass(ExperienceOrb.class),
				box, e -> true);
		ExperienceOrb first = null;
		for (var exp : exps) {
			exp.value = exp.value * ((ExperienceOrbAccessor) exp).getCount();
			((ExperienceOrbAccessor) exp).setCount(1);
			if (first == null) {
				first = exp;
			} else {
				first.value += exp.value;
				exp.discard();
			}
		}
		if (first != null) {
			handleLeftoverExp(first, player);
		}
		if (destroyExpCount > 0) {
			if (player != null) {
				player.sendSystemMessage(MGLangData.DESTROY_EXP.get(golem, destroyExpCount));
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
		float ratio = MGConfig.COMMON.mendingXpCost.get();
		float heal = Math.min(lost, exp / ratio);
		int cost = (int) (heal * ratio);
		golem.heal(heal);
		return exp - cost;
	}

	private void handleLeftoverItem(ItemEntity item, @Nullable Player player) {
		GolemHandleItemEvent event = new GolemHandleItemEvent(golem, item);
		MinecraftForge.EVENT_BUS.post(event);
		if (item.getItem().isEmpty()) {
			item.discard();
		}
		if (item.isRemoved()) {
			return;
		}
		if (target != null) {
			var opt = target.getCapability(ForgeCapabilities.ITEM_HANDLER);
			if (opt.resolve().isPresent()) {
				var handler = opt.resolve().get();
				ItemStack remain = ItemHandlerHelper.insertItem(handler, item.getItem(), false);
				if (remain.isEmpty()) {
					item.discard();
					return;
				}
				item.setItem(remain);
			}
		}
		if (player != null) {
			item.playerTouch(player);
		}
		if (item.isRemoved()) {
			return;
		}
		if (golem.hasFlag(GolemFlags.NO_DESTROY) || item.hasPickUpDelay()) {
			return;
		}
		destroyItemCount += item.getItem().getCount();
		item.discard();
	}

	private void handleLeftoverExp(ExperienceOrb exp, @Nullable Player player) {
		exp.value = repairGolemAndItems(exp.value);
		GolemHandleExpEvent event = new GolemHandleExpEvent(golem, exp);
		MinecraftForge.EVENT_BUS.post(event);
		if (exp.value <= 0) {
			exp.discard();
		}
		if (exp.isRemoved()) {
			return;
		}
		if (player != null) {
			player.takeXpDelay = 0;
			exp.playerTouch(player);
		}
		if (exp.isRemoved()) {
			return;
		}
		destroyExpCount += exp.value;
		exp.discard();
	}

	private void validateTarget() {
		if (target != null && !target.isRemoved()) {
			return;
		}
		target = null;
		BlockPos origin = golem.blockPosition();
		BlockPos.MutableBlockPos pos = origin.mutable();
		int r = 4;
		double dist = Double.POSITIVE_INFINITY;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					pos.setWithOffset(origin, i, j, k);
					if (!golem.level().getBlockState(pos).is(MGTagGen.POTENTIAL_DST)) {
						continue;
					}
					BlockEntity be = golem.level().getBlockEntity(pos);
					if (be != null && be.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().isPresent()) {
						double d = pos.distToCenterSqr(golem.position());
						if (d < dist) {
							target = be;
							dist = d;
						}
					}
				}
			}
		}
	}

}
