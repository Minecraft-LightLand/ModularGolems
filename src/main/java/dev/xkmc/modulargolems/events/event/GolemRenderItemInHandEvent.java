package dev.xkmc.modulargolems.events.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class GolemRenderItemInHandEvent extends Event implements ICancellableEvent {

	private final ArmedEntityRenderState state;
	private final ItemStackRenderState item;
	private final ItemStack stack;
	private final HumanoidArm arm;
	private final PoseStack pose;
	private final SubmitNodeCollector col;
	private final int light;

	public GolemRenderItemInHandEvent(ArmedEntityRenderState state, ItemStackRenderState item, ItemStack stack, HumanoidArm arm, PoseStack pose, SubmitNodeCollector col, int light) {
		this.state = state;
		this.item = item;
		this.stack = stack;
		this.arm = arm;
		this.pose = pose;
		this.col = col;
		this.light = light;
	}

	public ArmedEntityRenderState getState() {
		return state;
	}

	public ItemStackRenderState getItem() {
		return item;
	}

	public ItemStack getStack() {
		return stack;
	}

	public HumanoidArm getArm() {
		return arm;
	}

	public PoseStack getPose() {
		return pose;
	}

	public SubmitNodeCollector getCol() {
		return col;
	}

	public int getLight() {
		return light;
	}

}
