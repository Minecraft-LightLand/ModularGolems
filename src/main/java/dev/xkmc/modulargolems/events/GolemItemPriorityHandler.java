package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.item.card.ClickEntityFilterCard;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModularGolems.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GolemItemPriorityHandler {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onItemUseOnBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getItemStack().is(GolemItems.CARD_PATH.get())) {
			event.setUseItem(Event.Result.ALLOW);
			event.setUseBlock(Event.Result.DENY);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onItemUseOnEntity(PlayerInteractEvent.EntityInteractSpecific event) {
		if (event.getItemStack().getItem() instanceof ClickEntityFilterCard<?> card && event.getTarget() instanceof LivingEntity le) {
			var ans = card.interactLivingEntity(event.getItemStack(), event.getEntity(), le, event.getHand());
			event.setCancellationResult(ans);
			event.setCanceled(true);
		}
	}

}
