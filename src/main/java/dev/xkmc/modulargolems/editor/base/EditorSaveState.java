package dev.xkmc.modulargolems.editor.base;

import net.minecraft.client.Minecraft;

public class EditorSaveState {

	public static boolean savedFlag;

	public static boolean canEdit() {
		Minecraft mc = Minecraft.getInstance();
		return mc.getSingleplayerServer() != null
				&& mc.getSingleplayerServer().getWorldData().getAllowCommands()
				&& mc.player != null && mc.player.isCreative();
	}

}
