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
	// 指定事件处理的优先级为高
	@SubscribeEvent(priority = EventPriority.HIGH)
	// 右键点击方块
	public static void onItemUseOnBlock(PlayerInteractEvent.RightClickBlock event) {
		// 检查玩家使用的物品是否是模组化傀儡中的路径卡
		if (event.getItemStack().is(GolemItems.CARD_PATH.get())) {
			event.setUseItem(Event.Result.ALLOW);
			event.setUseBlock(Event.Result.DENY);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	// 右键点击实体
	public static void onItemUseOnEntity(PlayerInteractEvent.EntityInteractSpecific event) {
		// 检查玩家使用的物品是否是模组化傀儡中的实体过滤卡,且目标实体是否为LivingEntity
		if (event.getItemStack().getItem() instanceof ClickEntityFilterCard<?> card && event.getTarget() instanceof LivingEntity le) {
			// 调用卡牌的interactLivingEntity方法，处理玩家与目标实体之间的交互
			var ans = card.interactLivingEntity(event.getItemStack(), event.getEntity(), le, event.getHand());
			// 设置事件的取消结果为卡牌处理交互的结果
			event.setCancellationResult(ans);
			// 并取消该事件
			event.setCanceled(true);
		}
	}

}
