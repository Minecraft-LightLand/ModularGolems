package dev.xkmc.modulargolems.compat.materials.create.arm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ArmRenderer {

	public static void render(ArmState be, float pt, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		ItemStack item = be.heldItem;
		boolean hasItem = !item.isEmpty();
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
		BakedModel bakedModel = itemRenderer.getModel(item, be.level, null, 0);
		boolean isBlockItem = hasItem && item.getItem() instanceof BlockItem && bakedModel.isGui3d();
		VertexConsumer builder = buffer.getBuffer(RenderType.solid());
		PoseStack msLocal = new PoseStack();
		PoseTransformStack msr = TransformStack.of(msLocal);
		float a0 = be.baseAngle;
		float a1 = be.lowerArmAngle - 135.0F;
		float a2 = be.upperArmAngle - 90.0F;
		float a3 = be.headAngle;
		int color = 16777215;

		msr.center();

		renderArm(builder, ms, msLocal, msr, color, a0, a1, a2, a3, hasItem, isBlockItem, light);

		if (hasItem) {
			ms.pushPose();
			float itemScale = isBlockItem ? 0.5F : 0.625F;
			msr.rotateXDegrees(90.0F);
			msLocal.translate(0.0F, isBlockItem ? -0.5625F : -0.625F, 0.0F);
			msLocal.scale(itemScale, itemScale, itemScale);
			ms.last().pose().mul(msLocal.last().pose());
			itemRenderer.render(item, ItemDisplayContext.FIXED, false, ms, buffer, light, overlay, bakedModel);
			ms.popPose();
		}

	}

	private static void renderArm(VertexConsumer builder, PoseStack ms, PoseStack msLocal, TransformStack<?> msr, int color, float baseAngle, float lowerArmAngle, float upperArmAngle, float headAngle, boolean hasItem, boolean isBlockItem, int light) {
		BlockState blockState = AllBlocks.MECHANICAL_ARM.getDefaultState();
		SuperByteBuffer base = CachedBuffers.partial(AllPartialModels.ARM_BASE, blockState).light(light);
		SuperByteBuffer lowerBody = CachedBuffers.partial(AllPartialModels.ARM_LOWER_BODY, blockState).light(light);
		SuperByteBuffer upperBody = CachedBuffers.partial(AllPartialModels.ARM_UPPER_BODY, blockState).light(light);
		SuperByteBuffer claw = CachedBuffers.partial(AllPartialModels.ARM_CLAW_BASE, blockState).light(light);
		SuperByteBuffer upperClawGrip = CachedBuffers.partial(AllPartialModels.ARM_CLAW_GRIP_UPPER, blockState).light(light);
		SuperByteBuffer lowerClawGrip = CachedBuffers.partial(AllPartialModels.ARM_CLAW_GRIP_LOWER, blockState).light(light);
		transformBase(msr, baseAngle);
		(base.transform(msLocal)).renderInto(ms, builder);
		transformLowerArm(msr, lowerArmAngle);
		(lowerBody.color(color).transform(msLocal)).renderInto(ms, builder);
		transformUpperArm(msr, upperArmAngle);
		(upperBody.color(color).transform(msLocal)).renderInto(ms, builder);
		transformHead(msr, headAngle);
		(claw.transform(msLocal)).renderInto(ms, builder);
		for (int flip : Iterate.positiveAndNegative) {
			msLocal.pushPose();
			transformClawHalf(msr, hasItem, isBlockItem, flip);
			(flip > 0 ? lowerClawGrip : upperClawGrip).transform(msLocal).renderInto(ms, builder);
			msLocal.popPose();
		}

	}

	public static void transformClawHalf(TransformStack<?> msr, boolean hasItem, boolean isBlockItem, int flip) {
		msr.translate(0.0F, ((float) (-flip) * (hasItem ? (isBlockItem ? 0.1875F : 0.078125F) : 0.0625F)), -0.375F);
	}

	public static void transformHead(TransformStack<?> msr, float headAngle) {
		msr.translate(0.0F, 0.0F, -0.9375F);
		msr.rotateXDegrees(headAngle - 45.0F);
	}

	public static void transformUpperArm(TransformStack<?> msr, float upperArmAngle) {
		msr.translate(0.0F, 0.0F, -0.875F);
		msr.rotateXDegrees(upperArmAngle - 90.0F);
	}

	public static void transformLowerArm(TransformStack<?> msr, float lowerArmAngle) {
		msr.translate(0.0F, 0.125F, 0.0F);
		msr.rotateXDegrees(lowerArmAngle + 135.0F);
	}

	public static void transformBase(TransformStack<?> msr, float baseAngle) {
		msr.translate(0.0F, 0.25F, 0.0F);
		msr.rotateYDegrees(baseAngle);
	}

}
