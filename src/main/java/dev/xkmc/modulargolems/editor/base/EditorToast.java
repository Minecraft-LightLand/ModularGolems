package dev.xkmc.modulargolems.editor.base;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class EditorToast {

	public static void show(Component title, Component message) {
		Minecraft.getInstance().gui.setOverlayMessage(title.copy().append(" ").append(message), false);
	}

}
