package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.client.outline.BlockOutliner;
import dev.xkmc.modulargolems.content.entity.skin.ClientProfileManager;
import dev.xkmc.modulargolems.content.entity.skin.SpecialRenderProfile;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.mob.MobSkinDispatch;
import dev.xkmc.modulargolems.content.menu.table.TableTab;
import dev.xkmc.modulargolems.events.event.HumanoidSkinEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;


@EventBusSubscriber(value = Dist.CLIENT, modid = ModularGolems.MODID)
public class GolemClientEventListeners {

	@SubscribeEvent
	public static void onRenderStateAttach(RegisterRenderStateModifiersEvent event) {

	}

	@SubscribeEvent
	public static void renderStageEvent(RenderLevelStageEvent.AfterTranslucentBlocks event) {
		BlockOutliner.renderOutline(event.getPoseStack(), event.getLevelRenderState().cameraRenderState.pos);
	}

	@SubscribeEvent
	public static void onHumanoidSkin(HumanoidSkinEvent event) {
		if (event.getStack().isComponentsPatchEmpty()) {
			if (event.getStack().is(Items.ZOMBIE_HEAD)) {
				event.setSkin(MobSkinDispatch.of(EntityType.ZOMBIE));
			}
			if (event.getStack().is(Items.SKELETON_SKULL)) {
				event.setSkin(MobSkinDispatch.of(EntityType.SKELETON));
			}
			if (event.getStack().is(Items.WITHER_SKELETON_SKULL)) {
				event.setSkin(MobSkinDispatch.of(EntityType.WITHER_SKELETON));
			}
			if (event.getStack().is(Items.PIGLIN_HEAD)) {
				event.setSkin(MobSkinDispatch.of(EntityType.PIGLIN));
			}
			return;
		}
		if (event.getStack().is(Items.PLAYER_HEAD)) {
			String name = event.getStack().getHoverName().getString();
			var rl = Identifier.tryParse(name);
			if (rl != null)
				event.setSkin(new SpecialRenderProfile(true, rl));
		}
		if (event.getStack().is(Items.PIGLIN_HEAD)) {
			String name = event.getStack().getHoverName().getString();
			var rl = Identifier.tryParse(name);
			if (rl != null)
				event.setSkin(new SpecialRenderProfile(false, rl));
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

	@SubscribeEvent
	public static void onLayerRender(RenderGuiLayerEvent.Pre event) {
		if (event.getName().equals(VanillaGuiLayers.HOTBAR)) {
			//clearDepth(event.getGuiGraphics());
		}
	}

}
