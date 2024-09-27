package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.entity.humanoid.skin.ClientProfileManager;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.SpecialRenderProfile;
import dev.xkmc.modulargolems.events.event.HumanoidSkinEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;


@EventBusSubscriber(value = Dist.CLIENT, modid = ModularGolems.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GolemClientEventListeners {

	@SubscribeEvent
	public static void onHumanoidSkin(HumanoidSkinEvent event) {
		if (event.getStack().is(Items.PLAYER_HEAD)) {
			String name = event.getStack().getHoverName().getString();
			var rl = ResourceLocation.tryParse(name);
			if (rl != null)
				event.setSkin(new SpecialRenderProfile(true, rl));
		}
		if (event.getStack().is(Items.PIGLIN_HEAD)) {
			String name = event.getStack().getHoverName().getString();
			var rl = ResourceLocation.tryParse(name);
			if (rl != null)
				event.setSkin(new SpecialRenderProfile(false, rl));
		}
		if (event.getStack().is(MGTagGen.PLAYER_SKIN)) {
			event.setSkin(ClientProfileManager.get(event.getStack().getHoverName().getString()));
		}
	}

}
