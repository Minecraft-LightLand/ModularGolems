package dev.xkmc.modulargolems.editor.util;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModularGolems.MODID, value = Dist.CLIENT)
public class EditorReloadHooks {

	@SubscribeEvent
	public static void onTagsUpdated(TagsUpdatedEvent event) {
		if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
			EditorData.savedFlag = false;
		}
	}

}
