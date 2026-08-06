package dev.xkmc.modulargolems.editor.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public class EditorToast {

	public static void show(Component title, Component message) {
		SystemToast.add(Minecraft.getInstance().getToasts(), SystemToast.SystemToastIds.TUTORIAL_HINT, title, message);
	}

}
