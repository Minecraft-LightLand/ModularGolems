package dev.xkmc.modulargolems.debug;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import dev.xkmc.modulargolems.events.event.GolemInfoEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = ModularGolems.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DebugHandler {

	@SubscribeEvent
	public static void golemDebug(LivingEvent.LivingTickEvent event) {
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> golem && !golem.level().isClientSide()) {
			ArrayList<String> list = new ArrayList<>();
			DebugPacket.fill(golem, list);
			ModularGolems.HANDLER.toTrackingPlayers(new DebugPacket(golem.getId(), list), golem);
		}
	}

}
