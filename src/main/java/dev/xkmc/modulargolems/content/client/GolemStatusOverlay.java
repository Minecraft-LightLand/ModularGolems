package dev.xkmc.modulargolems.content.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2library.base.menu.OverlayUtils;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.WandItem;
import dev.xkmc.modulargolems.init.data.LangData;
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
		OverlayUtils util = new OverlayUtils(screenWidth, screenHeight);
		List<Component> text = new ArrayList<>();
		text.add(golem.getName());
		if (golem.getMode() == 0) {
			text.add(LangData.MODE_FOLLOWING.get());
		} else {
			var p = golem.getGuardPos();
			text.add(LangData.MODE_GUARDING.get(p.getX(), p.getY(), p.getZ()));
		}
		golem.getModifiers().forEach((k, v) -> text.add(k.getTooltip(v)));
		util.renderLongText(gui, poseStack, text);
	}

}
