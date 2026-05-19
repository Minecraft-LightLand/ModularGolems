
package dev.xkmc.modulargolems.debug;

import dev.xkmc.modulargolems.events.event.GolemInfoEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClientDebugInfo {

	private static int id;
	private static List<String> info;

	public static void handle(int golem, List<String> list) {
		id = golem;
		info = list;
	}

	public static void append(GolemInfoEvent event) {
		if (event.getGolem().getId() == id) {
			for (var e : info)
				event.addLine(Component.literal(e).withStyle(ChatFormatting.GRAY));
		}
	}

}
