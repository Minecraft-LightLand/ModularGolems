package dev.xkmc.modulargolems.compat.materials.common;

import net.minecraftforge.client.event.EntityRenderersEvent;

public class ClientCompatManager {

	public static void dispatchClientSetup() {
		for (ModDispatch dispatch : CompatManager.LIST) {
			dispatch.client.resolve().ifPresent(ClientModDispatch::dispatchClientSetup);
		}
	}

	public static void dispatchEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		for (ModDispatch dispatch : CompatManager.LIST) {
			dispatch.client.resolve().ifPresent(e -> e.dispatchEntityLayer(event));
		}
	}

}
