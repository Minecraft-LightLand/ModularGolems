package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

/**
 * 形态外观：只影响渲染（傀儡本体 / 装备 / 技能特效的贴图与配色），不影响任何实际效果。
 * 由装备界面按钮切换，四种模式见 {@link #DEFAULT}/{@link #INITIAL}/{@link #FINAL}/{@link #FOLLOW}。
 */
public final class GolemFormAppearance {

	public static final int DEFAULT = 0;
	public static final int INITIAL = 1;
	public static final int FINAL = 2;
	public static final int FOLLOW = 3;

	private GolemFormAppearance() {
	}

	/**
	 * 把某套装原本的“最终形态”判定 {@code realFinal} 按当前外观模式改写。
	 * <p>默认沿用原逻辑（穿终形胸甲即最终）；跟随生命值则无视胸甲、只按血量阈值；其余两种强制。
	 */
	public static boolean visualFinal(LivingEntity user, boolean realFinal, float ratio) {
		if (!(user instanceof AbstractGolemEntity<?, ?> golem)) return realFinal;
		return switch (golem.getFormAppearance()) {
			case INITIAL -> false;
			case FINAL -> true;
			case FOLLOW -> golem.getHealth() < golem.getMaxHealth() * ratio;
			default -> realFinal;
		};
	}

	/**
	 * 该傀儡穿的胸甲是否属于“有第二套外观、且解放完整力量”的套装。
	 * 目前只有：LM 浮云 / 堕落圣骑、灾变腾炎。
	 */
	public static boolean hasSecondAppearanceChest(AbstractGolemEntity<?, ?> golem) {
		ItemStack stack = golem.getItemBySlot(EquipmentSlot.CHEST);
		if (stack.isEmpty()) return false;
		if (ModList.get().isLoaded("legendary_monsters") && LMArmorHelper.isSecondAppearanceChest(stack)) return true;
		if (ModList.get().isLoaded("cataclysm") && CataArmorHelper.isSecondAppearanceChest(stack)) return true;
		return false;
	}

	public static Component modeName(int mode) {
		return switch (mode) {
			case INITIAL -> MGLangData.FORM_APPEARANCE_INITIAL.get();
			case FINAL -> MGLangData.FORM_APPEARANCE_FINAL.get();
			case FOLLOW -> MGLangData.FORM_APPEARANCE_FOLLOW.get();
			default -> MGLangData.FORM_APPEARANCE_DEFAULT.get();
		};
	}

}
