package dev.xkmc.modulargolems.util;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Renders golem previews in container screens.
 */
public class GolemPreviewUtil {

	public static final int ANCHOR_X = 32;
	public static final int ANCHOR_Y = 80;
	public static final int HALF_W = 29;
	public static final int HEIGHT = 64;
	public static final int FOOT_MARGIN = 8;

	private static final double TRACK_RANGE = 50.0;

	public record Adjustment(int anchorX, int anchorY, int halfWidth, int height, int footMargin) {
		public static Adjustment none() {
			return new Adjustment(0, 0, 0, 0, 0);
		}
	}

	public static void render(GuiGraphics g, int leftPos, int topPos, Entity entity, float scale,
			int mx, int my) {
		render(g, leftPos, topPos, entity, scale, Adjustment.none(), mx, my);
	}

	public static void render(GuiGraphics g, int leftPos, int topPos, Entity entity, float scale,
			Adjustment adjustment, int mx, int my) {
		int x = leftPos + ANCHOR_X + adjustment.anchorX();
		int y = topPos + ANCHOR_Y + adjustment.anchorY();
		renderAt(g, x, y, entity, scale, adjustment, true, mx, my);
	}

	public static void renderAt(GuiGraphics g, int x, int y, Entity entity, float scale, int mx, int my) {
		renderAt(g, x, y, entity, scale, Adjustment.none(), true, mx, my);
	}

	public static void renderAtUnclipped(GuiGraphics g, int x, int y, Entity entity, float scale,
			int mx, int my) {
		renderAt(g, x, y, entity, scale, Adjustment.none(), false, mx, my);
	}

	private static void renderAt(GuiGraphics g, int x, int y, Entity entity, float scale,
			Adjustment adjustment, boolean clip, int mx, int my) {
		if (!(entity instanceof LivingEntity living)) return;
		if (living instanceof AbstractGolemEntity<?, ?> golem) {
			float size = golem.getScale();
			if (size > 1e-4F) scale /= size;
		}
		float ax = (float) Math.atan((x - mx) / TRACK_RANGE);
		float ay = (float) Math.atan((y - 40 - my) / TRACK_RANGE);
		int halfW = HALF_W + adjustment.halfWidth();
		int height = HEIGHT + adjustment.height();
		int foot = FOOT_MARGIN + adjustment.footMargin();
		if (clip) {
			g.enableScissor(x - halfW, y - height, x + halfW, y + foot);
		}
		int sc = Math.max(1, (int) scale);
		// Compensate so the entity's feet land on the anchor y (like 1.20's point-based render)
		// instead of the official method's default centring, which lifts short golems (e.g. the dog).
		float yOffset = (height - foot) / 2.0F / sc - (float) living.getBbHeight() / 2.0F;
		InventoryScreen.renderEntityInInventoryFollowsAngle(g,
				x - halfW, y - height, x + halfW, y + foot,
				sc, yOffset, ax, ay, living);
		if (clip) {
			g.disableScissor();
		}
	}

	private GolemPreviewUtil() {
	}

}
