package dev.xkmc.modulargolems.content.menu.wheel;

import dev.xkmc.l2itemselector.wheel.DefaultKeyHandler;
import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import net.minecraft.world.entity.player.Player;

public class GolemWheelKeyHandler extends DefaultKeyHandler.Fast {

	@Override
	public void rightClick(WheelAdaptor<?> wheel, Player player) {
		if (GolemWheelHandler.press) {
			GolemWheelHandler.press = false;
			return;
		}
		super.rightClick(wheel, player);
	}

}
