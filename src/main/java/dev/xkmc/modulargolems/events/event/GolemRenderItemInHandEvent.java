package dev.xkmc.modulargolems.events.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class GolemRenderItemInHandEvent extends Event implements ICancellableEvent {

	public final LivingEntity entity;
	public final ItemStack stack;
	public final ItemDisplayContext ctx;
	public final HumanoidArm arm;
	public final PoseStack pose;
	public final MultiBufferSource buffer;
	public final int light;
	public final ItemInHandRenderer renderer;

	public GolemRenderItemInHandEvent(LivingEntity entity, ItemStack stack, ItemDisplayContext ctx, HumanoidArm arm, PoseStack pose, MultiBufferSource buffer, int light, ItemInHandRenderer renderer) {
		this.entity = entity;
		this.stack = stack;
		this.ctx = ctx;
		this.arm = arm;
		this.pose = pose;
		this.buffer = buffer;
		this.light = light;
		this.renderer = renderer;
	}

}
