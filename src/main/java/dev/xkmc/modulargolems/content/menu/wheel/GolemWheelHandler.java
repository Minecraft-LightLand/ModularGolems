package dev.xkmc.modulargolems.content.menu.wheel;

import dev.xkmc.l2itemselector.wheel.WheelHandler;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class GolemWheelHandler {

	public static boolean press = false;

	public static boolean enableWheel(Player player, AbstractGolemEntity<?, ?> golem) {
		Minecraft.getInstance().options.keyUse.setDown(false);
		if (WheelHandler.wheel instanceof GolemModeWheel)
			return false;
		if (WheelHandler.wheel != null) WheelHandler.wheel.onClose();
		WheelHandler.wheelIndex = 0;
		WheelHandler.wheel = new GolemModeWheel(golem);
		WheelHandler.wheel.onOpen();
		WheelHandler.keyboardIndex = -1;
		Minecraft.getInstance().mouseHandler.releaseMouse();
		press = true;
		return true;
	}

}
