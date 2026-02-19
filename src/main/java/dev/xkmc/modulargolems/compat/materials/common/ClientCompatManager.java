package dev.xkmc.modulargolems.compat.materials.common;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientCompatManager {

	public static void dispatchClientSetup() {
		for (ModDispatch dispatch : CompatManager.LIST) {
			if (dispatch.client != null)
				dispatch.client.get().dispatchClientSetup();
		}
	}

	public static void dispatchEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		for (ModDispatch dispatch : CompatManager.LIST) {
			if (dispatch.client != null)
				dispatch.client.get().dispatchEntityLayer(event);
		}
	}

}
