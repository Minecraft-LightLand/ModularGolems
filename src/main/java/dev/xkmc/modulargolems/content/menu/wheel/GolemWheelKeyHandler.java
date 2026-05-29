package dev.xkmc.modulargolems.content.menu.wheel;

import dev.xkmc.l2itemselector.wheel.DefaultKeyHandler;
import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import dev.xkmc.l2itemselector.wheel.WheelContext;
import dev.xkmc.modulargolems.content.menu.registry.OpenConfigMenuToServer;
import dev.xkmc.modulargolems.content.menu.registry.OpenEquipmentMenuToServer;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
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

	@Override
	protected void execute(WheelAdaptor<?> wheel, Player player, ActionCode action, WheelContext ctx) {
		if (action == ActionCode.SWITCH && wheel instanceof GolemModeWheel golemWheel) {
			var golem = golemWheel.golem();
			if (ctx.code().switcher() < 0) {
				var entry = golem.getConfigEntry(MGLangData.LOADING.get());
				if (entry != null) {
					ModularGolems.HANDLER.toServer(new OpenConfigMenuToServer(entry.getID(), entry.getColor(), OpenConfigMenuToServer.Type.TOGGLE));
				}
			} else {
				ModularGolems.HANDLER.toServer(new OpenEquipmentMenuToServer(golem.getUUID(), OpenEquipmentMenuToServer.Type.EQUIPMENT));
			}
		}
		super.execute(wheel, player, action, ctx);
	}

}
