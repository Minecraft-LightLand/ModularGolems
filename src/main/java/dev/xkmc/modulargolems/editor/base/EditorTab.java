package dev.xkmc.modulargolems.editor.base;

import net.minecraft.network.chat.Component;

public record EditorTab(Component label, Runnable onSelect) {
}
