package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Arrays;

/** Triggers the omnipotent wand function bound to the pressed key while an omnipotent wand is held. */
@EventBusSubscriber(value = Dist.CLIENT, modid = ModularGolems.MODID, bus = EventBusSubscriber.Bus.GAME)
public class OmniWandKeyHandler {

	private static final int REPEAT_DELAY = 4;

	private static final int[] cooldown = new int[OmniWandKeys.values().length];

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		if (Minecraft.getInstance().screen != null) {
			Arrays.fill(cooldown, 0);
			return;
		}
		var keys = OmniWandKeys.values();
		for (int i = 0; i < keys.length; i++) {
			if (cooldown[i] > 0) {
				cooldown[i]--;
				continue;
			}
			if (keys[i].map.isDown()) {
				fire(keys[i]);
				cooldown[i] = REPEAT_DELAY;
			}
		}
	}

	private static void fire(OmniWandKeys key) {
		var player = Proxy.getClientPlayer();
		if (player == null) return;
		InteractionHand hand = null;
		for (var candidate : InteractionHand.values()) {
			if (OmniWandFunction.of(player.getItemInHand(candidate)) != null) {
				hand = candidate;
				break;
			}
		}
		if (hand == null) return;
		var func = OmniWandFunction.byOrdinal(key.ordinal());
		if (func == null) return;
		player.swing(hand);
		func.item().useFromKey(player.level(), player, hand);
		ModularGolems.HANDLER.toServer(new OmniWandKeyToServer(func.ordinal()));
	}

}
