package dev.xkmc.modulargolems.content.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;

public interface GolemShoulderPose {

	LinkedHashMap<ResourceLocation, GolemShoulderPose> MAP = new LinkedHashMap<>();

	static void register(ResourceLocation id, GolemShoulderPose pose) {
		synchronized (MAP) {
			MAP.put(id, pose);
		}
	}

	void setup(MetalGolemEntity entity, MetalGolemModel model, ItemStack stack, InteractionHand hand, float pTick);

	void render(MetalGolemEntity entity, MetalGolemModel model, ItemStack stack, InteractionHand hand, PoseStack pose, MultiBufferSource source, float pTick);

}
