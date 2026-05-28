package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.client.outline.BlockOutliner;
import dev.xkmc.modulargolems.content.menu.table.TableTab;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabBase;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModularGolems.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GolemClientEventListeners {

	@SubscribeEvent
	public static void renderStageEvent(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
			BlockOutliner.renderOutline(event.getPoseStack(), event.getCamera().getPosition());
		}
	}

	@SubscribeEvent
	public static void onInitScreen(ScreenEvent.Init.Post event) {
		if (TableTab.lastOpened != null && TableTab.level != null) {
			long time = TableTab.level.getGameTime();
			if (TableTab.level == Minecraft.getInstance().level &&
					TableTab.time + 60 >= time &&
					TableTab.time <= time &&
					event.getScreen() instanceof AbstractContainerScreen<?> acs &&
					acs.getMenu().getType() == TableTab.lastOpened.menu) {
				TableTab.initScreen(TableTab.lastOpened, acs, event::addListener);
			}
			TableTab.lastOpened = null;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onBGRender(ScreenEvent.Render.Post event) {
		if (event.getScreen() instanceof AbstractContainerScreen<?> cont) {
			for (var e : cont.renderables) {
				if (e instanceof GolemTabBase<?, ?> base) {
					base.reposition(cont.getGuiLeft(), cont.getGuiTop());
				}
			}
		}
	}

}
