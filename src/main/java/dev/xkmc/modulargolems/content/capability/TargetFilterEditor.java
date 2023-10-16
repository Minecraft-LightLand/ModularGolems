package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.modulargolems.content.menu.ghost.ReadOnlyContainer;
import net.minecraft.world.item.ItemStack;

public record TargetFilterEditor(GolemConfigEditor editor) implements ReadOnlyContainer {

	public TargetFilterLine targetHostile() {
		return new TargetFilterLine(editor, editor.entry().targetFilter.hostileTo, 0);
	}

	public TargetFilterLine targetFriendly() {
		return new TargetFilterLine(editor, editor.entry().targetFilter.friendlyTo, TargetFilterConfig.LINE);
	}

	private TargetFilterConfig getConfig() {
		return editor.entry().targetFilter;
	}


	@Override
	public int getContainerSize() {
		return TargetFilterConfig.LINE * 2;
	}

	@Override
	public boolean isEmpty() {
		return getConfig().hostileTo.size() == 0 && getConfig().friendlyTo.size() == 0;
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot < TargetFilterConfig.LINE ? targetHostile().getItem(slot) : targetFriendly().getItem(slot);
	}
}
