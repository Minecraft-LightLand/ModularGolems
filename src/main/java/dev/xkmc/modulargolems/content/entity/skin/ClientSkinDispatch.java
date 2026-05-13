package dev.xkmc.modulargolems.content.entity.skin;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderState;
import dev.xkmc.modulargolems.events.event.HumanoidSkinEvent;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;

public class ClientSkinDispatch {

	@Nullable
	public static SpecialRenderSkin get(HumanoidGolemRenderState entity) {
		var name = entity.common.skin();
		if (name.isEmpty()) return null;
		var event = new HumanoidSkinEvent(entity, name);
		NeoForge.EVENT_BUS.post(event);
		return event.getSkin();
	}

}
