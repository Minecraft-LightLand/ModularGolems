package dev.xkmc.modulargolems.content.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2library.base.overlay.OverlayUtils;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.WandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.ArrayList;
import java.util.List;

public class GolemStatusOverlay extends GuiComponent implements IGuiOverlay {

	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;
		if (!(player.getMainHandItem().getItem() instanceof WandItem)) return;
		var hit = Minecraft.getInstance().hitResult;
		if (!(hit instanceof EntityHitResult entityHit)) return;
		if (!(entityHit.getEntity() instanceof AbstractGolemEntity<?, ?> golem)) return;
		gui.setupOverlayRenderState(true, false);
		List<Component> text = new ArrayList<>();
		text.add(golem.getName());
		text.add(golem.getMode().getDesc(golem));
		golem.getModifiers().forEach((k, v) -> text.add(k.getTooltip(v)));
		OverlayUtils util = new OverlayUtils(screenWidth, screenHeight);
		util.renderLongText(gui, poseStack, text);
	}

}
