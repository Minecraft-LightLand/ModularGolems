package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.mob.MobSkinDispatch;
import dev.xkmc.modulargolems.events.event.HumanoidSkinEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;

public class ClientSkinDispatch {

	@Nullable
	public static SpecialRenderSkin get(HumanoidGolemEntity entity) {
		ItemStack name = ItemStack.EMPTY;
		var curio = CurioCompatRegistry.get();
		if (curio != null) name = curio.getSkin(entity);
		var event = new HumanoidSkinEvent(entity, name);
		NeoForge.EVENT_BUS.post(event);
		if (event.getSkin() != null)
			return event.getSkin();
		var playerSkin = entity.getPlayerSkin();
		if (!playerSkin.isEmpty()) {
			if (!playerSkin.contains(":")) {
				var profile = ClientProfileManager.get(playerSkin);
				if (profile != null) return profile;
			}
			var id = ResourceLocation.tryParse(playerSkin);
			if (id != null) {
				if (BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
					var type = BuiltInRegistries.ENTITY_TYPE.get(id);
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
