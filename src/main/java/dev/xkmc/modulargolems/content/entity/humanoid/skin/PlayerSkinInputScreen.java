package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.menu.registry.OpenEquipmentMenuToServer;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class PlayerSkinInputScreen extends Screen {

	private static final Component TITLE = Component.translatable(ModularGolems.MODID + ".gui.player_skin");
	private static final Component CONFIRM = Component.translatable(ModularGolems.MODID + ".gui.player_skin.confirm");
	private static final Component CANCEL = Component.translatable(ModularGolems.MODID + ".gui.player_skin.cancel");

	private final HumanoidGolemEntity golem;
	private final String prev;
	private EditBox input;

	public PlayerSkinInputScreen(HumanoidGolemEntity golem) {
		super(TITLE);
		this.golem = golem;
		prev = golem.getPlayerSkin();
	}

	@Override
	protected void init() {
		int cx = width / 2;
		int cy = height / 2;

		input = new EditBox(font, cx - 120, cy - 20, 240, 20, Component.empty());
		input.setMaxLength(256);
		input.setValue(golem.getPlayerSkin());
		addRenderableWidget(input);
		setInitialFocus(input);

		addRenderableWidget(Button.builder(CONFIRM, e -> confirm())
				.bounds(cx - 120, cy + 10, 116, 20).build());
		addRenderableWidget(Button.builder(CANCEL, e -> cancel())
				.bounds(cx + 4, cy + 10, 116, 20).build());
	}

	private void cancel() {
		ModularGolems.HANDLER.toServer(SetPlayerSkinToServer.of(golem.getId(), prev));
		golem.setPlayerSkin(prev);
		onClose();
	}

	private void apply() {
		String value = input.getValue().trim().replace('\\', '/');
		ModularGolems.HANDLER.toServer(SetPlayerSkinToServer.of(golem.getId(), value));
		golem.setPlayerSkin(value);
	}

	private void confirm() {
		apply();
		onClose();
	}

	@Override
	public void onClose() {
		ModularGolems.HANDLER.toServer(new OpenEquipmentMenuToServer(golem.getUUID(), OpenEquipmentMenuToServer.Type.EQUIPMENT));
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float ptick) {
		renderBackground(g);
		super.render(g, mx, my, ptick);
		g.drawCenteredString(font, TITLE, width / 2, height / 2 - 40, 0xFFFFFF);
		g.drawCenteredString(font, Component.translatable(ModularGolems.MODID + ".gui.player_skin.hint"), width / 2, height / 2 + 40, 0x808080);
		renderPreview(g, mx, my);
	}

	private void renderPreview(GuiGraphics g, int mx, int my) {
		int w = 40;
		int h = 40;

		int x = width / 2 - 120 - w / 2;
		int y = height / 2 + h / 2;
		double lx = x - mx;
		double ly = y - h - my;
		int scale = 24;
		float ax = (float) Math.atan(lx / 50.0);
		float ay = (float) Math.atan(ly / 50.0);
		scale = (int) (scale / golem.getScale());
		InventoryScreen.renderEntityInInventoryFollowsAngle(g, x, y, scale, ax, ay, golem);
	}

	public boolean keyPressed(int key, int p_97879_, int p_97880_) {
		if (key == 256) {
			onClose();
			return true;
		}
		if (key == GLFW.GLFW_KEY_ENTER) {
			apply();
			return true;
		}
		return !this.input.keyPressed(key, p_97879_, p_97880_) && !this.input.canConsumeInput() ?
				super.keyPressed(key, p_97879_, p_97880_) : true;
	}

}
