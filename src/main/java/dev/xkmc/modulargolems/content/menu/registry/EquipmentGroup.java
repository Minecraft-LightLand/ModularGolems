package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabGroup;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EquipmentGroup extends GolemTabGroup<EquipmentGroup> {

	public AbstractGolemEntity<?, ?> golem;

	/**
	 * 由背包物品打开的界面：此时 golem 只是临时 dummy，标签页不能在世界里按 UUID 找实体，
	 * 需要带着物品本身与所在槽位重新打开对应界面。
	 */
	@Nullable
	public final ItemStack source;
	public final int sourceSlot;

	public EquipmentGroup(AbstractGolemEntity<?, ?> golem) {
		this(golem, null, -1);
	}

	public EquipmentGroup(AbstractGolemEntity<?, ?> golem, @Nullable ItemStack source, int sourceSlot) {
		super(GolemTabRegistry.LIST_EQUIPMENT);
		this.golem = golem;
		this.source = source;
		this.sourceSlot = sourceSlot;
	}

	public boolean isItemMode() {
		return source != null;
	}

}
