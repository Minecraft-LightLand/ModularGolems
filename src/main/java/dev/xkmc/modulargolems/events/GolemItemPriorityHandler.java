package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.item.card.ClickEntityFilterCard;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ModularGolems.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GolemItemPriorityHandler {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onItemUseOnBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getItemStack().is(GolemItems.CARD_PATH.get())) {
			event.setUseItem(TriState.TRUE);
			event.setUseBlock(TriState.FALSE);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onItemUseOnEntity(PlayerInteractEvent.EntityInteractSpecific event) {
		if (event.getItemStack().getItem() instanceof ClickEntityFilterCard<?> card && event.getTarget() instanceof LivingEntity le) {
			var ans = card.interactLivingEntity(event.getItemStack(), event.getEntity(), le, event.getHand());
			event.setCancellationResult(ans);
			event.setCanceled(true);
		}
	}

}
