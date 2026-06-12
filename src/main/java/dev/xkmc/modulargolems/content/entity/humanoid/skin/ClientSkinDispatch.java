package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.events.event.HumanoidSkinEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public class ClientSkinDispatch {

	@Nullable
	public static SpecialRenderSkin get(HumanoidGolemEntity entity) {
		ItemStack name = ItemStack.EMPTY;
		var curio = CurioCompatRegistry.get();
		if (curio != null) name = curio.getSkin(entity);
		var event = new HumanoidSkinEvent(entity, name);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getSkin() != null)
			return event.getSkin();
		var playerSkin = entity.getPlayerSkin();
		if (!playerSkin.isEmpty()) {
			var profile = ClientProfileManager.get(playerSkin);
			if (profile != null) return profile;
			if (ResourceLocation.isValidResourceLocation(playerSkin)) {
				return new SpecialRenderProfile(false, new ResourceLocation(playerSkin));
			}
		}
		return null;
	}

}
