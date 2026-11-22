package dev.xkmc.modulargolems.editor.util;

import dev.xkmc.modulargolems.editor.base.EditorSaveState;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ModularGolems.MODID, value = Dist.CLIENT)
public class EditorReloadHooks {

	@SubscribeEvent
	public static void onTagsUpdated(TagsUpdatedEvent event) {
		if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
			EditorSaveState.savedFlag = false;
		}
	}

}
