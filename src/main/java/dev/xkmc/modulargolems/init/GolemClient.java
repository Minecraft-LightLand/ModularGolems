package dev.xkmc.modulargolems.init;

import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.content.client.GolemStatusOverlay;
import dev.xkmc.modulargolems.content.item.UpgradeItem;
import dev.xkmc.modulargolems.content.item.golem.ClientHolderManager;
import dev.xkmc.modulargolems.content.item.golem.GolemBEWLR;
import dev.xkmc.modulargolems.init.data.TagGen;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class GolemClient {

	private static IEventBus clientBus;

	public static void onCtorClient(IEventBus bus, IEventBus eventBus) {
		clientBus = bus;
		bus.addListener(GolemClient::clientSetup);
		bus.addListener(GolemClient::onResourceReload);
		bus.addListener(GolemClient::registerOverlays);
		MinecraftForge.EVENT_BUS.register(ClientHolderManager.class);
	}

	public static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ClampedItemPropertyFunction func = (stack, level, entity, layer) ->
					entity != null && entity.isBlocking() && entity.getUseItem() == stack ? 1.0F : 0.0F;
			ItemProperties.register(Items.SHIELD, new ResourceLocation("blocking"), func);
			ClampedItemPropertyFunction arrow = (stack, level, entity, layer) ->
					stack.is(TagGen.BLUE_UPGRADES) ? 1 : 0;
			for (var item : UpgradeItem.LIST)
				ItemProperties.register(item, new ResourceLocation(ModularGolems.MODID, "blue_arrow"), arrow);
			CompatManager.dispatchClientSetup(clientBus);
		});
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "golem_stats", new GolemStatusOverlay());
	}

	@OnlyIn(Dist.CLIENT)
	public static void onResourceReload(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(GolemBEWLR.INSTANCE.get());
	}

}
