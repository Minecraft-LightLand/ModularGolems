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

	/*
	 * Shared preview position, relative to the container origin. Adjust these to move every preview.
	 */

	public static final int ANCHOR_X = 32;
	public static final int ANCHOR_Y = 80;

	/*
	 * Shared preview clipping. The anchor is the feet of the model, so the area is measured upwards
	 * from there with only a small margin below for the feet. The preview is clipped to this rectangle
	 * instead of being scaled down, so oversized golems stay readable and simply get cut off at the
	 * top. Vanilla solved the same class of overflow for the vanilla portrait in MC-85582 by clamping
	 * the scale, which shrinks the model instead. Adjust these to resize the visible area.
	 */

	public static final int HALF_W = 29;
	public static final int HEIGHT = 64;
	public static final int FOOT_MARGIN = 8;

	/**
	 * How far the preview may drift above the anchor before the model turns to follow the cursor.
	 */
	private static final double TRACK_RANGE = 50.0;

	/**
	 * Per screen tweaks applied on top of the shared values above. Zero means follow the shared value,
	 * so a screen only states what it actually changes.
	 */
	public record Adjustment(int anchorX, int anchorY, int halfWidth, int height, int footMargin) {

		public static Adjustment none() {
			return new Adjustment(0, 0, 0, 0, 0);
		}

	}

	/**
	 * Renders a golem preview at the shared anchor with the shared clipping.
	 */
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

	/**
	 * Renders a golem preview at an absolute anchor, for screens not laid out around a container.
	 */
	public static void renderAt(GuiGraphics g, int x, int y, Entity entity, float scale, int mx, int my) {
		renderAt(g, x, y, entity, scale, Adjustment.none(), true, mx, my);
	}

	/**
	 * Same as {@link #renderAt} but without clipping, for screens that have no preview area to stay
	 * inside.
	 */
	public static void renderAtUnclipped(GuiGraphics g, int x, int y, Entity entity, float scale,
			int mx, int my) {
		renderAt(g, x, y, entity, scale, Adjustment.none(), false, mx, my);
	}

	private static void renderAt(GuiGraphics g, int x, int y, Entity entity, float scale,
			Adjustment adjustment, boolean clip, int mx, int my) {
		if (!(entity instanceof LivingEntity living)) return;
		// The golem renderer scales the model by the entity's own size, on top of the scale passed in here.
		// Offscreen previews report a size of 1 and need no compensation, but the equipment screen renders the
		// real world golem, whose size would otherwise blow the preview up for large golems. Dividing it back
		// out keeps every preview at its declared size regardless of how big the golem actually is.
		if (living instanceof AbstractGolemEntity<?, ?> golem) {
			float size = golem.getScale();
			if (size > 1e-4F) scale /= size;
		}
		float ax = (float) Math.atan((x - mx) / TRACK_RANGE);
		float ay = (float) Math.atan((y - 40 - my) / TRACK_RANGE);
		if (clip) {
			int halfW = HALF_W + adjustment.halfWidth();
			int height = HEIGHT + adjustment.height();
			int foot = FOOT_MARGIN + adjustment.footMargin();
			g.enableScissor(x - halfW, y - height, x + halfW, y + foot);
		}
		InventoryScreen.renderEntityInInventoryFollowsAngle(g, x, y, (int) scale, ax, ay, living);
		if (clip) {
			g.disableScissor();
		}
	}

	private GolemPreviewUtil() {
	}

}
