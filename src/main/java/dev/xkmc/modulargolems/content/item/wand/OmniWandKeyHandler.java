package dev.xkmc.modulargolems.content.item.wand;

import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;

/**
 * Triggers the omnipotent wand function bound to the pressed key. While an omnipotent wand is held, pressing the key
 * for a function triggers that function's right click, and sneaking while pressing it triggers the sneak right click
 * (the shift state is read on the server from the player's real input).
 *
 * <p>Reading {@code isDown()} rather than the key event means the first tick fires immediately and holding the key
 * keeps repeating, the same way holding right click keeps swinging, instead of firing only once per press.
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModularGolems.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OmniWandKeyHandler {

	/** Ticks between repeats while a key is held, matching how often right click can be re-sent. */
	private static final int REPEAT_DELAY = 4;

	private static final int[] cooldown = new int[OmniWandKeys.values().length];

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
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
		// client side first, so purely client actions such as opening the mode wheel behave like a real right click
		func.item().useFromKey(player.level(), player, hand);
		ModularGolems.HANDLER.toServer(new OmniWandKeyToServer(func));
	}

}
