package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.client.outline.BlockOutliner;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.ClientProfileManager;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.SpecialRenderProfile;
import dev.xkmc.modulargolems.content.menu.table.TableTab;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabBase;
import dev.xkmc.modulargolems.events.event.HumanoidSkinEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// 处理客户端渲染的监听
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModularGolems.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GolemClientEventListeners {

	@SubscribeEvent
	public static void renderStageEvent(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
			BlockOutliner.renderOutline(event.getPoseStack(), event.getCamera().getPosition());
		}
	}

	@SubscribeEvent
	public static void onHumanoidSkin(HumanoidSkinEvent event) {
		if (event.getStack().is(Items.PLAYER_HEAD)) {
			String name = event.getStack().getHoverName().getString();
			if (ResourceLocation.isValidResourceLocation(name))
				event.setSkin(new SpecialRenderProfile(true, new ResourceLocation(name)));
		}
		if (event.getStack().is(Items.PIGLIN_HEAD)) {
			String name = event.getStack().getHoverName().getString();
			if (ResourceLocation.isValidResourceLocation(name))
				event.setSkin(new SpecialRenderProfile(false, new ResourceLocation(name)));
		}
		if (event.getStack().is(MGTagGen.PLAYER_SKIN)) {
			event.setSkin(ClientProfileManager.get(event.getStack().getHoverName().getString()));
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
