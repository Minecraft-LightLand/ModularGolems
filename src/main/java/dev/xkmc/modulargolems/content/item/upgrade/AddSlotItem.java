package dev.xkmc.modulargolems.content.item.upgrade;

import dev.xkmc.modulargolems.content.modifier.base.ModifierInstance;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;

import java.util.List;

/**
 * 直接作为升级项使用：不占升级槽，技能是 {@code AddSlotModifier}，按等级增加升级槽数量。
 * 描述沿用 {@link UpgradeItem} 的默认逻辑，显示这个技能的 tooltip 与 details。
 */
public class AddSlotItem extends UpgradeItem {

	public final int slot;

	public AddSlotItem(Properties properties, int slot) {
		super(properties, false);
		this.slot = slot;
	}

	@Override
	public boolean consumesSlot() {
		return false;
	}

	@Override
	public List<ModifierInstance> get() {
		return List.of(new ModifierInstance(GolemModifiers.ADD_SLOT.get(), slot));
	}

}
