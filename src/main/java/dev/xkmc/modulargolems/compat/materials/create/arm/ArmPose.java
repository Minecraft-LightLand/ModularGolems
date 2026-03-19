package dev.xkmc.modulargolems.compat.materials.create.arm;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.client.pose.GolemShoulderPose;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class ArmPose implements GolemShoulderPose {

	@Override
	public void setup(MetalGolemEntity entity, MetalGolemModel model, ItemStack stack, InteractionHand hand, float pTick) {

	}

	@Override
	public void render(MetalGolemEntity entity, MetalGolemModel model, ItemStack stack, InteractionHand hand, PoseStack pose, MultiBufferSource source, int light, float pTick) {
		int s0 = hand == InteractionHand.MAIN_HAND ? 1 : -1;
		var src = new Vec3(s0 * 7f / 16, 31f / 16, 0);
		var def = ArmAngleTarget.NO_TARGET;
		ArmState state;
		long last = stack.getOrCreateTag().getLong("FixAction");
		float speed = Mth.clamp(stack.getOrCreateTag().getFloat("FixSpeed"), 0.25f, 4f);
		long current = entity.level().getGameTime();
		float time = ((current - last) + pTick) * speed;
		if (last > current || time > 80) {
			state = new ArmState(entity.level(), ItemStack.EMPTY, 0, def, def);
		} else if (time < 40) {
			float progress = 1 - Math.abs(time / 20 - 1);
			var dst = new Vec3(0, 18 / 16f, 20 / 16f);
			var target = new ArmAngleTarget(src, dst, Direction.SOUTH, false);
			target.headAngle += 180;
			var diff = ArmState.getShortestAngleDiff(def.baseAngle, target.baseAngle);
			diff = diff > 0 ? diff - 360 : diff + 360;
			target.baseAngle = def.baseAngle + diff;
			ItemStack held = time > 20 ? Items.IRON_INGOT.getDefaultInstance() : ItemStack.EMPTY;
			state = new ArmState(entity.level(), held, progress, def, target);
		} else {
			float progress = 1 - Math.abs((time - 40) / 20 - 1);
			var dst = new Vec3(0, 18 / 16f, -20 / 16f);
			var target = new ArmAngleTarget(src, dst, Direction.NORTH, false);
			target.headAngle += 180;
			var diff = ArmState.getShortestAngleDiff(def.baseAngle, target.baseAngle);
			target.baseAngle = def.baseAngle + diff;
			ItemStack held = time < 60 ? Items.IRON_INGOT.getDefaultInstance() : ItemStack.EMPTY;
			state = new ArmState(entity.level(), held, progress, def, target);
		}

		pose.pushPose();
		pose.scale(-1, -1, 1);
		pose.translate(src.x - 0.5f, src.y - 2f, src.z - 8.5f / 16);

		ArmRenderer.render(state, pTick, pose, source, light, OverlayTexture.NO_OVERLAY);
		pose.popPose();
	}

}
