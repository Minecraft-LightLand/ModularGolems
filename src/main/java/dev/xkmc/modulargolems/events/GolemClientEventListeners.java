package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.entity.humanoid.skin.ClientProfileManager;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.SpecialRenderProfile;
import dev.xkmc.modulargolems.events.event.HumanoidSkinEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;


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

	private static int shift = 0;
	private static int count = 0;
	private static boolean shifted = false, found = false;

	@SubscribeEvent()
	public static void onGuiRender(RenderGuiEvent.Pre event) {
		count = 0;
		found = false;
		shifted = false;
	}


	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onGuiLayerRender(RenderGuiLayerEvent.Pre event) {
		if (!shifted) {
			event.getGuiGraphics().pose().translate(0, 0, -200 * shift);
			shifted = true;
		}
		if (found) return;
		if (event.getName().equals(VanillaGuiLayers.HOTBAR)) {
			shift = count;
			found = true;
		} else {
			count++;
		}
	}

}
