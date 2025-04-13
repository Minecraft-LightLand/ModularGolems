package dev.xkmc.modulargolems.compat.materials.goety.modifier;

import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.SEHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SoulRepairModifier extends GolemModifier {

	public SoulRepairModifier() {
		super(StatFilterType.MASS, 10);
	}

	@Override
	public double onInventoryHealTick(double heal, HealingContext ctx, int level) {
		if (ctx.health() > ctx.maxHealth() * MGConfig.COMMON.soulHealingThreshold.get()) return heal;
		Player player = null;
		if (ctx.owner() instanceof Player pl) player = pl;
		else if (ctx.owner() instanceof AbstractGolemEntity<?, ?> golem)
			player = golem.getOwner();
		if (player == null) return heal;
		if (!SEHelper.getSoulsContainer(player)) return heal;
		int rate = ItemConfig.ItemsRepairAmount.get() * MGConfig.COMMON.soulHealingCost.get();
		if (rate <= 0) return heal;
		int max = Math.min(level * MGConfig.COMMON.soulHealingRate.get(), SEHelper.getSoulAmountInt(player) / rate);
		if (max <= 0) return heal;
		SEHelper.decreaseSouls(player, max * rate);
		return heal + max;
	}

	@Override
	public double onHealTick(double heal, AbstractGolemEntity<?, ?> entity, int level) {
		Player player = entity.getOwner();
		if (player != null && SEHelper.getSoulsContainer(player)) {
			int rate = ItemConfig.ItemsRepairAmount.get();
			for (var e : EquipmentSlot.values()) {
				ItemStack stack = entity.getItemBySlot(e);
				if (!stack.isEmpty() && stack.isDamaged()) {
					int max = Math.min(Math.min(stack.getDamageValue(), level), SEHelper.getSoulAmountInt(player) / rate);
					if (max > 0) {
						SEHelper.decreaseSouls(player, rate * max);
						stack.setDamageValue(stack.getDamageValue() - max);
					}
				}
			}
		}
		return super.onHealTick(heal, entity, level);
	}

}
