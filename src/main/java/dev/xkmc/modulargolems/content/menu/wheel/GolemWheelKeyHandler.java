package dev.xkmc.modulargolems.content.menu.wheel;

import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import dev.xkmc.l2itemselector.wheel.DefaultKeyHandler;
import dev.xkmc.l2itemselector.wheel.WheelAdaptor;
import dev.xkmc.l2itemselector.wheel.WheelContext;
import dev.xkmc.l2itemselector.wheel.WheelHandler;
import dev.xkmc.l2itemselector.wheel.WheelKeyHandler;
import dev.xkmc.modulargolems.content.menu.registry.OpenConfigMenuToServer;
import dev.xkmc.modulargolems.content.menu.registry.OpenEquipmentMenuToServer;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.world.entity.player.Player;

/**
 * 傀儡轮盘的两种操作方式，结构与 {@code SwapWheelKeyHandler} 一致：
 * 由 {@link L2ISConfig} 的 {@code useFastSwitchWheel} 决定用 Fast 还是 Switcher。
 * 两种模式都保留傀儡自己的两处特化：右键开轮盘后的第一次右键被吞掉，
 * 以及侧边切换改为打开配置/装备界面。
 */
public class GolemWheelKeyHandler {

	public static final Fast FAST = new Fast();
	public static final Switcher SWITCHER = new Switcher();

	static WheelKeyHandler getDefault() {
		return L2ISConfig.CLIENT.useFastSwitchWheel.get() ? SWITCHER : FAST;
	}

	private static boolean consumePress() {
		if (GolemWheelHandler.press) {
			GolemWheelHandler.press = false;
			return true;
		}
		return false;
	}

	private static void executeGolem(WheelAdaptor<?> wheel, Player player, DefaultKeyHandler.ActionCode action, WheelContext ctx) {
		if (action == DefaultKeyHandler.ActionCode.SWITCH && wheel instanceof GolemModeWheel golemWheel) {
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
	}

	public static class Fast extends DefaultKeyHandler.Fast {

		@Override
		public void rightClick(WheelAdaptor<?> wheel, Player player) {
			if (consumePress()) return;
			super.rightClick(wheel, player);
		}

		@Override
		protected void execute(WheelAdaptor<?> wheel, Player player, ActionCode action, WheelContext ctx) {
			executeGolem(wheel, player, action, ctx);
			super.execute(wheel, player, action, ctx);
		}

	}

	public static class Switcher extends DefaultKeyHandler.Switcher {

		/** 左键：点扇区=只选中不关闭；点侧边=打开配置/装备界面。 */
		@Override
		public void leftClick(WheelAdaptor<?> wheel, Player player) {
			var ctx = wheel.getContext(player, wheel.getWheelSize());
			if (ctx.code().switcher() != 0) {
				executeGolem(wheel, player, DefaultKeyHandler.ActionCode.SWITCH, ctx);
				WheelHandler.disableWheel(player);
				return;
			}
			if (ctx.hover() >= 0) wheel.select(ctx.hover());
		}

		/** 右键（松开）：点扇区=选中并关闭；中心空白=关闭；侧边=打开配置/装备界面。 */
		@Override
		public void rightClick(WheelAdaptor<?> wheel, Player player) {
			if (consumePress()) return;
			var ctx = wheel.getContext(player, wheel.getWheelSize());
			if (ctx.code().switcher() != 0) {
				executeGolem(wheel, player, DefaultKeyHandler.ActionCode.SWITCH, ctx);
			} else if (ctx.hover() >= 0) {
				wheel.select(ctx.hover());
			}
			WheelHandler.disableWheel(player);
		}

		@Override
		protected void execute(WheelAdaptor<?> wheel, Player player, ActionCode action, WheelContext ctx) {
			executeGolem(wheel, player, action, ctx);
			super.execute(wheel, player, action, ctx);
		}

		/**
		 * 指示器（扇形高亮/弧色）与 MG 版右键行为对齐：
		 * 侧边=SWITCH，扇区=SELECT，中心=CLOSE；空闲(RELEASE)时仅侧边/扇区给出提示。
		 */
		@Override
		public ActionCode getAction(WheelContext ctx, ActionInput input) {
			boolean side = ctx.code().switcher() != 0;
			return switch (input) {
				case RELEASE -> side ? ActionCode.SWITCH : ctx.hover() >= 0 ? ActionCode.SELECT : ActionCode.CLOSE;
				case LEFT -> side ? ActionCode.SWITCH : ctx.hover() >= 0 ? ActionCode.SELECT : ActionCode.NONE;
				case RIGHT -> side ? ActionCode.SWITCH : ctx.hover() >= 0 ? ActionCode.SELECT : ActionCode.CLOSE;
			};
		}

	}

}
