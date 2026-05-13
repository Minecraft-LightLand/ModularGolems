package dev.xkmc.modulargolems.init;

import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.common.ClientCompatManager;
import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.client.overlay.GolemStatusOverlay;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import dev.xkmc.modulargolems.content.entity.skin.PlayerSkinRenderer;
import dev.xkmc.modulargolems.content.item.render.GolemFacadeRenderer;
import dev.xkmc.modulargolems.content.item.render.GolemHolderRenderer;
import dev.xkmc.modulargolems.content.item.render.GolemPartRenderer;
import dev.xkmc.modulargolems.content.item.render.IsInTag;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.table.ItemListClientTooltip;
import dev.xkmc.modulargolems.content.menu.table.ItemListTooltip;
import dev.xkmc.modulargolems.util.EsterEggUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(value = Dist.CLIENT, modid = ModularGolems.MODID)
public class GolemClient {

	@SubscribeEvent
	public static void registerItemModelProperty(RegisterConditionalItemModelPropertyEvent event) {
		event.register(ModularGolems.loc("tag"), IsInTag.MAP_CODEC);
	}

	@SubscribeEvent
	public static void registerSpecialModel(RegisterSpecialModelRendererEvent event) {
		event.register(ModularGolems.loc("holder"), GolemHolderRenderer.Unbaked.MAP_CODEC);
		event.register(ModularGolems.loc("part"), GolemPartRenderer.Unbaked.MAP_CODEC);
		event.register(ModularGolems.loc("facade"), GolemFacadeRenderer.Unbaked.MAP_CODEC);
	}

	private static final boolean ENABLE_TLM = true;//TODO

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		//if (ENABLE_TLM && ModList.get().isLoaded(TouhouLittleMaid.MOD_ID)) NeoForge.EVENT_BUS.register(MaidSkinCompat.class);

		event.enqueueWork(() -> {
			ClientCompatManager.dispatchClientSetup();

			GolemTabRegistry.register();
			CurioCompatRegistry.clientRegister();
			EsterEggUtil.registerEsterEggTextures();
		});
	}

	@SubscribeEvent
	public static void registerOverlays(RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.CROSSHAIR, ModularGolems.loc("golem_stats"), new GolemStatusOverlay());
	}

	@SubscribeEvent
	public static void registerArmorLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		GolemEquipmentModels.registerArmorLayer(event);
		ClientCompatManager.dispatchEntityLayer(event);
	}

	@SubscribeEvent
	public static void onResourceReload(AddClientReloadListenersEvent event) {
		ModelOverrides.reload();
	}

	@SubscribeEvent
	public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
		PlayerSkinRenderer.SLIM = new PlayerSkinRenderer(event.getContext(), true);
		PlayerSkinRenderer.REGULAR = new PlayerSkinRenderer(event.getContext(), false);
		// if (ENABLE_TLM && ModList.get().isLoaded(TouhouLittleMaid.MOD_ID)) MaidSkinCompat.addLayers(event);
	}

	@SubscribeEvent
	public static void registerClientTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(ItemListTooltip.class, ItemListClientTooltip::new);
	}

}
