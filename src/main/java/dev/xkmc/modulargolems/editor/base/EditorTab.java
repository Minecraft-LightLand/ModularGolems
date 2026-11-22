package dev.xkmc.modulargolems.editor.base;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public record EditorTab(Component label, @Nullable Component tooltip, Runnable onSelect) {

	public EditorTab(Component label, Runnable onSelect) {
		this(label, null, onSelect);
	}

}
