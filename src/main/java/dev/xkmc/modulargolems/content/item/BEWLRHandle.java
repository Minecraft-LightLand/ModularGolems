package dev.xkmc.modulargolems.content.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;

public record BEWLRHandle(ItemStack stack, ItemTransforms.TransformType type, PoseStack poseStack,
						  MultiBufferSource bufferSource, int light, int overlay) {
}
