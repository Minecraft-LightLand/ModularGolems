package dev.xkmc.modulargolems.init;

import dev.xkmc.modulargolems.content.client.GolemStatusOverlay;
import dev.xkmc.modulargolems.content.item.golem.ClientHolderManager;
import dev.xkmc.modulargolems.content.item.golem.GolemBEWLR;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class GolemClient {

	public static void onCtorClient(IEventBus bus, IEventBus eventBus) {
		bus.addListener(GolemClient::clientSetup);
		bus.addListener(GolemClient::onResourceReload);
		bus.addListener(GolemClient::registerOverlays);
		MinecraftForge.EVENT_BUS.register(ClientHolderManager.class);
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {

	}

	@OnlyIn(Dist.CLIENT)
	public static void registerOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "golem_stats", new GolemStatusOverlay());
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onResourceReload(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(GolemBEWLR.INSTANCE.get());
	}

}
