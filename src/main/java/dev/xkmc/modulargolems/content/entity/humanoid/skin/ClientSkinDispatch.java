package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.events.event.HumanoidSkinEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public class ClientSkinDispatch {

	@Nullable
	public static SpecialRenderSkin get(HumanoidGolemEntity entity) {
		var event = new HumanoidSkinEvent(entity, ItemStack.EMPTY);
		if (entity.getMaidModelId().isEmpty() && entity.getPlayerSkin().isEmpty())
			return null;
		MinecraftForge.EVENT_BUS.post(event);
		var ans = event.getSkin();
		if (ans != null) return ans;
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
