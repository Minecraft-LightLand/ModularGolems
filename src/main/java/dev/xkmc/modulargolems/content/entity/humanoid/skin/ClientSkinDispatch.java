package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.events.event.HumanoidSkinEvent;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public class ClientSkinDispatch {

	@Nullable
	public static SpecialRenderSkin get(HumanoidGolemEntity entity) {
		var curio = CurioCompatRegistry.get();
		if (curio == null) return null;
		var name = curio.getSkin(entity);
		if (name.isEmpty()) return null;
		var event = new HumanoidSkinEvent(entity, name);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getSkin();
	}

}
