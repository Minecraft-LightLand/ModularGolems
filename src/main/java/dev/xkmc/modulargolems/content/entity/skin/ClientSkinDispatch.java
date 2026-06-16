package dev.xkmc.modulargolems.content.entity.skin;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderState;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.mob.MobSkinDispatch;
import dev.xkmc.modulargolems.events.event.HumanoidSkinEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;

public class ClientSkinDispatch {

	@Nullable
	public static SpecialRenderSkin get(HumanoidGolemRenderState entity, String playerSkin) {
		ItemStack name = entity.common.skin();
		var event = new HumanoidSkinEvent(entity, name);
		NeoForge.EVENT_BUS.post(event);
		if (event.getSkin() != null)
			return event.getSkin();
		if (!playerSkin.isEmpty()) {
			if (!playerSkin.contains(":")) {
				return ClientProfileManager.get(playerSkin);
			}
			var id = Identifier.tryParse(playerSkin);
			if (id != null) {
				if (BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
					var type = BuiltInRegistries.ENTITY_TYPE.getValue(id);
					var mob = MobSkinDispatch.of(type);
					if (mob != null)
						return mob;
				}
				return new SpecialRenderProfile(false, id);
			}
		}
		return null;
	}

}
