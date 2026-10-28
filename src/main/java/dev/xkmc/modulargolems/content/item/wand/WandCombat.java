package dev.xkmc.modulargolems.content.item.wand;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * 手杖的战斗数值与附魔准入。
 * 写法对齐 L2Weaponry 的 WeaponItem / GenericWeaponItem：附魔按 {@link EnchantmentCategory} 整体放行，
 * 再单独扣掉锋利与横扫之刃，这样别的 mod 加的武器附魔也能直接用。
 * l2weaponry 是可选依赖，所以只照抄写法，不继承它的类。
 */
public class WandCombat {

	/**
	 * 下面是 modifier 值，不是物品面板上的最终值。玩家基础是伤害 1.0、速度 4.0，
	 * 所以最终显示值 = 基础值 + modifier。目标：万能手杖 6 伤、其余 4 伤、速度 1.4。
	 */
	public static final double BASE_ATTACK_SPEED = -2.6D;
	public static final double WAND_ATTACK_DAMAGE = 3.0D;
	public static final double OMNI_ATTACK_DAMAGE = 5.0D;

	public static boolean canApply(Enchantment enchantment) {
		if (enchantment == Enchantments.SHARPNESS) return false;
		if (enchantment == Enchantments.SWEEPING_EDGE) return false;
		return enchantment.category == EnchantmentCategory.WEAPON;
	}

	private WandCombat() {
	}

}
