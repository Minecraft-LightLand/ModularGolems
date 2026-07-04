package dev.xkmc.modulargolems.content.entity.humanoid.skin;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.mob.MobSkinDispatch;
import dev.xkmc.modulargolems.content.menu.registry.OpenEquipmentMenuToServer;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class PlayerSkinInputScreen extends Screen {

	private static final Component TITLE = Component.translatable(ModularGolems.MODID + ".gui.player_skin");
	private static final Component CONFIRM = Component.translatable(ModularGolems.MODID + ".gui.player_skin.confirm");
	private static final Component CANCEL = Component.translatable(ModularGolems.MODID + ".gui.player_skin.cancel");

	private static final List<List<EntityType<?>>> PRESET_GROUPS = List.of(
			List.of(EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED),
			List.of(EntityType.SKELETON, EntityType.WITHER_SKELETON, EntityType.STRAY, EntityType.BOGGED),
			List.of(EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.ZOMBIFIED_PIGLIN)
	);

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

		int presetY = cy + 65;
		int btnSize = 22;
		int gap = 4;
		int groupGap = 6;

		int groupCount = PRESET_GROUPS.size();
		int tabCount = 0;
		for (var e : PRESET_GROUPS) tabCount += e.size();

		int totalWidth = (btnSize + gap) * tabCount + groupCount * groupGap - groupGap - gap;
		int x = cx - totalWidth / 2;
		for (int g = 0; g < PRESET_GROUPS.size(); g++) {
			var group = PRESET_GROUPS.get(g);
			for (int i = 0; i < group.size(); i++) {
				var type = group.get(i);
				int bx = x;
				addRenderableWidget(new PresetButton(bx, presetY, btnSize, btnSize, type, b -> {
					input.setValue(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
					apply();
				}));
				x += btnSize;
				if (i < group.size() - 1) x += gap;
				else if (g < PRESET_GROUPS.size() - 1) x += groupGap;
			}
		}
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
	public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float a) {
		super.extractRenderState(g, mx, my, a);
		g.centeredText(font, TITLE, width / 2, height / 2 - 40, 0xFFFFFF);
		g.centeredText(font, Component.translatable(ModularGolems.MODID + ".gui.player_skin.hint"), width / 2, height / 2 + 40, 0x808080);
		renderPreview(g, mx, my);
	}

	private void renderPreview(GuiGraphicsExtractor g, int mx, int my) {
		if (golem == null) return;
		int w = 40;
		int h = 40;

		int x = width / 2 - 120 - w / 2;
		int y = height / 2 + h / 2;
		double lx = x - mx;
		double ly = y - 40 - my;
		float scale = 24;
		float ax = (float) Math.atan(lx / 50.0);
		float ay = (float) Math.atan(ly / 50.0);
		scale = scale / golem.getScale();
		InventoryScreen.renderEntityInInventoryFollowsAngle(g,
				x - 30, y - 70, x + 30, y + 20,
				20, 1f / scale, ax, ay, golem);

	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.key() == 256) {
			onClose();
			return true;
		}
		if (event.key() == GLFW.GLFW_KEY_ENTER) {
			apply();
			return true;
		}
		return this.input.keyPressed(event) || this.input.canConsumeInput() || super.keyPressed(event);
	}

	private static final class PresetButton extends Button {

		private final EntityType<?> preset;

		public PresetButton(int x, int y, int w, int h, EntityType<?> preset, OnPress onPress) {
			super(x, y, w, h, Component.empty(), onPress, DEFAULT_NARRATION);
			this.preset = preset;
			setTooltip(Tooltip.create(preset.getDescription()));
		}

		@Override
		public MutableComponent createNarrationMessage() {
			return Component.translatable("gui.narrate.button", preset.getDescription());
		}

		@Override
		protected void extractContents(GuiGraphicsExtractor g, int mx, int my, float pt) {
			this.extractDefaultSprite(g);
			var level = Minecraft.getInstance().level;
			if (level == null) return;
			var e = MobSkinDispatch.of(preset);
			if (e == null) return;
			e.renderSkullIcon(level, g, pt, getX() + getWidth() / 2, getY() + getHeight() / 2);
		}

	}

}
