package dev.xkmc.modulargolems.init;

import dev.xkmc.modulargolems.content.item.ClientHolderManager;
import dev.xkmc.modulargolems.content.item.GolemBEWLR;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class GolemClient {

	public static void onCtorClient(IEventBus bus, IEventBus eventBus) {
		bus.addListener(GolemClient::clientSetup);
		bus.addListener(GolemClient::onResourceReload);
		MinecraftForge.EVENT_BUS.register(ClientHolderManager.class);
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		ClientRegister.registerItemProperties();
		ClientRegister.registerOverlays();
		ClientRegister.registerKeys();
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onResourceReload(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(GolemBEWLR.INSTANCE.get());
	}

}
