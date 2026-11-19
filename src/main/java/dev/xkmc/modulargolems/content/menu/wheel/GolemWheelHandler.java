package dev.xkmc.modulargolems.content.menu.wheel;

import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import dev.xkmc.l2itemselector.wheel.WheelHandler;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

public class GolemWheelHandler {

	public static boolean press = false;
	/** 长按右键开轮盘的阈值，与轮盘库 DefaultKeyHandler 一致。 */
	private static final long LONG_PRESS_MS = 200;

	private static AbstractGolemEntity<?, ?> pendingGolem;
	private static long pendingSince;

	/**
	 * 命令手杖客户端右键傀儡时调用。
	 * <p>Fast 模式或全命令手杖按键路径（没有按住真实右键）→ 立即开轮盘；
	 * Switcher 模式 + 真实右键 → 先挂起：短按松开切下一个模式，长按才开轮盘。
	 */
	public static boolean onClientRightClick(Player player, AbstractGolemEntity<?, ?> golem) {
		// Fast 模式：保持原来的“右键立即开轮盘”
		if (!L2ISConfig.CLIENT.useFastSwitchWheel.get()) {
			return enableWheel(player, golem);
		}
		// Switcher 模式但走的是全命令手杖按键（没有按住真实右键）：直接开轮盘
		if (!rightMouseDown()) {
			return openWheel(player, golem, false);
		}
		if (pendingGolem == null) {
			pendingGolem = golem;
			pendingSince = System.currentTimeMillis();
		} else {
			pendingGolem = golem;
		}
		return true;
	}

	/** 每客户端 tick 检查挂起的右键：松开=切模式，按住超过阈值=开轮盘。 */
	public static void tickPending(Player player) {
		if (pendingGolem == null) return;
		if (Minecraft.getInstance().screen != null || WheelHandler.wheel != null) {
			pendingGolem = null;
			return;
		}
		if (!rightMouseDown()) {
			var golem = pendingGolem;
			pendingGolem = null;
			cycleNextMode(golem);
			return;
		}
		if (System.currentTimeMillis() - pendingSince >= LONG_PRESS_MS) {
			var golem = pendingGolem;
			pendingGolem = null;
			openWheel(player, golem, false);
		}
	}

	private static boolean rightMouseDown() {
		return GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(),
				GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
	}

	private static void cycleNextMode(AbstractGolemEntity<?, ?> golem) {
		if (golem == null || !golem.isAlive()) return;
		int size = GolemModes.LIST.size();
		if (size <= 0) return;
		int next = Math.floorMod(golem.getMode().getID() + 1, size);
		ModularGolems.HANDLER.toServer(GolemSetModeToServer.of(golem, next));
	}

	public static boolean enableWheel(Player player, AbstractGolemEntity<?, ?> golem) {
		return openWheel(player, golem, true);
	}

	private static boolean openWheel(Player player, AbstractGolemEntity<?, ?> golem, boolean pressFlag) {
		Minecraft.getInstance().options.keyUse.setDown(false);
		if (WheelHandler.wheel instanceof GolemModeWheel)
			return false;
		if (WheelHandler.wheel != null) WheelHandler.wheel.onClose();
		WheelHandler.wheelIndex = 0;
		WheelHandler.wheel = new GolemModeWheel(golem);
		WheelHandler.wheel.onOpen();
		WheelHandler.keyboardIndex = -1;
		Minecraft.getInstance().mouseHandler.releaseMouse();
		press = pressFlag;
		pendingGolem = null;
		return true;
	}

}
