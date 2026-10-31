package dev.xkmc.modulargolems.compat.materials.botania;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ManaMendingModifier extends ManaModifier {

	/**
	 * Healing step. {@code BotUtils#consumeMana} always hands back a whole multiple of the mana cost,
	 * so this is also the smallest amount a single mending tick can restore.
	 */
	public static final double HEAL_STEP = 1d;

	public ManaMendingModifier() {
		super(StatFilterType.HEALTH, MAX_LEVEL);
	}

	@Override
	public double onHealTick(double heal, AbstractGolemEntity<?, ?> le, int level) {
		var healthDiff = le.getMaxHealth() - le.getGuardedDataImpl() - heal;
		var cost = MGConfig.COMMON.manaMendingCost.get();
		int maxHeal = (int) Math.floor(Math.min(healthDiff, MGConfig.COMMON.manaMendingVal.get() * level));
		if (maxHeal <= 0) return heal;
		int maxCost = maxHeal * cost;
		int consume = new BotUtils(le).consumeMana(maxCost);
		double toHeal = HEAL_STEP * consume / cost;
		return heal + toHeal;
	}

	public List<MutableComponent> getDetail(int v) {
		int eff = MGConfig.COMMON.manaMendingCost.get();
		double val = MGConfig.COMMON.manaMendingVal.get() * v;
		var cval = Component.literal(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(val)).withStyle(ChatFormatting.AQUA);
		var ceff = Component.literal("" + eff).withStyle(ChatFormatting.AQUA);
		var cstep = Component.literal("" + (int) HEAL_STEP).withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", cval, ceff, cstep).withStyle(ChatFormatting.GREEN));
	}
}
