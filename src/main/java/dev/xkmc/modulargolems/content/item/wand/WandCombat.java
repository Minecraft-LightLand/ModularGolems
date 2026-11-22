package dev.xkmc.modulargolems.content.item.wand;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 手杖的战斗数值与附魔准入。
 * 1.21 移除了 {@code EnchantmentCategory}，改为按附魔自身的数据（supported_items）判定适用性，
 * 因此不再按 category 整体放行，直接用 {@link Enchantment#canEnchant(ItemStack)}。
 */
public class WandCombat {

	/**
	 * 下面是 modifier 值，不是物品面板上的最终值。玩家基础是伤害 1.0、速度 4.0，
	 * 所以最终显示值 = 基础值 + modifier。目标：万能手杖 6 伤、其余 4 伤、速度 1.4。
	 */
	public static final double BASE_ATTACK_SPEED = -2.6D;
	public static final double WAND_ATTACK_DAMAGE = 3.0D;
	public static final double OMNI_ATTACK_DAMAGE = 5.0D;

	public static boolean canApply(Enchantment enchantment, ItemStack stack) {
		return enchantment.canEnchant(stack);
	}

	private WandCombat() {
	}

}
